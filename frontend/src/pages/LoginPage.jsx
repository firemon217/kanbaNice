import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/ui/elements/Button';
import { useUser } from '../context/UserContext';
import { useNavigate } from 'react-router-dom';
import { Modal } from '../components/ui/Modal';

import loginStyle from './LoginPage.module.css';

export default function LoginPage() {
  const { login, forgotPassword } = useUser();
  const navigate = useNavigate();

  const [showForgotPassword, setShowForgotPassword] = useState(false);

  const [showPassword, setShowPassword] = useState(false);
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [email, setEmail] = useState('');
  const [loading, setLoading] = useState(false);
  const [submittedForgotPassword, setSubmittedForgotPassword] = useState(false);

  const handleSubmitForgotPassword = async (e) => {
    e.preventDefault();
    setLoading(true);
    await forgotPassword(email);
    setSubmittedForgotPassword(true);
    setLoading(false);
  };

  const handleSubmitLogin = async (e) => {
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
      window.location.href = "/oauth2/authorization/google";
  };

  return (
    <div className={loginStyle.page}>
      <div className={loginStyle.card}>
        {/* HEADER */}
        <div className={loginStyle.header}>
          <h1 className={loginStyle.title}>
            KanbaNice
          </h1>

          <p className={loginStyle.subtitle}>
            Войдите, чтобы продолжить работу
          </p>
        </div>

        {/* FORM */}
        <form
          onSubmit={handleSubmitLogin}
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
                variant="password"
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
              <Button
                variant="primary"
                type="button"
                onClick={() => setShowForgotPassword(true)}
              >
                Забыли пароль?
              </Button>
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

      <Modal isOpen={showForgotPassword} onClose={() => setShowForgotPassword(false)} title="Восстановление пароля">
        {submittedForgotPassword ? (
          <p className={loginStyle.label}>Если аккаунт с таким email существует, ссылка для сброса пароля была отправлена на вашу почту.</p>
        ) : (
          <form onSubmit={handleSubmitForgotPassword}>
            <div className={loginStyle.field}>
              <label className={loginStyle.label}>
                Email
              </label>
              <div className={loginStyle.inputWrapper}>
                <input
                  type="email"
                  required
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                  placeholder="Введите ваш email"
                  className={loginStyle.input}
                />
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
        )}
      </Modal>
    </div>
  );
}