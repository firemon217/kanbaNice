import { useState, useEffect } from 'react';
import { useProject } from '../../context/ProjectContext';

import styles from './Board.module.css';
import { Button } from '../../components/ui/elements/Button';
import { Task } from './Task';


export const Board = ({onCreate, id, name, setName, handleCreateBoard, title, style, onAddTask}) => {

    const {   
        tasksMap,      
        fetchTasks,
        deleteBoard,
        updateBoard
    } = useProject()

    const tasks = tasksMap[id] || [];

    const [isEditingTitle, setIsEditingTitle] = useState(false);
    const [editTitleValue, setEditTitleValue] = useState(title);

    const handleDeleteClick = async () => {
        if (window.confirm('Вы уверены, что хотите удалить эту доску? Все задачи будут удалены.')) {
            await deleteBoard(id);
        }
    };

    const handleUpdateTitle = async () => {
        if (editTitleValue.trim() === '') return;
        
        const success = await updateBoard(id, editTitleValue);
        if (success) {
            setIsEditingTitle(false);
        }
    };

    useEffect(() => {
        if (!onCreate && id) {
            fetchTasks(id);
        }
    }, [id, onCreate]);

    if (onCreate) {
        return (
            <div className={styles.board} style={style} onClick={(e)=>{e.stopPropagation()}}>
                <form 
                    onSubmit={handleCreateBoard}
                    className={styles.createBoard}
                >
                    <h2 className={styles.boardTitle}>
                        Введите название доски
                    </h2>
                    <input 
                        className={styles.input} 
                        value={name} 
                        onChange={(e)=>setName(e.target.value)}
                        placeholder="Название доски"
                    />
                    <Button className={styles.addTaskButton} type="submit">
                        + Создать доску
                    </Button>
                </form>
            </div>
        );
    }

    return (
        <div className={styles.board} style={style} onClick={(e)=>{e.stopPropagation()}}>
            <div className={styles.boardHeader}>
                <div className={styles.headerContent}>
                    {isEditingTitle ? (
                        <div className={styles.editTitleContainer}>
                            <input 
                                className={styles.input}
                                value={editTitleValue}
                                onChange={(e) => setEditTitleValue(e.target.value)}
                                autoFocus
                            />
                            <div className={styles.editActions}>
                                <Button onClick={handleUpdateTitle} className={styles.saveBtn}>Подтвердить</Button>
                                <Button onClick={()=>{
                                    setEditTitleValue(title);
                                    setIsEditingTitle(false);}} 
                                    className={styles.cancelBtn}>Отменить</Button>
                            </div>
                        </div>
                    ) : (
                        <>
                            <h2 
                                className={styles.boardTitle} 
                                title="Двойной клик для редактирования"
                            >
                                {title}
                            </h2>
                            <span className={styles.taskCount}>
                                {tasks?.length || 0} задач
                            </span>
                        </>
                    )}
                </div>

                <div className={styles.boardControls}>
                    {!isEditingTitle && (
                        <>
                            <Button 
                                className={styles.editBoardBtn} 
                                onClick={() => setIsEditingTitle(true)}
                                title="Переименовать доску"
                            >
                                ✏️
                            </Button>
                            <Button 
                                className={styles.deleteBoardBtn} 
                                onClick={handleDeleteClick}
                                title="Удалить доску"
                            >
                                🗑️
                            </Button>
                        </>
                    )}
                </div>
            </div>

            <Button className={styles.addTaskButton} onClick={(e) => {onAddTask(e)}}>
                + Добавить задачу
            </Button>  
            
            <div className={styles.tasks}>
                {tasks?.map((task) => (
                    <Task
                        key={task.id}
                        id={task.id}
                        boardId={id}
                        title={task.title}
                        description={task.description}
                        status={task.status}
                    />
                ))}
            </div>
        </div>
    );
}