import button from './Button.module.css';

export const Button = ({ variant, children, ...props }) => {

    return (
            <button 
            className={button[variant] || button.primary}
            {...props}
            >
                {children}
            </button>
    );
}