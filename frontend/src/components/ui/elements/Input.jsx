import input from './Input.module.css';

export const Input = ({ variant, value, className, ...props }) => {

    return (
            <input 
            className={(input[variant] || input.primary) + ' ' + className}
            value={value}
            {...props}
            />
    );
}