// 允许在生产环境也使用 mock 数据
const debug =
  import.meta.env.MODE !== 'production' ||
  import.meta.env.VITE_USE_MOCK === 'true';

export default debug;
