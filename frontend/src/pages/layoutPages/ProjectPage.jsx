import { useProject } from '../../context/ProjectContext';
import { useState, useEffect } from 'react';
import { useParams } from "react-router-dom";

import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/elements/Button';

import modal from '../../components/ui/Modal.module.css';
import styles from './ProjectPage.module.css';

import {Board} from '../../components/projects/Board';

export const ProjectsPage = () => {

    const { id } = useParams();

    const { currentProject } = useProject();

    return (
        <div className={styles.page}>
            <div className={styles.header}>
                <div>
                    <h1 className={styles.projectTitle}>
                        {currentProject.data.name}
                    </h1>

                    <p className={styles.projectSubtitle}>
                        Manage boards and tasks
                    </p>
                </div>

                <Button variant="primary" className={styles.addBoardButton}>
                    + Add board
                </Button>
            </div>

            <div className={styles.boards}>
                {/* {boards.map((board) => (
                    <Board
                        key={board.id}
                        title={board.title}
                        tasks={board.tasks}
                    /> 
                ))} */}
            </div>
        </div>
    );
};