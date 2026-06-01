import { useProject } from '../../context/ProjectContext';
import { useState, useEffect } from 'react';
import { useParams } from "react-router-dom";

import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/elements/Button';

import modal from '../../components/ui/Modal.module.css';
import styles from './ProjectPage.module.css';

import {Board} from '../../components/projects/Board';
import { boardService } from '../../api';

export const ProjectsPage = () => {

    const { id } = useParams();

    const { boards, currentProject, createBoard, createTask } = useProject();

    const [loading, setLoading] = useState(false);

    const [boardName, setBoardName] = useState('')
    const [currentBoardId, setCurrentBoardId] = useState("")

    const [taskTitle, setTaskTitle] = useState('')
    const [taskDiscription, setTaskDiscription] = useState('')

    const [createBoardModalIsOpen, setCreateBoardModalIsOpen] = useState(false);
    const [createTaskModalIsOpen, setCreateTaskModalIsOpen] = useState(false);

    const handleCreateBoard = async (e) => {
        e.preventDefault()
        setLoading(true)
        try{
            await createBoard(boardName)
        }
        finally{
            setCreateBoardModalIsOpen(false)
            setBoardName('')
            setLoading(false)
        }
    }

    const handleCreateTask = async (e) => {
        e.preventDefault()
        setLoading(true)
        try{
            await createTask(currentBoardId, taskTitle, taskDiscription, 0)
        }
        finally{
            setCreateTaskModalIsOpen(false)
            setCurrentBoardId('')
            setTaskTitle('')
            setTaskDiscription('')
            setLoading(false)
        }
    }

    return (
        <div className={styles.page} onClick={()=>setCreateBoardModalIsOpen(false)}>
            <div className={styles.header}>
                <div>
                    <h1 className={styles.projectTitle}>
                        {currentProject?.name}
                    </h1>

                    <p className={styles.projectSubtitle}>
                        Управляйте досками и задачами
                    </p>
                </div>

                <Button variant="primary" className={styles.addBoardButton} onClick={(e)=>{
                    e.stopPropagation();
                    setCreateBoardModalIsOpen(true)
                }}>
                    + Добавить доску
                </Button>
            </div>

            <div className={styles.boards}>
                {boards?.map((board) => (
                    <Board
                        onCreat={false}
                        key={board.id}
                        id={board.id}
                        title={board.name}
                        onAddTask={()=>{
                            setCurrentBoardId(board.id)
                            setCreateTaskModalIsOpen(true)
                        }}    
                    /> 
                ))}
                <Board
                    onCreate={true}
                    style={createBoardModalIsOpen ? {} : {display: "none"}}
                    handleCreateBoard={(e)=>handleCreateBoard(e)}
                    name={boardName}
                    setName={setBoardName}
                />
            </div>

            <Modal isOpen={createTaskModalIsOpen} onClose={() => {
                    setCreateTaskModalIsOpen(false);
                    setCurrentBoardId('')
                    setTaskTitle('')
                    setTaskDiscription('')
                }} title="Добавить задачу">
                <form className={modal.form} onSubmit={(e) => handleCreateTask(e)}>
                    <label className={modal.label}>
                        Заголовок
                    </label>
                    <div className={modal.inputWrapper}>
                        <input
                            value={taskTitle}
                            onChange={(e) => setTaskTitle(e.target.value)}
                            placeholder="Заголовок задачи"
                            className={modal.input}
                        />
                    </div>
                    <label className={modal.label}>
                        Описание
                    </label>
                    <div className={modal.inputWrapper}>
                        <textarea
                            value={taskDiscription}
                            onChange={(e) => setTaskDiscription(e.target.value)}
                            placeholder="Описание задачи"
                            className={modal.input}
                        />
                    </div>
                    <Button
                        variant="primary"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Добавление...' : 'Добавить'}
                    </Button>
                </form>
            </Modal>
        </div>
    );
};