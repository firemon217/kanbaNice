import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/ui/elements/Button';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';

import loginStyle from './LoginPage.module.css';

export default function LoginPage() {

  const { login } = useAuth();
  const navigate = useNavigate();

  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    try{
      const response = await login(username, password);
      navigate('/main');
    }
    catch(error){
      console.error('An error occurred:', error);
    }
    finally{
      setLoading(false);
    }

  };

  const handleGoogleLogin = () => {
    console.log('Google loginStyle');
  };

  return (
    <div className={loginStyle.page}>
      <div className={loginStyle.card}>
        {/* HEADER */}
        <div className={loginStyle.header}>
          <h1 className={loginStyle.title}>
            Добро пожаловать
          </h1>

          <p className={loginStyle.subtitle}>
            Войдите, чтобы продолжить работу
          </p>
        </div>

        {/* FORM */}
        <form
          onSubmit={handleSubmit}
          className={loginStyle.form}
        >
          {/* USERNAME */}
          <div className={loginStyle.field}>
            <label className={loginStyle.label}>
              Имя пользователя
            </label>

            <div className={loginStyle.inputWrapper}>
              <div className={loginStyle.icon}>
                {/* <User size={18} /> */}
              </div>

              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Введите имя пользователя"
                className={loginStyle.input}
              />
            </div>
          </div>

          {/* PASSWORD */}
          <div className={loginStyle.field}>
            <label className={loginStyle.label}>
              Пароль
            </label>

            <div className={loginStyle.inputWrapper}>
              <div className={loginStyle.icon}>
                {/* <Lock size={18} /> */}
              </div>

              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className={loginStyle.input}
              />

              <Button
                variant="primary"
                className={loginStyle.passwordButton}
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword
                  ? "eyeOff"
                  : "eye"
                }
              </Button>
            </div>

            <div className={loginStyle.actions}>
              <Link
                to="/forgot-password"
                className={loginStyle.link}
              >
                Забыли пароль?
              </Link>
            </div>
          </div>

          <Button
            variant="primary"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Вход...' : 'Войти'}
          </Button>
        </form>

        <div className={loginStyle.divider}>
          <span>ИЛИ</span>
        </div>

        <Button variant="primary" onClick={handleGoogleLogin}>
          Продолжить с Google
        </Button>

        <div className={loginStyle.footer}>
          Нет аккаунта?{' '}

          <Link
            to="/signup"
            className={loginStyle.footerLink}
          >
            Зарегистрироваться
          </Link>
        </div>
      </div>
    </div>
  );
}