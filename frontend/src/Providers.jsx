import { CompanyProvider } from "./context/CompanyContext";
import { UserProvider } from "./context/UserContext";

export const Providers = ({ children }) => {
  return (
    <UserProvider>
      <CompanyProvider>        
        {children}
      </CompanyProvider>
    </UserProvider>
  );
};