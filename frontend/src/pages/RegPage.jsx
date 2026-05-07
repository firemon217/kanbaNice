import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';

import { useAuth } from '../context/AuthContext';

import signupStyle from './RegPage.module.css';

export default function RegPage() {
  const [name, setName] = useState('');
  const [username, setUsername] = useState('');
  const [role, setRole] = useState('');
  const [password, setPassword] = useState('');
  const [showPassword, setShowPassword] = useState(false);

  const [loading, setLoading] = useState(false);

  const { signup } = useAuth();

  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();

    if (password.length < 6) {
      toast.error('Пароль должен содержать минимум 6 символов.');
      return;
    }

    setLoading(true);

    const success = await signup({
      name,
      role,
      username,
      password,
    });

    setLoading(false);

    if (success) {
      navigate('/login');
    }
  };

  return (
    <div className={signupStyle.page}>
      <div className={signupStyle.card}>
        {/* HEADER */}
        <div className={signupStyle.header}>
          <h1 className={signupStyle.title}>
            Создать аккаунт
          </h1>

          <p className={signupStyle.subtitle}>
            Зарегистрируйтесь для работы с системой
          </p>
        </div>

        {/* FORM */}
        <form
          onSubmit={handleSubmit}
          className={signupStyle.form}
        >
          {/* NAME */}
          <div className={signupStyle.field}>
            <label className={signupStyle.label}>
              Полное имя
            </label>

            <div className={signupStyle.inputWrapper}>
              <div className={signupStyle.icon}>
                {/* <UserCheck size={18} /> */}
              </div>

              <input
                type="text"
                required
                value={name}
                onChange={(e) => setName(e.target.value)}
                placeholder="Иван Иванов"
                className={signupStyle.input}
              />
            </div>
          </div>

          {/* USERNAME */}
          <div className={signupStyle.field}>
            <label className={signupStyle.label}>
              Имя пользователя
            </label>

            <div className={signupStyle.inputWrapper}>
              <div className={signupStyle.icon}>
                {/* <User size={18} /> */}
              </div>

              <input
                type="text"
                required
                value={username}
                onChange={(e) => setUsername(e.target.value)}
                placeholder="ivanov123"
                className={signupStyle.input}
              />
            </div>
          </div>

          {/* ROLE */}
          <div className={signupStyle.field}>
            <label className={signupStyle.label}>
              Роль пользователя
            </label>

            <div className={signupStyle.inputWrapper}>
              <div className={signupStyle.icon}>
                {/* <Shield size={18} /> */}
              </div>

              <select
                required
                value={role}
                onChange={(e) => setRole(e.target.value)}
                className={signupStyle.select}
              >
                <option value="" disabled hidden>
                  Выберите роль
                </option>

                <option value="0">
                  Работник
                </option>

                <option value="1">
                  Организатор
                </option>
              </select>
            </div>
          </div>

          {/* PASSWORD */}
          <div className={signupStyle.field}>
            <label className={signupStyle.label}>
              Пароль
            </label>

            <div className={signupStyle.inputWrapper}>
              <div className={signupStyle.icon}>
                {/* <Lock size={18} /> */}
              </div>

              <input
                type={showPassword ? 'text' : 'password'}
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                placeholder="Минимум 6 символов"
                className={signupStyle.input}
              />

              <button
                type="button"
                onClick={() => setShowPassword(!showPassword)}
                className={signupStyle.passwordButton}
              >
                {showPassword
                  ? "eyeOff" : "eye"
                }
              </button>
            </div>
          </div>

          {/* SUBMIT */}
          <button
            type="submit"
            disabled={loading}
            className={signupStyle.submitButton}
          >
            {loading
              ? 'Создание аккаунта...'
              : 'Зарегистрироваться'}
          </button>
        </form>

        {/* FOOTER */}
        <div className={signupStyle.footer}>
          Уже есть аккаунт?{' '}

          <Link
            to="/login"
            className={signupStyle.link}
          >
            Войти
          </Link>
        </div>
      </div>
    </div>
  );
}