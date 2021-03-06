import axios from 'axios';

export const instance = axios.create({
  baseURL: process.env.REACT_APP_API_END_POINT,
  timeout: 5000,
  withCredentials: true,
});

instance.interceptors.response.use(
  (response) => {
    return response;
  },
  async (error) => {
    const {
      config,
      response: { status },
    } = error;

    if (status === 403 && error.response.data === 'access token expired') {
      const originalRequest = config;
      instance.defaults.headers.common['Authorization'] = '';
      const res = await instance.post(`/silent-refresh`);
      instance.defaults.headers.common['Authorization'] = res.data.accessToken;
      originalRequest.headers['Authorization'] = res.data.accessToken;
      return axios(originalRequest);
    }

    return Promise.reject(error);
  },
);
