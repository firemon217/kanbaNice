import { useProject } from '../../context/ProjectContext';
import { useState } from 'react';

import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/elements/Button';

import modal from '../../components/ui/Modal.module.css';
import styles from './ProjectPage.module.css';

export const ProjectsSelectionPage = () => {
    const {
        projects,
        createProjects,
        deleteProject,
        chooseCurrentProject,
    } = useProject();

    const [projectName, setProjectName] = useState('');
    const [loading, setLoading] = useState(false);

    const [deleteModalOpen, setDeleteModalOpen] = useState(false);
    const [selectedProjectId, setSelectedProjectId] = useState(null);

    const handleCreateProject = async (e) => {
        e.preventDefault();

        if (!projectName.trim()) return;

        setLoading(true);

        const success = await createProjects(projectName);

        if (success) {
            setProjectName('');
        }

        setLoading(false);
    };

    const handleDeleteProject = async (e) => {
        e.preventDefault();

        setLoading(true);

        const success = await deleteProject(selectedProjectId);

        if (success) {
            setDeleteModalOpen(false);
            setSelectedProjectId(null);
        }

        setLoading(false);
    };

    return (
        <div className={styles.page}>


        </div>
    );
};