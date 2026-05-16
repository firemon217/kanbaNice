import { useProject } from '../../context/ProjectContext';
import { useState } from 'react';

import { ProjectCard } from '../../components/projects/ProjectCard'; 

import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/elements/Button';

import modal from '../../components/ui/Modal.module.css';
import styles from './ProjectsPage.module.css';

export const ProjectsPage = () => {
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

            <div className={styles.header}>
                <h1 className={styles.title}>Проекты</h1>
            </div>

            <div className={styles.content}>

                <div className={styles.createBlock}>
                    <h2 className={styles.blockTitle}>
                        Создать проект
                    </h2>

                    <form
                        className={styles.form}
                        onSubmit={handleCreateProject}
                    >
                        <input
                            type="text"
                            placeholder="Название проекта"
                            value={projectName}
                            onChange={(e) => setProjectName(e.target.value)}
                            className={styles.input}
                        />

                        <Button
                            type="submit"
                            disabled={loading}
                        >
                            {loading ? 'Создание...' : 'Создать'}
                        </Button>
                    </form>
                </div>

                <div className={styles.projectsBlock}>
                    <h2 className={styles.blockTitle}>
                        Список проектов
                    </h2>

                    {projects?.length === 0 ? (
                        <div className={styles.empty}>
                            Проектов пока нет
                        </div>
                    ) : (
                        <div className={styles.projectsList}>
                            {projects?.map((project) => (
                                <ProjectCard key={project.id} id={project.is} name={project.name} OnOpen={() => chooseCurrentProject(project.id)} onDelete={() => {
                                    setSelectedProjectId(project.id);
                                    setDeleteModalOpen(true);
                                }}>
                                </ProjectCard>
                            ))}
                        </div>
                    )}
                </div>

            </div>

            <Modal
                isOpen={deleteModalOpen}
                onClose={() => {
                    setDeleteModalOpen(false);
                }}
                title="Удалить проект"
            >
                <form
                    className={modal.form}
                    onSubmit={handleDeleteProject}
                >
                    <div className={modal.label}>
                        Вы уверены, что хотите удалить проект?
                    </div>

                    <Button
                        variant="delete"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Удаление...' : 'Удалить'}
                    </Button>
                </form>
            </Modal>

            <Modal
                isOpen={deleteModalOpen}
                onClose={() => {
                    setDeleteModalOpen(false);
                }}
                title="Удалить проект"
            >
                <form
                    className={modal.form}
                    onSubmit={handleDeleteProject}
                >
                    <div className={modal.label}>
                        Вы уверены, что хотите удалить проект?
                    </div>

                    <Button
                        variant="delete"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Удаление...' : 'Удалить'}
                    </Button>
                </form>
            </Modal>

        </div>
    );
};