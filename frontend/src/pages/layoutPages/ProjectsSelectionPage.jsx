import { useProject } from '../../context/ProjectContext';
import { useCompany } from '../../context/CompanyContext';
import { useState } from 'react';

import { ProjectCard } from '../../components/projects/ProjectCard'; 
import { CompanyCoworkerCard } from '../../components/company/Company_CoworkerCard';

import { Modal } from '../../components/ui/Modal';
import { Button } from '../../components/ui/elements/Button';
import toast from 'react-hot-toast';

import modal from '../../components/ui/Modal.module.css';
import styles from './ProjectsSelectionPage.module.css';

export const ProjectsSelectionPage = () => {

    const {
        company
    } = useCompany();

    const {
        projects,
        createProjects,
        deleteProject,
        addWorkerInProject,
        chooseCurrentProject,
    } = useProject();

    const [projectName, setProjectName] = useState('');
    const [loading, setLoading] = useState(false);

    const [selectedProjectId, setSelectedProjectId] = useState('');
    
    const [deleteModalIsOpen, setDeleteModalIsOpen] = useState(false);
    const [addWorkerModalIsOpen, setAddWorkerModalIsOpen] = useState(false);
 
    const handleCreateProject = async (e) => {
        e.preventDefault();
        setLoading(true);
        try{
            if (!projectName.trim()) 
            {
                toast.error("Введите имя проекта");
                throw Error;
            }
            const success = await createProjects(projectName);
        }
        finally{
            setProjectName('');
            setLoading(false);
        }
    };

    const handleDeleteProject = async (e) => {
        e.preventDefault();
        setLoading(true);
        try{    
            await deleteProject(selectedProjectId);
        }
        finally {
            setDeleteModalIsOpen(false);
            setSelectedProjectId('');
            setLoading(false);
        }
    };
    
    const handleAddWorkerInProject = async (e, id) => {
        e.preventDefault();
        setLoading(true);
        try{
            await addWorkerInProject(selectedProjectId, id);
        }
        finally {
            setAddWorkerModalIsOpen(false);
            setSelectedProjectId('');
            setLoading(false);
        }
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
                                <ProjectCard key={project.id} id={project.is} name={project.name} 
                                onAddWorker={() => {
                                    setSelectedProjectId(project.id);
                                    setAddWorkerModalIsOpen(true)
                                }} 
                                OnOpen={() => chooseCurrentProject(project.id)} onDelete={() => {
                                    setSelectedProjectId(project.id);
                                    setDeleteModalIsOpen(true);
                                }}>
                                </ProjectCard>
                            ))}
                        </div>
                    )}
                </div>

            </div>

            <Modal
                isOpen={deleteModalIsOpen}
                onClose={() => {
                    setDeleteModalIsOpen(false);
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
                isOpen={addWorkerModalIsOpen}
                onClose={() => {
                    setAddWorkerModalIsOpen(false);
                }}
                title="Кого добавить в проект?"
            >
                <form
                    className={modal.form}
                    onSubmit={() => handleAddWorkerInProject(e)}
                >
                    {company?.users.map((user)=>{
                        return (
                        <CompanyCoworkerCard key={user.id} name={user.name} email={user.email} onClick={(e) => {
                            handleAddWorkerInProject(e, user.id)
                        }}>
                        </CompanyCoworkerCard>
                        )
                    })
                    }
                </form>
            </Modal>

        </div>
    );
};