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

  useEffect(() => {
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

    fetchCompany();
  }, [token]);

  const createCompany = async (name) => {
    try {
      await companyService.createCompany(name);
      toast.success('Создание прошло успешно');
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка создания');
      return false;
    }
    fetchCompany()
  };

  const addWorkers = async (email) => {
    try {
      await companyService.addWorkers(email);
      toast.success('Работник добавлен успешно');
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка добавления нового работника');
      return false;
    }
    fetchCompany()
  };

  const deleteWorkers = async(email) => {
    try {
      await companyService.deleteWorkers(email);
      toast.success('Работник удален успешно');
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления работника');
      return false;
    }
    fetchCompany()
  };

  const deleteCompany = async() => {
    try {
      await companyService.deleteCompany();
      toast.success('Компания удалена успешно');
      return true;
    } catch (error) {
      toast.error(error.response?.data?.message || 'Ошибка удаления компании');
      return false;
    }
    fetchCompany()
  };

  const value = {
    company,
    createCompany,
    addWorkers,
    deleteWorkers,
    deleteCompany
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