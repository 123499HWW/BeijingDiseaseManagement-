import debug from './env';

export default ({ mock, setup }: { mock?: boolean; setup: () => void }) => {
  // 允许在所有环境下使用 mock 数据
  if (mock !== false) setup();
};

export const successResponseWrap = (data: unknown) => {
  return {
    data,
    status: 'ok',
    msg: '请求成功',
    code: 20000,
  };
};

export const failResponseWrap = (data: unknown, msg: string, code = 50000) => {
  return {
    data,
    status: 'fail',
    msg,
    code,
  };
};
