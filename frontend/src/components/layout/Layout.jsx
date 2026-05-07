import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

import layout from './Layout.module.css';

export const Layout = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(true);

  return (
    <div className={layout.container}>
      <Sidebar isOpen={mobileMenuOpen} setMobileMenuOpen={setMobileMenuOpen} />
      
      <div className={layout.content}>
        <main className={layout.main}>
          <div className={layout.outlet}>
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

