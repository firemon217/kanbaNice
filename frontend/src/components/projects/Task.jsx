import styles from './Task.module.css';

export const Task = ({title, description}) => {

    return (
        <div
            className={styles.taskCard}
        >
            <h3 className={styles.taskTitle}>
                {title}
            </h3>

            <p className={styles.taskDescription}>
                {description}
            </p>

            <div className={styles.taskFooter}>
                <span className={styles.taskTag}>
                    Task
                </span>
            </div>
        </div>
    );
}