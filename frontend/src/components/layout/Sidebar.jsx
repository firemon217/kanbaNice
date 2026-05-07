import { NavLink } from 'react-router-dom';
import { useAuth } from '../../context/AuthContext';

const NAV_ITEMS_PROFILE = [
  { path: '/profile', label: 'Профиль' },
  { path: '/subjects', label: 'Предметы' },
  { path: '/groups', label: 'Группа' },
];

export const Sidebar = ({ isOpen, setMobileMenuOpen }) => {
  const { user, logout } = useAuth();

  return (
    <>
      <div 
        className={``}
      >
        <div className="">
          <div className="">
            <h1 className="">Список групп</h1>
          </div>
          
          <nav className="">
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
                  <span className="">{item.label}</span>
                </NavLink>
              );
            })}
          </nav>

          <div className="">
            <div className="">
              <div className="">
                {user?.name?.charAt(0) || user?.username?.charAt(0) || '?'}
              </div>
              <div className="">
                <p className="">{user?.name}</p>
                <p className="">@{user?.username}</p>
              </div>
            </div>
            <button 
              onClick={logout}
              className=""
            >
              Выход
            </button>
          </div>
        </div>
      </div>
      
      {/* Mobile overlay */}
      {isOpen && (
        <div 
          className=""
          onClick={() => setMobileMenuOpen(false)}
        />
      )}
    </>
  );
};

