import { useProject } from '../../context/ProjectContext';
import { useState } from 'react';

import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/elements/Button';

import modal from '../../components/ui/Modal.module.css';
import styles from './ProjectPage.module.css';

export const ProjectsPage = () => {
    const {
        projects,
    } = useProject();

    const [projectName, setProjectName] = useState('');
    const [loading, setLoading] = useState(false);

    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [selectedProjectId, setSelectedProjectId] = useState(null);

    return (
        <div className={styles.page}>
            sss
        </div>
    );
};