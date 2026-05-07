import { useState } from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';

export const Layout = () => {
  const [mobileMenuOpen, setMobileMenuOpen] = useState(false);

  return (
    <div className="">
      <Sidebar isOpen={mobileMenuOpen} setMobileMenuOpen={setMobileMenuOpen} />
      
      <div className={``}>
        <header className="">
          <button 
            onClick={() => setMobileMenuOpen(!mobileMenuOpen)}
            className=""
          >
            {/* <Menu className="w-6 h-6" /> */}
          </button>
          <span className="">Student Tracker</span>
        </header>

        <main className="">
          <div className="">
            <Outlet />
          </div>
        </main>
      </div>
    </div>
  );
};

