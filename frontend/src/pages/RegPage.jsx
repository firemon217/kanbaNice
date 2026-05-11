import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { Button } from '../components/ui/elements/Button';
import { Input } from '../components/ui/elements/Input';

import { useAuth } from '../context/AuthContext';

import signupStyle from './RegPage.module.css';

export default function RegPage() {
  const [name, setName] = useState('');
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [userType, setUserType] = useState('');
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
      userType,
      username,
      email,
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
                placeholder="Проценко Дмитрий"
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
                placeholder="procenko"
                className={signupStyle.input}
              />
            </div>
          </div>

          {/* EMAIL */}
          <div className={signupStyle.field}>
            <label className={signupStyle.label}>
              Email
            </label>

            <div className={signupStyle.inputWrapper}>
              <div className={signupStyle.icon}>
                {/* <User size={18} /> */}
              </div>

              <input
                type="text"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="procenko@gmail.com"
                className={signupStyle.input}
              />
            </div>
          </div>

          {/* userType */}
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
                value={userType}
                onChange={(e) => setUserType(e.target.value)}
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

              <Button
                variant="password"
                type="button"
                onClick={() => setShowPassword(!showPassword)}
              >
                {showPassword
                  ? "eyeOff" : "eye"
                }
              </Button>
            </div>
          </div>

          {/* SUBMIT */}
          <Button
            variant="primary"
            type="submit"
            disabled={loading}
            className={signupStyle.submitButton}
          >
            {loading
              ? 'Создание аккаунта...'
              : 'Зарегистрироваться'}
          </Button>
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