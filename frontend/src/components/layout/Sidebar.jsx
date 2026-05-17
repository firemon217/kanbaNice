import { NavLink } from 'react-router-dom';

import sidebar from './Sidebar.module.css';
import { useProject } from '../../context/ProjectContext';

export const Sidebar = ({ isOpen, setMobileMenuOpen }) => {

  const { currentProject } = useProject();

  const NAV_ITEMS_PROFILE = [
    { path: '/profile', label: 'Профиль' },
    { path: '/projects', label: 'Проекты' },
    { path: '/company', label: 'Ваша Компания' },
  ];

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

