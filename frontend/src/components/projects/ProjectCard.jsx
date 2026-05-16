import styles from "./ProjectCard.module.css"

import { Button } from '../ui/elements/Button';

export const ProjectCard = ({id, name, OnOpen, onDelete}) => {
    return(
        <div className={styles.projectCard}>
            <div className={styles.projectInfo}>
                <h3 className={styles.projectName}>
                    {name}
                </h3>

                <span className={styles.projectId}>
                    ID: {id}
                </span>
            </div>

            <div className={styles.actions}>
                <Button
                    onClick={OnOpen}
                >
                    Открыть
                </Button>

                <Button
                    variant="delete"
                    onClick={onDelete}
                >
                    Удалить
                </Button>
            </div>
        </div>
    );
}