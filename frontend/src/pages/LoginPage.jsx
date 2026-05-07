import { useState } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '../components/ui/elements/Button';

import styles from './LoginPage.module.css';

export default function LoginPage() {
  const [showPassword, setShowPassword] = useState(false);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [loading, setLoading] = useState(false);

  const handleSubmit = (e) => {
    e.preventDefault();

    console.log({
      username,
      password,
    });
  };

  const handleGoogleLogin = () => {
    console.log('Google login');
  };

  return (
    <div className={styles.page}>
      <div className={styles.card}>
        {/* HEADER */}
        <div className={styles.header}>
          <h1 className={styles.title}>
            Добро пожаловать
          </h1>

          <p className={styles.subtitle}>
            Войдите, чтобы продолжить работу
          </p>
        </div>

        {/* FORM */}
        <form
          onSubmit={handleSubmit}
          className={styles.form}
        >
          {/* USERNAME */}
          <div className={styles.field}>
            <label className={styles.label}>
              Имя пользователя
            </label>

            <div className={styles.inputWrapper}>
              <div className={styles.icon}>
                <User size={18} />
              </div>

              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="Введите имя пользователя"
                className={styles.input}
              />
            </div>
          </div>

          {/* PASSWORD */}
          <div className={styles.field}>
            <label className={styles.label}>
              Пароль
            </label>

            <div className={styles.inputWrapper}>
              <div className={styles.icon}>
                <Lock size={18} />
              </div>

              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="••••••••"
                className={styles.input}
              />

              <Button
                variant="primary"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword
                  ? <EyeOff size={18} />
                  : <Eye size={18} />
                }
              </Button>
            </div>

            <div className={styles.actions}>
              <Link
                to="/forgot-password"
                className={styles.link}
              >
                Забыли пароль?
              </Link>
            </div>
          </div>

          {/* SUBMIT */}
          <Button
            variant="primary"
            type="submit"
            disabled={loading}
          >
            {loading ? 'Вход...' : 'Войти'}
          </Button>
        </form>

        {/* DIVIDER */}
        <div className={styles.divider}>
          <span>ИЛИ</span>
        </div>

        {/* GOOGLE */}
        <Button variant="primary" onClick={handleGoogleLogin}>
          Продолжить с Google
        </Button>

        {/* FOOTER */}
        <div className={styles.footer}>
          Нет аккаунта?{' '}

          <Link
            to="/signup"
            className={styles.footerLink}
          >
            Зарегистрироваться
          </Link>
        </div>
      </div>
    </div>
  );
}