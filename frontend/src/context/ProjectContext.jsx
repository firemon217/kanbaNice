import { createContext, useState, useContext, useEffect } from 'react';
import { projectService } from '../api';
import toast from 'react-hot-toast';
import { useUser } from './UserContext';

const ProjectContext = createContext();

export const useProject = () => useContext(ProjectContext);

export const ProjectProvider = ({ children }) => {
  const { token } = useUser();  

  const [projects, setProjects] = useState(null);
  const [currentProject, setCurrentProject] = useState(() => {
    const savedProject = sessionStorage.getItem('currentProject');
    if (savedProject && savedProject !== 'undefined') {
      try {
        return JSON.parse(savedProject);
      } catch {
        return null;
      }
    }
    return null;
  })
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (currentProject) {
      sessionStorage.setItem('currentProject', JSON.stringify(currentProject));
    } else {
      sessionStorage.removeItem('currentProject');
    }
  }, [currentProject]);

  const fetchProjects = async () => {
    if (token) {
      try {
        const res = await projectService.getAllProjects();
        setProjects(res.data);
        if(!currentProject)
        {
          setCurrentProject(projects ? projects[0] : null)
        }
      } catch (error) {
        console.error('Error fetching projects data', error);
      }
    } else {
      setProjects(null);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchProjects();
  }, [token]);

  const createProjects = async (name) => {
    try {
      await projectService.createProject(name);
      toast.success('Создание прошло успешно');
      await fetchProjects();
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка создания');
      return false;
    }
  };

  const chooseCurrentProject = async (id) => {
    try {
      const project = await projectService.getProjectById(id);
      setCurrentProject(project)
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка');
      return false;
    }
  }

  const addWorkerInProject = async (id, userId) => {
    try {
      await projectService.addWorkerInProjectById(id, userId);
      toast.success('Работник добавлен успешно');
      await fetchProjects();
      return true;  
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка добавления нового работника');
      return false;
    }
  };

  const deleteProject = async(id) => {
    try {
      await projectService.deleteProjectById(id);
      toast.success('Проект удален успешно');
      await fetchProjects();
      return true;  
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления проекта');
      return false;
    }
  };

  const value = {
    projects,
    currentProject,
    createProjects,
    addWorkerInProject,
    deleteProject,
    chooseCurrentProject,
  };

  return (
    <ProjectContext.Provider value={value}>
      {loading ? (
        <div className="">
          Loading...
        </div>
      ) : (
        children
      )}
    </ProjectContext.Provider>
  );
};