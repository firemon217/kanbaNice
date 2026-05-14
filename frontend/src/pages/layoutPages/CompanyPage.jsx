import { useUser } from '../../context/UserContext';
import { useCompany } from '../../context/CompanyContext';
import { useEffect, useState } from 'react';
import { CompanyProjectCard } from '../../components/company/Company_ProjectCard';
import { CompanyCoworkerCard } from '../../components/company/Company_CoworkerCard';

import { Modal } from '../../components/ui/Modal';
import modal from '../../components/ui/Modal.module.css'
import { Button } from '../../components/ui/elements/Button'

import styles from './CompanyPage.module.css';

export const CompanyPage = () => {

    const { user } = useUser();
    const { company, createCompany } = useCompany();

    const [companyName, setCompanyName] = useState('');

    const [loading, setLoading] = useState(false)
    const [createCompanyModalIsOpen, setCreateCompanyModalIsOpen] = useState(false);

    const handleCreateCompany = (e) =>
    {
        e.preventDefault()
        setLoading(true)
        try{
            createCompany(companyName)
        }
        finally{
            setCreateCompanyModalIsOpen(false)
        }
        setLoading(false)
    }

    useEffect (()=>{
        console.log(user)
    }, [user])

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
                    <Button variant={!company ? "primary" : ""} className={company ? styles.editButton : ""} onClick={!company ? () => setCreateCompanyModalIsOpen(true) : ""}>
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
                </div>
            {company && 
                <>
                {/* Projects Section */}
                <div className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2 className={styles.sectionTitle}>Projects</h2>
                        <div className={styles.sectionActions}>
                            <button className={styles.iconButton}>+ Add project with tasks</button>
                            <button className={styles.textButton}>Add CRM-project</button>
                        </div>
                    </div>

                    {/* for */}
                    <CompanyProjectCard></CompanyProjectCard>
                </div>

                {/* Coworkers Section */}
                <div className={styles.section}>
                    <div className={styles.sectionHeader}>
                        <h2 className={styles.sectionTitle}>Coworkers (9)</h2>
                        <div className={styles.sectionActions}>
                            <button className={styles.textButton}>+ Add a user</button>
                            <button className={styles.textButton}>Download list</button>
                        </div>
                    </div>

                    {/* Coworkers Grid */}
                    <div className={styles.coworkersGrid}>
                        {/* Пример сотрудников — можно заменить на реальные данные */}
                        <CompanyCoworkerCard></CompanyCoworkerCard>
                        <CompanyCoworkerCard></CompanyCoworkerCard>
                        <CompanyCoworkerCard></CompanyCoworkerCard>
                        <div className={styles.coworkerAdd}>
                            <button className={styles.addCoworkerBtn}>+ Добавить</button>
                        </div>
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
        </div>
    );
};