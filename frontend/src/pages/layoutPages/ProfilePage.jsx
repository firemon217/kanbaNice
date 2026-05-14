import profile from './ProfilePage.module.css';
import modal from '../../components/ui/Modal.module.css'
import { Button } from '../../components/ui/elements/Button';
import { useUser } from '../../context/UserContext'
import { useEffect, useState } from 'react'
import { Modal } from '../../components/ui/Modal'
import toast from 'react-hot-toast';

export const ProfilePage = () => {

    const { user, logout, deleteAccount, updateProfile } = useUser();

    const [newName, setNewName] = useState("");
    const [newUsername, setNewUsername] = useState("");
    const [newEmail, setNewEmail] = useState("");
    
    const [updateModalIsOpen, setUpdateModalIsOpen] = useState(false)
    const [deleteModalIsOpen, setDeleteModalIsOpen] = useState(false)

    const [newPassword, setNewPassword] = useState("");
    const [confirmPassword, setConfirmPassword] = useState("");    

    const [showNewPassword, setShowNewPassword] = useState("");
    const [showConfirmPassword, setShowConfirmPassword] = useState("");    

    const [loading, setLoading] = useState(false);

    useEffect(() => {
        console.log(user)
    }, [user]);

    const handleDeleteAccount = async (e) => {
        e.preventDefault();
        setLoading(true)
        try{
            deleteAccount()
        }
        finally{
            setLoading(false)
            setDeleteModalIsOpen(false)
        }
        location.reload();  
    }

    const handleUpdateProfile = async (e) => {
        e.preventDefault();
        setLoading(true)
        try{
            if((newPassword || newPassword.trim().length > 0) && newPassword.length != 6)
            {
                toast.error("Новый пароль слишком короткий, минимальная длинна = 6")
                return;
            }
            if(newPassword != confirmPassword)
            {
                toast.error("Пароли не совпадают")
                return;
            }
            console.log({newName, newUsername, newEmail, newPassword, confirmPassword})
            updateProfile(newName, newUsername, newEmail, newPassword, confirmPassword )
        } 
        finally{
            setLoading(false)
            setUpdateModalIsOpen(false)
        }
        location.reload();  
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
                        {user?.username[0].toUpperCase()}
                    </div>

                    <div className={profile.headerInfo}>
                        <h1 className={profile.name}>
                            {user?.name}  
                        </h1>

                        <p className={profile.username}>
                            {user?.username}  
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
                            <p className={profile.infoValue}>{user?.email}</p>
                        </div>
                        </div>

                        <div className={profile.infoCard}>
                        <div className={profile.infoIcon}>
                            🛡️
                        </div>
                        <div>
                            <p className={profile.infoLabel}>Роль</p>
                            <p className={profile.infoValue}>{roleName(user?.userType)}</p>
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

            <Modal isOpen={updateModalIsOpen} onClose={() => setUpdateModalIsOpen(false)} title="Обновить пользователя">
                <form className={modal.form} onSubmit={(e) => handleUpdateProfile(e)}>
                    <div className={modal.field}>
                        <label className={modal.label}>
                            Имя
                        </label>
                        <div className={modal.inputWrapper}>
                            <input
                                value={newName}
                                onChange={(e) => setNewName(e.target.value)}
                                placeholder="Введите ваше имя"
                                className={modal.input}
                            />
                        </div>
                    </div>
                    <div className={modal.field}>
                        <label className={modal.label}>
                            Username
                        </label>
                        <div className={modal.inputWrapper}>
                            <input
                                value={newUsername}
                                onChange={(e) => setNewUsername(e.target.value)}
                                placeholder="Введите ваш username"
                                className={modal.input}
                            />
                        </div>
                    </div>
                    <div className={modal.field}>
                        <label className={modal.label}>
                            Email
                        </label>
                        <div className={modal.inputWrapper}>
                            <input
                                type="email"
                                value={newEmail}
                                onChange={(e) => setNewEmail(e.target.value)}
                                placeholder="Введите ваш email"
                                className={modal.input}
                            />
                        </div>
                    </div>
                    <div className={modal.field}>
                        <label className={modal.label}>
                            Новый пароль
                        </label>
                        <div className={modal.inputWrapper}>
                            <input
                                type={showNewPassword ? 'text' : 'password'}
                                value={newPassword}
                                onChange={(e) => setNewPassword(e.target.value)}
                                placeholder="••••••••"
                                className={modal.input}
                            />
                            <Button
                                variant="password"
                                type="button"
                                onClick={() => setShowNewPassword(!showNewPassword)}
                            >
                                {showNewPassword
                                ? "eyeOff"
                                : "eye"
                                }
                            </Button>
                        </div>
                    </div>
                    <div className={modal.field}>
                        <label className={modal.label}>
                            Повторите новый пароль
                        </label>
                        <div className={modal.inputWrapper}>
                            <input
                                type={showConfirmPassword ? 'text' : 'password'}
                                value={confirmPassword}
                                onChange={(e) => setConfirmPassword(e.target.value)}
                                placeholder="••••••••"
                                className={modal.input}
                            />
                            <Button
                                variant="password"
                                type="button"
                                onClick={() => setShowConfirmPassword(!showConfirmPassword)}
                            >
                                {showConfirmPassword
                                ? "eyeOff"
                                : "eye"
                                }
                            </Button>
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