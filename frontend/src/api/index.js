import axios from 'axios';
import toast from 'react-hot-toast';

export const API_URL = '/api';
export const AUTH_URL = '/auth';

const api = axios.create({
  baseURL: API_URL // Uses proxy in vite config
});

const authApi = axios.create({
  baseURL: AUTH_URL // For signup/login
});

// Request interceptor to attach token
api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('accessToken');

    if (token) {
      config.headers = {
        ...config.headers,
        Authorization: `Bearer ${token}`,
      };
    }

    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor to handle 401
api.interceptors.response.use(
  (response) => response,
  (error) => {
    // DO NOT redirect here to prevent race conditions during OAuth load
    return Promise.reject(error);
  }
);

export const authService = {
  login: (data) => authApi.post('/login', data),
  signup: (data) => authApi.post('/signup', data),
};

export const userService = {
  getProfile: () => api.get('/users/profile'),
  updateProfile: (name, username, email, newPassword, confirmPassword) => api.put(`/users/profile`, {name, username, email, newPassword, confirmPassword}),
  changePassword: (data) => api.put('/users/change-password', data),
  forgotPassword: (email) => api.post(`/users/forgot-password?email=${email}`),
  deleteAccount: () => api.delete("/users/profile")
};

export const companyService = {
  getCompany: () => api.get('/company'),
  createCompany: (name) => api.post(`/company`, {name}),
  addWorkers: (email) => api.post(`/company/workers`, {email}),
  deleteWorkers: (id) => api.delete(`/company/workers/${id}`),
  deleteCompany: () => api.delete("/company"),
  updateCompany: (name) => api.put('/company', {name})
};

export const projectService = {
  getAllProjects: () => api.get('/projects'),
  getProjectById: (id) => api.get(`/projects/${id}`),
  createProject: (name) => api.post(`/projects`, {name}),
  deleteProjectById: (id) => api.delete(`/projects/${id}`),
  addWorkerInProjectById: (id, userId) => api.post(`/projects/${id}/members/${userId}`),
};

export const boardService = {
  getBoards: (id) => api.get(`/projects/${id}/boards`),
  createBoard: (id, name) => api.post(`/projects/${id}/boards`, {name}),
  deleteBoard: (id, boardId) => api.delete(`/projects/${id}/boards/${boardId}`),
  updateBoard: (id, boardId, name) => api.put(`/projects/${id}/boards/${boardId}`, {name})
};

export const taskService = {
  getTasks: (id) => api.get(`/projects/boards/${id}/tasks`),
  createTask: (id, title, description, status) => api.post(`/projects/boards/${id}/tasks`, {title, description, status}),
  deleteTask: (id, taskId) => api.delete(`/projects/tasks/${taskId}`),
  updateTask: (id, taskId, title, description, status) => api.put(`/projects/tasks/${taskId}`, {title, description, status})
};


export default api;
