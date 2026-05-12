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
  getCompany: () => api.get('/company/me'),
  createCompany: (name) => api.post(`/company`, {name}),
  addWorkers: (email) => api.post(`/company/workers`, {email}),
  deleteWorkers: (email) => api.delete(`/company/workers`, {email}),
  deleteCompany: () => api.delete("/company")
};


export default api;
