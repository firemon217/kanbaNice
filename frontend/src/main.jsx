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
          position="top-right"
          toastOptions={{
            duration: 3000,
            style: {
              background: '#102A20',
              color: '#e5e7eb',
              border: '1px solid #1B4332',
            },
            success: {
              iconTheme: {
                primary: '#10B981',
                secondary: '#fff',
              },
            },
            error: {
              iconTheme: {
                primary: '#FF4D4D',
                secondary: '#fff',
              },
            },
          }}
        />
      </Providers>
    </BrowserRouter>
  </StrictMode>,
);
