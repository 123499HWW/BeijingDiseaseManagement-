import axios from 'axios';

const baseURL = 'http://localhost:8080/api'; // 后端API基础地址，实际部署时可调整

const request = axios.create({
  baseURL,
  timeout: 10000,
});

export default request;
