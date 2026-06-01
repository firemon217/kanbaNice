import { createContext, useState, useContext, useEffect } from 'react';
import { projectService, boardService, taskService } from '../api';
import toast from 'react-hot-toast';
import { useUser } from './UserContext';

const ProjectContext = createContext();

export const useProject = () => useContext(ProjectContext);

export const ProjectProvider = ({ children }) => {
  const { token } = useUser();  

  const [boards, setBoards] = useState(null);
  const [projects, setProjects] = useState(null);
  const [tasksMap, setTasksMap] = useState({}); 

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
  });
  const [loading, setLoading] = useState(true);

  const fetchProjects = async () => {
    if (token) {
      try {
        const res = await projectService.getAllProjects();
        setProjects(res.data);
      } catch (error) {
        console.error('Error fetching projects data', error);
      }
    } else {
      setProjects(null);
    }
  };

  const fetchBoards = async () => {
    if (token && currentProject) {
      try {
        const res = await boardService.getBoards(currentProject.id);
        setBoards(res.data);
      } catch (error) {
        console.error('Error fetching boards data', error);
        setBoards(null);
      }
    } else {
      setBoards(null);
    }
  };

  const fetchTasks = async (boardId) => {
    try {
      const res = await taskService.getTasks(boardId);
      setTasksMap(prev => ({
        ...prev,
        [boardId]: res.data
      }));
      return res.data;
    } catch (error) {
      console.error(error);
      return [];
    }
  };

  useEffect(() => {
    const init = async () => {
      setLoading(true);
      await fetchProjects();
      setLoading(false);
    };
    init();
  }, [token]);

  useEffect(() => {
    if (currentProject && token) {
      fetchBoards();
    } else {
      setBoards(null);
    }
  }, [currentProject, token]);

  useEffect(() => {
    if (currentProject) {
      sessionStorage.setItem('currentProject', JSON.stringify(currentProject));
    } else {
      sessionStorage.removeItem('currentProject');
    }
  }, [currentProject]);

  //Projects
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
      setCurrentProject(project.data);
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка');
      return false;
    }
  };

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

  const deleteProject = async (id) => {
    try {
      await projectService.deleteProjectById(id);
      toast.success('Проект удален успешно');
      await fetchProjects();
      if (currentProject?.id === id) {
        setCurrentProject(null);
      }
      return true;  
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления проекта');
      return false;
    }
  };

  //Boards
  const createBoard = async (name) => {
    if (!currentProject) {
      toast.error('Выберите проект');
      return false;
    }
    try {
      await boardService.createBoard(currentProject.id, name);
      toast.success('Доска добавлена успешно');
      await fetchBoards();
      return true;  
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка добавления доски');
      return false;
    }
  };

  const deleteBoard = async (boardId) => {
    if (!currentProject) {
      toast.error('Выберите проект');
      return false;
    }
    try {
      await boardService.deleteBoard(boardId);
      toast.success('Доска удалена успешно');
      await fetchBoards();
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления доски');
      return false;
    }
  };

  const updateBoard = async (boardId, name) => {
    if (!currentProject) {
      toast.error('Выберите проект');
      return false;
    }
    try {
      await boardService.updateBoard(boardId, name);
      toast.success('Доска обновлена успешно');
      await fetchBoards();
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка обновления доски');
      return false;
    }
  };

  const createTask = async (boardId, title, description, status) => {
    try {
      await taskService.createTask(boardId, title, description, status);
      toast.success('Задача создана успешно');
      fetchTasks(boardId); 
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка создания задачи');
      return false;
    }
  };

  const deleteTask = async (boardId, taskId) => {
    try {
      await taskService.deleteTask(taskId);
      toast.success('Задача удалена успешно');
      fetchTasks(boardId);
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления задачи');
      return false;
    }
  };

  const updateTask = async (boardId, taskId, title, description, status) => {
    try {
      await taskService.updateTask(taskId, title, description, status);
      toast.success('Задача обновлена успешно');
      fetchTasks(boardId);
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка обновления задачи');
      return false;
    }
  };

  const value = {
    projects,
    boards,
    currentProject,
    tasksMap,
    createProjects,
    addWorkerInProject,
    deleteProject,
    chooseCurrentProject,
    createBoard,
    deleteBoard,
    updateBoard,
    fetchTasks,
    createTask,
    deleteTask,
    updateTask
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