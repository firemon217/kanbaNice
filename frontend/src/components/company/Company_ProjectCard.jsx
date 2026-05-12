import styles from "./Company_ProjectCard.module.css"


export const CompanyProjectCard = ({  }) => {

    return (
        <div className={styles.projectCard}>
            <div className={styles.projectIcon}>🌾</div>
            <div className={styles.projectInfo}>
                <div className={styles.projectName}>LAFA Land project</div>
                <div className={styles.projectStatus}>FAR</div>
            </div>
            <div className={styles.projectCount}>+4</div>
        </div>
    );
}
