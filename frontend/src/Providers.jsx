import { CompanyProvider } from "./context/CompanyContext";
import { UserProvider } from "./context/UserContext";
import { ProjectProvider } from "./context/ProjectContext";

export const Providers = ({ children }) => {
  return (
    <UserProvider>
      <CompanyProvider>
        <ProjectProvider>
        {children}
        </ProjectProvider>   
      </CompanyProvider>
    </UserProvider>
  );
};