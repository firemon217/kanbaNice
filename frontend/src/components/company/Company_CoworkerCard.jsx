import styles from "./Company_CoworkerCard.module.css"

export const CompanyCoworkerCard = ({  }) => {

    return (
        <div className={styles.coworkerCard}>
            <div className={styles.coworkerAvatar}>М</div>
            <div className={styles.coworkerName}>Максим Орлов</div>
            <div className={styles.coworkerRole}>Разработчик</div>
        </div>
    );
}