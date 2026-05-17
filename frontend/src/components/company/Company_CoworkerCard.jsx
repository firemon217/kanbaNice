import styles from "./Company_CoworkerCard.module.css"

export const CompanyCoworkerCard = ({ name, email, children, ...params }) => {

    return (
        <div className={styles.coworkerCard} {...params}>
            <div className={styles.coworkerName}>{children}</div>
            <div className={styles.coworkerAvatar}>{name?.charAt(0)}</div>
            <div className={styles.coworkerName}>{name}</div>
            <div className={styles.coworkerRole}>{email}</div>
        </div>
    );
}