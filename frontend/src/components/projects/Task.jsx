import { useProject } from '../../context/ProjectContext';
import { useState } from 'react';
import { Button } from '../../components/ui/elements/Button';

import styles from './Task.module.css';

export const Task = ({id, boardId, title, description, status}) => {

    const { deleteTask, updateTask } = useProject();

    const [editTitle, setEditTitle] = useState(title);
    const [editDescription, setEditDescription] = useState(description);
    const [isEditing, setIsEditing] = useState(false)

    const handleDelete = async () => {
        if (window.confirm('Удалить задачу?')) {
            await deleteTask(boardId, id);
        }
    };

    const handleToggleStatus = async () => {
        const newStatus = status === "TODO" ? "DONE" : "TODO";
        await updateTask(boardId, id, title, description, newStatus);
    };

    const handleUpdate = async () => {
        await updateTask(boardId, id, editTitle, editDescription, status);
        setIsEditing(false);
    };

    const statusParser = (status) => {
        switch (status)
        {
            case "TODO": return "В работе"
            case "DONE": return "Готово"
            default: return status
        }
    }
        
    if (isEditing) {
        return (
            <div className={styles.taskCard}>
                <input
                    className={styles.editInput}
                    value={editTitle}
                    onChange={(e) => setEditTitle(e.target.value)}
                    placeholder="Заголовок"
                    autoFocus
                />
                <textarea
                    className={styles.editTextarea}
                    value={editDescription}
                    onChange={(e) => setEditDescription(e.target.value)}
                    placeholder="Описание"
                />
                
                <div className={styles.taskFooter}>
                    <div className={styles.editActions}>
                        <Button onClick={handleUpdate} className={styles.saveBtn}>Сохранить</Button>
                        <Button onClick={() => {
                            setEditTitle(title);
                            setEditDescription(description);
                            setIsEditing(false);}} className={styles.cancelBtn}
                            >
                                Отмена
                        </Button>
                    </div>
                </div>
            </div>
        );
    }

    return (
        <div className={styles.taskCard}>
            <h3 className={styles.taskTitle}>
                {title}
            </h3>

            <p className={styles.taskDescription}>
                {description}
            </p>

            <div className={styles.taskFooter}>
                <span className={styles.taskTag}>
                    {statusParser(status)}
                </span>
                
                <div className={styles.actions}>
                    <button onClick={handleToggleStatus} title="Переключить статус">
                        🔄
                    </button>
                    <button onClick={() => setIsEditing(true)} title="Редактировать">
                        ✏️
                    </button>
                    <button onClick={handleDelete} title="Удалить" style={{ color: 'red' }}>
                        🗑️
                    </button>
                </div>
            </div>
        </div>
    );
}