import profile from './ProfilePage.module.css';
import { Button } from '../../components/ui/elements/Button';
import { useAuth } from '../../context/AuthContext'
import { useEffect, useState } from 'react'
import { Modal } from '../../components/ui/Modal'

export const ProfilePage = () => {

    const { user } = useAuth();
    const [newEmail, setNewEmail] = useState("");

    const [currentPassword, setCurrentPassword] = useState("");
    const [newPassword, setNewPassword] = useState("");
    const [confirmPamssword, setConfirmPassword] = useState("");    

    useEffect(() => {
        console.log(user)
    }, [user]);


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
                    className={profile.editButton}>
                    onClick={console.log(user)}
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

                        <Button variant="primary" className={profile.secondaryButton}>
                        🔑
                        <span>Изменить почту</span>
                        </Button>

                        <Button variant="primary"  className={profile.secondaryButton}>
                        🔑
                        <span>Изменить пароль</span>
                        </Button>

                        <Button variant="delete" className={profile.dangerButton}>
                        🗑️
                        <span>Удалить аккаунт</span>
                        </Button>

                    </div>
                </div>
            </div>
            
            <Modal>

            </Modal>
        </div>
    );
};