import {useAuth} from '../../context/AuthContext';
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export const MainPage = () => {

    const { user } = useAuth();
    const navigate = useNavigate();

    return (
        <div>
        <h1>Main Pages</h1>
        </div>
    );
};