import { useState } from 'react';

import styles from './Board.module.css';
import { Button } from '../../components/ui/elements/Button';
import { Task } from './Task';


export const Board = ({title, tasks}) => {

    return (
    <div className={styles.board}>
        <div className={styles.boardHeader}>
            <div>
                <h2 className={styles.boardTitle}>
                    {title}
                </h2>

                <span className={styles.taskCount}>
                    {tasks.length} tasks
                </span>
            </div>

            <Button className={styles.boardMenu}>
                •••
            </Button>
        </div>

        <div className={styles.tasks}>
            {tasks.map((task) => (
                <Task
                    key={task.id}
                    title={task.title}
                    description={task.description}
                />
            ))}

            <Button className={styles.addTaskButton}>
                + Add task
            </Button>
        </div>
    </div>
    );
}