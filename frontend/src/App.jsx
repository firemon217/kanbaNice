import { Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from './components/layout/Layout';
import { PrivateRoute } from './components/auth/PrivateRoute';

// // IMPORTANT: Add this import
import OAuthRedirect from './pages/OAuthRedirect';

function App() {
  return (<Routes>

    {/* ✅ OAuth route MUST be public and ABOVE PrivateRoute */}
    <Route path="/oauth2/redirect" element={<OAuthRedirect />} />

    {/* Protected Routes */}
    <Route element={<PrivateRoute />}>
      <Route element={<Layout />}>
        <Route path="/profile" element={<div>Profile Page</div>} />
      </Route>
    </Route>

    {/* Fallback */}
    <Route path="*" element={<Navigate to="/profile" replace />} />
  </Routes>
  );
}

export default App;
