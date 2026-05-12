import profile from './ProfilePage.module.css';
import modal from '../../components/ui/Modal.module.css'
import { Button } from '../../components/ui/elements/Button';
import { useAuth } from '../../context/AuthContext'
import { useEffect, useState } from 'react'
import { Modal } from '../../components/ui/Modal'

export const ProfilePage = () => {

    const { user, logout, deleteAccount, changeEmail } = useAuth();
    const [newEmail, setNewEmail] = useState("");
    
    const [updateModalIsOpen, setUpdateModalIsOpen] = useState(false)
    const [deleteModalIsOpen, setDeleteModalIsOpen] = useState(false)
    const [changeEmailModalIsOpen, setChangeEmailModalIsOpen] = useState(false)

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPamssword, setConfirmPassword] = useState("");    

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        console.log(user)
    }, [user]);

    const handleChangeEmail = async (e) => {
        e.preventDefault();
        setLoading(true)
        try{
            changeEmail(newEmail)
        } 
        catch (er) {
            toast(er)
        }
        finally{
            setLoading(false)
            setChangeEmailModalIsOpen(false)
        }
    }

    const handleDeleteAccount = async (e) => {
        e.preventDefault();
        setLoading(true)
        try{
            deleteAccount()
        } 
        catch (er) {
            toast(er)
        }
        finally{
            setLoading(false)
            setDeleteModalIsOpen(false)
            location.reload();
        }
    }

    const roleName = (role) =>
    {
        switch (role)
        {
            case "LEADER": return "Организатор"
            case "STANDART": return "Работник"
            default: return role
        }
    }

    return (
        <div className={profile.page}>
            <div className={profile.container}>

                {/* HEADER */}
                <div className={profile.header}>
                    <div className={profile.avatar}>
                        {user.username[0].toUpperCase()}
                    </div>

                    <div className={profile.headerInfo}>
                        <h1 className={profile.name}>
                            {user.name}  
                        </h1>

                        <p className={profile.username}>
                            {user.username}  
                        </p>
                    </div>

                    <Button 
                    variant="primary"
                    className={profile.editButton}
                    onClick={() => setUpdateModalIsOpen(true)}>
                        ✏️
                        <span>Редактировать</span>
                    </Button>
                </div>

                {/* INFO */}
                <div className={profile.section}>
                    <h2 className={profile.sectionTitle}>
                        Информация профиля
                    </h2>

                    <div className={profile.infoGrid}>

                        <div className={profile.infoCard}>
                        <div className={profile.infoIcon}>
                            📧
                        </div>
                        <div>
                            <p className={profile.infoLabel}>Email</p>
                            <p className={profile.infoValue}>{user.email}</p>
                        </div>
                        </div>

                        <div className={profile.infoCard}>
                        <div className={profile.infoIcon}>
                            🛡️
                        </div>
                        <div>
                            <p className={profile.infoLabel}>Роль</p>
                            <p className={profile.infoValue}>{roleName(user.userType)}</p>
                        </div>
                        </div>

                        <div className={profile.infoCard}>
                        <div className={profile.infoIcon}>
                            📅
                        </div>
                        <div>
                            <p className={profile.infoLabel}>Дата регистрации</p>
                            <p className={profile.infoValue}>07.05.2026</p>
                        </div>
                        </div>

                    </div>
                </div>

                {/* ACTIONS */}
                <div className={profile.section}>
                    <h2 className={profile.sectionTitle}>
                        Безопасность
                    </h2>

                    <div className={profile.actions}>
                        <Button variant="primary" className={profile.secondaryButton} onClick={() => setChangeEmailModalIsOpen(true)}>
                        🔑
                        <span>Изменить почту</span>
                        </Button>

                        <Button variant="delete" className={profile.dangerButton} onClick={() => setDeleteModalIsOpen(true)}>
                        🗑️
                        <span>Удалить аккаунт</span>
                        </Button>
                        
                        <Button variant="cancel" className={profile.dangerButton} onClick={() => logout()}>
                            <span>Выйти из аккаунта</span>
                        </Button>
                    </div>
                </div>
            </div>

            <Modal isOpen={changeEmailModalIsOpen} onClose={() => setChangeEmailModalIsOpen(false)} title="Изменить почту">
                <form className={modal.form} onSubmit={(e) => handleChangeEmail(e)}>
                    <div className={modal.field}>
                        <label className={modal.label}>
                        Email
                        </label>
                        <div className={modal.inputWrapper}>
                        <input
                            type="email"
                            required
                            value={newEmail}
                            onChange={(e) => setNewEmail(e.target.value)}
                            placeholder="Введите ваш email"
                            className={modal.input}
                        />
                        </div>
                    </div>
                    <Button
                        variant="primary"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'Отправка...' : 'Отправить'}
                    </Button>
                </form>
            </Modal>

            <Modal isOpen={deleteModalIsOpen} onClose={() => setDeleteModalIsOpen(false)} title="Удалить аккаунт">
                <form className={modal.form} onSubmit={(e) => handleDeleteAccount(e)}>
                    <div className={modal.field}>
                        Вы уверенны, что хотите удалить свой аккаунт
                    </div>
                    <Button
                        variant="delete"
                        type="submit"
                        disabled={loading}
                    >
                        {loading ? 'удаление...' : 'Удалить'}
                    </Button>
                </form>
            </Modal>
        </div>
    );
};