import { NavLink } from 'react-router-dom';

import sidebar from './Sidebar.module.css';

const NAV_ITEMS_PROFILE = [
  { path: '/profile', label: 'Профиль' },
  { path: '/project/tasks', label: 'Задачи' },
  { path: '/projects', label: 'Проекты' },
  { path: '/company', label: 'Ваша Компания' },
];

export const Sidebar = ({ isOpen, setMobileMenuOpen }) => {
  return (
    <div className={sidebar.container}>
        <header className={sidebar.header}>
          KanbaNice
        </header>

        <nav className={sidebar.nav}>
          {NAV_ITEMS_PROFILE.map((item) => {
            return (
              <NavLink
                key={item.path}
                to={item.path}
                onClick={() => setMobileMenuOpen(false)}
                className={({ isActive }) =>
                  isActive
                    ? sidebar.navActive
                    : sidebar.nav
                }
              >
                <span className={sidebar.navLinkText}>{item.label}</span>
              </NavLink>
            );
          })}
        </nav>
    </div>
  );
};

