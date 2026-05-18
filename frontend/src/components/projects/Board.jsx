import { useState, useEffect } from 'react';
import { useProject } from '../../context/ProjectContext';

import styles from './Board.module.css';
import { Button } from '../../components/ui/elements/Button';
import { Task } from './Task';


export const Board = ({onCreate, id, name, setName, handleCreateBoard, title, style, onAddTask}) => {

    const { getTasks } = useProject()

    const [tasks, setTasks] = useState()

    const fetchTasks = async () =>
    {
        if(onCreate) return;
        try
        {
            setTasks(await getTasks(id))
        }
        finally{}
    }

    const handleCreateTask = async(e) =>
    {
        e.preventDefault()
        await onAddTask()
        await fetchTasks()
    }

    useEffect(() =>{
        fetchTasks()
    }, [])

    return (
    <div className={styles.board} style={style} onClick={(e)=>{e.stopPropagation()}}>
        <div className={styles.boardHeader}>
            {!onCreate &&
            <>
                <div>
                    <h2 className={styles.boardTitle}>
                        {title}
                    </h2>

                    <span className={styles.taskCount}>
                        {tasks?.length} tasks
                    </span>
                </div>

                <Button className={styles.boardMenu}>
                    •••
                </Button>
            </>
            }
            {onCreate &&
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
            }
        </div>
        <Button className={styles.addTaskButton} onClick={(e) => {handleCreateTask(e)}}>
            + Добавить задачу
        </Button>   
        <div className={styles.tasks}>
            {!onCreate &&
            <>
                {tasks?.map((task) => (
                    <Task
                        key={task.id}
                        title={task.title}
                        description={task.description}
                    />
                ))}
            </>
            }
        </div>
    </div>
    );
}