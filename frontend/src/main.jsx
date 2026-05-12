import { StrictMode } from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { Providers } from './Providers';
import App from './App.jsx';

import './styles/global.css'


createRoot(document.getElementById('root')).render(
  <StrictMode>
    <BrowserRouter>
      <Providers>
        <App />
        <Toaster
          position="bottom-right"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#102A20',
              color: '#e5e7eb',
              border: '1px solid #236e4f',
            },
            success: {
              iconTheme: {
                primary: '#1f614b',
                secondary: '#fff',
              },
            },
            error: {
              iconTheme: {
                primary: '#8f2a2a',
                secondary: '#fff',
              },
            },
          }}
        />
      </Providers>
    </BrowserRouter>
  </StrictMode>,
);
