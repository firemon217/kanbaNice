import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';
import { Button } from '../ui/elements/Button';

import sidebar from './Sidebar.module.css';

const NAV_ITEMS_PROFILE = [
  { path: '/profile', label: 'Профиль' },
  { path: '/subjects', label: 'Задачи' },
  { path: '/groups', label: 'Проекты' },
];

export const Sidebar = ({ isOpen, setMobileMenuOpen }) => {
  const { user, logout } = useAuth();

  return (
    <div className={sidebar.container}>
        <nav className={sidebar.nav}>
          {NAV_ITEMS_PROFILE.map((item) => {
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={() => setMobileMenuOpen(false)}
                className={({ isActive }) =>
                  ` ${
                    isActive 
                      ? '' 
                      : ''
                  }`
                }
              >
                <span className={sidebar.navLinkText}>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>

        <footer className={sidebar.footer}>
          <div className={sidebar.userAvatar}>
            {user?.name?.charAt(0) || user?.username?.charAt(0) || '?'}
          </div>
            <div className={sidebar.userName}>
              <p className={sidebar.name}>{user?.name}</p>
              <p className={sidebar.username}>@{user?.username}</p>
            </div>
          <Button 
            variant="cancel"
            onClick={logout}
          >
            Выход
          </Button>
        </footer>
    </div>
  );
};

