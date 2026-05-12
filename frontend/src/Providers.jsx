import { AuthProvider } from "./context/AuthContext";
import { CompanyProvider } from "./context/CompanyContext";

export const Providers = ({ children }) => {
  return (
    <AuthProvider>
      <CompanyProvider>        
        {children}
      </CompanyProvider>
    </AuthProvider>
  );
};