import { useUser } from '../../context/UserContext';
import { useCompany } from '../../context/CompanyContext';
import { useEffect, useState } from 'react';
import { CompanyProjectCard } from '../../components/company/Company_ProjectCard';
import { CompanyCoworkerCard } from '../../components/company/Company_CoworkerCard';

import { Modal } from '../../components/ui/Modal';
import modal from '../../components/ui/Modal.module.css'
import { Button } from '../../components/ui/elements/Button'

import styles from './CompanyPage.module.css';
import { useNavigate } from 'react-router-dom';

export const CompanyPage = () => {

      const navigate = useNavigate();

    const { user } = useUser();
    const { company, createCompany, addWorker, deleteWorker, deleteCompany, updateCompany } = useCompany();

    const [companyName, setCompanyName] = useState('');
    const [emailWorker, setEmailWorker] = useState('')

    const [currentWorkerId, setCurrentWorkerId] = useState('')

    const [loading, setLoading] = useState(false)
    const [createCompanyModalIsOpen, setCreateCompanyModalIsOpen] = useState(false);
    const [updateCompanyModalIsOpen, setUpdateCompanyModalIsOpen] = useState(false);
    const [addWorkerModalIsOpen, setAddWorkerModalIsOpen] = useState(false);
    const [deleteWorkerModalIsOpen, setDeleteWorkerModalIsOpen] = useState(false);
    const [deleteCompanyModalIsOpen, setDeleteCompanyModalIsOpen] = useState(false);

    const handleCreateCompany = async (e) =>
    {
        e.preventDefault()
        setLoading(true)
        try{
            await createCompany(companyName)
        }
        finally{
            setCreateCompanyModalIsOpen(false)
            setLoading(false)
        }
    }

    const handleUpdateCompany = async (e) =>
    {
        e.preventDefault()
        setLoading(true)
        try{
            await updateCompany(companyName)
        }
        finally{
            setUpdateCompanyModalIsOpen(false)
            setLoading(false)
        }
    }

    const handleAddWorker = async (e) => 
    {
        e.preventDefault()
        setLoading(true)
        try{
            await addWorker(emailWorker)
            setEmailWorker("")
        }
        finally{
            setAddWorkerModalIsOpen(false)
            setEmailWorker("")
            setLoading(false)
        }
    }

    const handleDeleteWorker = async (e) =>
    {
        e.preventDefault()
        setLoading(true)
        try{
            await deleteWorker(currentWorkerId)
        }
        finally{
            setDeleteWorkerModalIsOpen(false)
            setCurrentWorkerId('')
            setLoading(false)
        }
    }

    const handleDeleteCompany = async (e) =>
    {
        e.preventDefault()
        setLoading(true)
        try{
            await deleteCompany()
        }
        finally{
            setDeleteCompanyModalIsOpen(false)
            setLoading(false)
        }
        navigate("/profile") 
    }

    return (
        <div className={styles.page}>
            <div className={styles.container}>
                {/* Header */}
                <div className={styles.header}>
                    <div className={styles.avatar}>
                        {company?.name.charAt(0)}
                    </div>
                    <div className={styles.headerInfo}>
                        <div className={styles.name}>{company ? company.name : user.userType == "LEADER" ? "Создайте компанию" : "Вы не принадлижите ни одной компании"}</div>
                    </div>
                    {user.userType == "LEADER" &&
                    <>
                    <Button variant={!company ? "primary" : ""} className={company ? styles.editButton : ""} onClick={company ? () => setUpdateCompanyModalIsOpen(true) : setCreateCompanyModalIsOpen(true)}>
                        {company &&
                            <>
                                <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                                    <path d="M20 14.66V20a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V6a2 2 0 0 1 2-2h5.34" />
                                    <polygon points="18 2 22 6 12 16 8 16 8 12 18 2" />
                                </svg>
                                Редактировать
                            </>
                        }
                        {!company &&
                            <>
                                Зарегистрировать компанию
                            </>
                        }
                    </Button>
                    {company &&
                        <Button className={styles.editButton} onClick={() => setDeleteCompanyModalIsOpen(true)}>
                            Удалить компанию
                        </Button>
                    }
                    </>
                    }
                </div>
            {company && 
                <>
                <div className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2 className={styles.sectionTitle}>Coworkers ({company?.users.length})</h2>
                        {user.userType == "LEADER" &&
                        <div className={styles.sectionActions}>
                            <Button className={styles.textButton} onClick={() => setAddWorkerModalIsOpen(true)}>+ Add a user</Button>
                        </div>
                        }
                    </div>

                    <div className={styles.coworkersGrid}>
                        {company?.users.map(worker => {
                            return <CompanyCoworkerCard key={worker.id} name={worker.name} email={worker.email} onClick={worker.userType != "LEADER" && user.userType == "LEADER" ? ()=>{
                                    setCurrentWorkerId(worker.id)
                                    setDeleteWorkerModalIsOpen(true);
                                }
                                :
                                undefined
                            }></CompanyCoworkerCard>
                            })
                        }
                    </div>
                </div>
            </>
            }
            </div>

            <Modal isOpen={createCompanyModalIsOpen} onClose={() => setCreateCompanyModalIsOpen(false)} title="Создать компанию">
                <form className={modal.form} onSubmit={(e) => handleCreateCompany(e)}>
                    <div className={modal.inputWrapper}>
                        <lable className={modal.field}>
                            Название компании
                        </lable>
                        <input                             
                            value={companyName}
                            onChange={(e) => setCompanyName(e.target.value)}
                            placeholder="Введите название компании"
                            className={modal.input}
                        />
                    </div>
                    <Button
                        variant="primary"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Создание...' : 'Создать'}
                    </Button>
                </form>
            </Modal>

            <Modal isOpen={updateCompanyModalIsOpen} onClose={() => setUpdateCompanyModalIsOpen(false)} title="Редактировать компанию">
                <form className={modal.form} onSubmit={(e) => handleUpdateCompany(e)}>
                    <div className={modal.inputWrapper}>
                        <lable className={modal.field}>
                            Название компании
                        </lable>
                        <input                             
                            value={companyName}
                            onChange={(e) => setCompanyName(e.target.value)}
                            placeholder="Введите название компании"
                            className={modal.input}
                        />
                    </div>
                    <Button
                        variant="primary"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Редактирование...' : 'Редактировать'}
                    </Button>
                </form>
            </Modal>

            <Modal isOpen={addWorkerModalIsOpen} onClose={() => setAddWorkerModalIsOpen(false)} title="Добавить сотрудника">
                <form className={modal.form} onSubmit={(e) => handleAddWorker(e)}>
                    <div className={modal.inputWrapper}>
                        <lable className={modal.field}>
                            E-mail сотрудника
                        </lable>
                        <input                             
                            value={emailWorker}
                            onChange={(e) => setEmailWorker(e.target.value)}
                            placeholder="Введите email сотрудника"
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

            <Modal isOpen={deleteWorkerModalIsOpen} onClose={() => {
                    setDeleteWorkerModalIsOpen(false);
                }} title="Удалить сотрудника">
                <form className={modal.form} onSubmit={(e) => handleDeleteWorker(e)}>
                    <div className={modal.lable}>
                        Вы уверены, что хотите удалить сотрудника?
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

            <Modal isOpen={deleteCompanyModalIsOpen} onClose={() => {
                    setDeleteCompanyModalIsOpen(false);
                }} title="Удалить компанию">
                <form className={modal.form} onSubmit={(e) => handleDeleteCompany(e)}>
                    <div className={modal.lable}>
                        Вы уверены, что хотите удалить компанию?
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