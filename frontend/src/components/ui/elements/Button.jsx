import button from './Button.module.css';

export const Button = ({ variant, className, children, ...props }) => {

    return (
            <button 
            className={(button[variant] || button.primary) + ' ' + className}
            {...props}
            >
                {children}
            </button>
    );
}