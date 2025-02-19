// @ts-ignore
/* eslint-disable */
import { request } from '@umijs/max';
import { message } from 'antd';

/** 获取当前的用户 GET /api/currentUser */
export async function currentUser(options?: { [key: string]: any }) {
  return request<{
    data: API.CurrentUser;
  }>('/api/currentUser', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 退出登录接口 POST /api/login/outLogin */
export async function outLogin(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api/login/outLogin', {
    method: 'POST',
    ...(options || {}),
  });
}

/** 登录接口 POST /api/login/account */
export async function login(body: API.LoginParams, options?: { [key: string]: any }) {
  return request<API.LoginResult>('/api/login/account', {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    data: body,
    ...(options || {}),
  });
}

/** 此处后端没有提供注释 GET /api/notices */
export async function getNotices(options?: { [key: string]: any }) {
  return request<API.NoticeIconList>('/api/notices', {
    method: 'GET',
    ...(options || {}),
  });
}

/** 获取规则列表 GET /api/rule */
export async function rule(
  params: {
    // query
    /** 当前的页码 */
    current?: number;
    /** 页面的容量 */
    pageSize?: number;
  },
  options?: { [key: string]: any },
) {
  return request<API.RuleList>('/api/rule', {
    method: 'GET',
    params: {
      ...params,
    },
    ...(options || {}),
  });
}

/** 更新规则 PUT /api/rule */
export async function updateRule(options?: { [key: string]: any }) {
  return request<API.RuleListItem>('/api/rule', {
    method: 'POST',
    data: {
      method: 'update',
      ...(options || {}),
    }
  });
}

/** 新建规则 POST /api/rule */
export async function addRule(options?: { [key: string]: any }) {
  return request<API.RuleListItem>('/api/rule', {
    method: 'POST',
    data: {
      method: 'post',
      ...(options || {}),
    }
  });
}

/** 删除规则 DELETE /api/rule */
export async function removeRule(options?: { [key: string]: any }) {
  return request<Record<string, any>>('/api/rule', {
    method: 'POST',
    data: {
      method: 'delete',
      ...(options || {}),
    }
  });
}





/**
 * 统一post请求
 * @param url 请求url
 * @param params 参数
 * @param options 
 * @returns 
 */
export async function post<T>(url: string, params?: {}, options?: { [key: string]: any }) {
  return request<API.Response<T>>('/api' + url, {
    method: 'POST',
    data: {
      traceId: new Date().getTime(),
      ...params,
    },
    ...(options || {}),
  });
}


//使用递归的方式实现数组、对象的深拷贝
export function deepClone(obj: any) {
  let objClone: any = Array.isArray(obj) ? [] : {};
  if (obj && typeof obj === 'object') {
    for (var key in obj) {
      if (obj.hasOwnProperty(key)) {
        //判断ojb子元素是否为对象，如果是，递归复制
        if (obj[key] && typeof obj[key] === 'object') {
          objClone[key] = deepClone(obj[key]);
        } else {
          //如果不是，简单复制
          objClone[key] = obj[key];
        }
      }
    }
  }
  return objClone;
}

export function listToTree<T extends { id: number; parentId?: number;}>(  
  list: T[],  
  getTitleKey: (item: T) => string  
): API.TreeNode<T>[]{  
  const map: Record<number, API.TreeNode<T>> = {}; // 用于存储已转换的树节点  
  const rootNodes: API.TreeNode<T>[] = []; // 存储根节点（没有parentId的节点）  
  
  // 第一步：遍历菜单列表，构建map  
  list.forEach(item => {  
    const treeNode: API.TreeNode<T> = {  
      key: item.id,  
      value: item.id,  
      title: getTitleKey(item), // 使用getTitleKey来获取标题  
      ...item,  
    };  
    map[item.id ?? 0] = treeNode;  
  
    if (!item.parentId) {  
      // 如果没有parentId，则是根节点  
      rootNodes.push(treeNode);  
    }  
  });  
  
  list.forEach(item => {  
    const currentNode = map[item.id ?? 0];  
    if (item.parentId !== undefined) {  
      // 查找父节点并添加到其子节点数组中  
      const parentNode = map[item.parentId];  
      if (parentNode) {  
        if (!parentNode.children)  
          parentNode.children = [currentNode];  
        else  
          parentNode.children.push(currentNode);  
      }  
    }  
  });  
  
  return rootNodes;  
}; 