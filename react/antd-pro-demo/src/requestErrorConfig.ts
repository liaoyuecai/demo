import type { RequestOptions } from '@@/plugin-request/request';
import type { RequestConfig } from '@umijs/max';
import { message, notification } from 'antd';
import { history } from '@umijs/max';
import { stringify } from 'querystring';
// 错误处理方案： 错误类型
enum ErrorShowType {
  SILENT = 0,
  WARN_MESSAGE = 1,
  ERROR_MESSAGE = 2,
  NOTIFICATION = 3,
  REDIRECT = 9,
}
// 与后端约定的响应数据格式
interface ResponseStructure {
  code: number;
  data: any;
  errorMsg?: string;
}

/**
 * @name 错误处理
 * pro 自带的错误处理， 可以在这里做自己的改动
 * @doc https://umijs.org/docs/max/request#配置
 */
export const errorConfig: RequestConfig = {
  // 错误处理： umi@3 的错误处理方案。
  errorConfig: {
    // 错误抛出
    errorThrower: (res) => {
      const { code, data, errorMsg } =
        res as unknown as ResponseStructure;
      if (code !== 0) {
        const error: any = new Error(errorMsg);
        error.name = 'BizError';
        error.info = { code, errorMsg, data };
        throw error; // 抛出自制的错误
      }
    },
    // 错误接收及处理
    errorHandler: (error: any, opts: any) => {
      if (opts?.skipErrorHandler) throw error;
      // 我们的 errorThrower 抛出的错误。
      if (error.name === 'BizError') {
        const errorInfo: ResponseStructure | undefined = error.info;
        if (errorInfo) {
          const { errorMsg, code } = errorInfo;
          message.error(errorMsg);
        }
      } else if (error.response) {
        // Axios 的错误
        // 请求成功发出且服务器也响应了状态码，但状态代码超出了 2xx 的范围
        console.error(`Response status:${error.response.status}`);
        message.error(`系统错误，请联系管理员！`);
      } else if (error.request) {
        // 请求已经成功发起，但没有收到响应
        // \`error.request\` 在浏览器中是 XMLHttpRequest 的实例，
        // 而在node.js中是 http.ClientRequest 的实例
        console.error('None response! Please retry.');
        message.error(`系统错误，请联系管理员！`);
      } else {
        // 发送请求时出了点问题
        console.error('Request error, please retry.');
        message.error(`服务器繁忙，请稍后尝试！`);
      }
    },
  },

  // 请求拦截器
  requestInterceptors: [
    (config: RequestOptions) => {
      let authorization = sessionStorage.getItem('Authorization');
      if (authorization)
        config.headers = { ...config.headers, Authorization: authorization }
      // 拦截请求配置，进行个性化处理。
      const url = config?.url;
      return { ...config, url };
    },

  ],

  // 响应拦截器
  responseInterceptors: [
    (response) => {
      // 拦截响应数据，进行个性化处理
      const { data } = response as unknown as ResponseStructure;
      if (data?.code !== 0) {
        if (data?.code === 3002) {
          message.error('登录信息超时，请重新登录')
          const { search, pathname } = window.location;
          const urlParams = new URL(window.location.href).searchParams;
          /** 此方法会跳转到 redirect 参数所在的位置 */
          const redirect = urlParams.get('redirect');
          // Note: There may be security issues, please note
          if (window.location.pathname !== '/user/login' && !redirect) {
            history.replace({
              pathname: '/user/login',
              search: stringify({
                redirect: pathname + search,
              }),
            });
          }
        } else {
          message.error(data.errorMsg)
        }
      }
      return response;
    },
  ],
};
