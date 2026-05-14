import { createContext, useState, useContext, useEffect } from 'react';
import { companyService } from '../api';
import toast from 'react-hot-toast';
import { useUser } from './UserContext';

const CompanyContext = createContext();

export const useCompany = () => useContext(CompanyContext);

export const CompanyProvider = ({ children }) => {
  const { token } = useUser();  

  const [company, setCompany] = useState(null);
  const [loading, setLoading] = useState(true);

  const fetchCompany = async () => {
    if (token) {
      try {
        const res = await companyService.getCompany();
        setCompany(res.data);
      } catch (error) {
        console.error('Error fetching company data', error);
        if (error.response?.status === 401) {
          logout();
        }
      }
    } else {
      setCompany(null);
    }
    setLoading(false);
  };

  useEffect(() => {
    fetchCompany();
  }, [token]);

  const createCompany = async (name) => {
    try {
      await companyService.createCompany(name);
      toast.success('Создание прошло успешно');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка создания');
      return false;
    }
    fetchCompany()
    return true;
  };

  const updateCompany = async (name) => {
    try {
      await companyService.updateCompany(name);
      toast.success('Редактирование прошло успешно');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка редактирования');
      return false;
    }
    fetchCompany()
    return true;
  };

  const addWorker = async (email) => {
    try {
      await companyService.addWorkers(email);
      toast.success('Работник добавлен успешно');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка добавления нового работника');
      return false;
    }
    fetchCompany()
    return true;  
  };

  const deleteWorker = async(id) => {
    try {
      await companyService.deleteWorkers(id);
      toast.success('Работник удален успешно');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления работника');
      return false;
    }
    fetchCompany()
    return true;  
  };

  const deleteCompany = async() => {
    try {
      await companyService.deleteCompany();
      toast.success('Компания удалена успешно');
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления компании');
      return false;
    }
    fetchCompany()
    return true;    
  };

  const value = {
    company,
    createCompany,
    addWorker,
    deleteWorker,
    deleteCompany,
    updateCompany
  };

  return (
    <CompanyContext.Provider value={value}>
      {loading ? (
        <div className="">
          Loading...
        </div>
      ) : (
        children
      )}
    </CompanyContext.Provider>
  );
};