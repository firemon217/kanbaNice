import { Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/layout/Layout';
import { PrivateRoute } from './components/auth/PrivateRoute';

import LoginPage from "./pages/LoginPage"
import RegPage from "./pages/RegPage"

// // IMPORTANT: Add this import
import OAuthRedirect from './pages/OAuthRedirect';
import { MainPage } from './pages/layoutPages/MainPage';
import { ProfilePage } from './pages/layoutPages/ProfilePage';

function App() {
  return (<Routes>

    {/* ✅ OAuth route MUST be public and ABOVE PrivateRoute */}
    <Route path="/oauth2/redirect" element={<OAuthRedirect />} />
    <Route path="/login" element={<LoginPage />} />
    <Route path="/signup" element={<RegPage />} />

    {/* Protected Routes */}
    <Route element={<PrivateRoute />}>
      <Route element={<Layout />}>
        <Route path="/main" element={<MainPage />} />
      </Route>
    </Route>

    <Route element={<PrivateRoute />}>
      <Route element={<Layout />}>
        <Route path="/profile" element={<ProfilePage />} />
      </Route>
    </Route>

    {/* Fallback */}
    <Route path="*" element={<Navigate to="/main" replace />} />
  </Routes>
  );
}

export default App;
