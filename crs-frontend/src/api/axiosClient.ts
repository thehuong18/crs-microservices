// path: crs-frontend/src/api/axiosClient.ts
// purpose: Request Interceptor (tu Buoi 7) + Response Interceptor (moi - xu ly 401)

import axios from 'axios';

const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

// Request Interceptor - tu Buoi 7, giu nguyen
axiosClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('crs_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Response Interceptor - MOI o Buoi 8: xu ly 401
axiosClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (axios.isAxiosError(error) && error.response?.status === 401) {
      localStorage.removeItem('crs_token');
      localStorage.removeItem('crs_user');
      // Dung window.location thay vi useNavigate() vi day la file thuan TS
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  }
);

export default axiosClient;
