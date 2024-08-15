import { Footer, Question, SelectLang, AvatarDropdown, AvatarName } from '@/components';
import { LinkOutlined } from '@ant-design/icons';
import type { Settings as LayoutSettings, MenuDataItem } from '@ant-design/pro-components';
import { SettingDrawer } from '@ant-design/pro-components';
import type { RequestConfig, RunTimeLayoutConfig } from '@umijs/max';
import { history, Link } from '@umijs/max';
import defaultSettings from '../config/defaultSettings';
import { errorConfig } from './requestErrorConfig';
import { post } from '@/services/ant-design-pro/api';
import React from 'react';
import * as allIcons from '@ant-design/icons';
import { Row } from 'antd';
import CreateIcon from './components/common/CreateIcon';
const isDev = process.env.NODE_ENV === 'development';
const loginPath = '/user/login';



type UserCache = {
  username: string;
  avatar: string;
  token: string;
  menuList?: API.MenuData[]
}
/**
 * @see  https://umijs.org/zh-CN/plugins/plugin-initial-state
 * */
export async function getInitialState(): Promise<{
  settings?: Partial<LayoutSettings>;
  currentUser?: API.CurrentUser;
  loading?: boolean;
  fetchUserInfo?: () => Promise<API.CurrentUser | undefined>;
}> {
  const fetchUserInfo = async () => {
    try {
      const msg = await post<UserCache>('/auth/current', {})
      if (msg.data) {
        return {
          name: msg.data.username,
          token: msg.data.token,
          avatar: process.env.baseUrl + msg.data.avatar,
          menuList: msg.data?.menuList,
        };
      }
      history.push(loginPath);
    } catch (error) {
      history.push(loginPath);
    }
    return undefined;
  };
  // 如果不是登录页面，执行
  const { location } = history;
  if (location.pathname !== loginPath) {
    const currentUser = await fetchUserInfo();
    return {
      fetchUserInfo,
      currentUser,
      settings: defaultSettings as Partial<LayoutSettings>,
    };
  }
  return {
    fetchUserInfo,
    settings: defaultSettings as Partial<LayoutSettings>,
  };
}


function listToTree(menus: API.MenuData[]): MenuDataItem[] {
  // 创建一个map来存储所有节点，key为id  
  const menuMap: Record<number, MenuDataItem> = {};
  // 创建一个数组来存储根节点（即没有parentId的节点）  
  const rootList: MenuDataItem[] = [];

  // 第一步：初始化节点并放入map中  
  menus.forEach(menu => {
    const menuItemId = menu.id;
    const menuItem: MenuDataItem = {
      name: menu.menuName,
      key: menu.menuName,
      icon: menu.menuIcon ? <div style={{ marginRight: 5 }}><CreateIcon name={menu.menuIcon} /></div> : undefined, // 假设转换为React元素  
      path: menu.menuPath,
    };
    menuMap[menuItemId] = menuItem;

    // 如果没有parentId，则认为是根节点  
    if (!menu.parentId) {
      rootList.push(menuItem);
    }
  });

  // 第二步：构建树形结构  
  menus.forEach(menu => {
    const menuItemId = menu.id;
    const currentItem = menuMap[menuItemId];

    // 如果当前节点有parentId，则找到其父节点并添加到其children中  
    if (menu.parentId !== undefined) {
      const parentId = menu.parentId;
      if (menuMap[parentId]) {
        if (menuMap[parentId].children)
          menuMap[parentId].children.push(currentItem);
        else menuMap[parentId].children = [currentItem]
      }
    }
  });

  return rootList;
}


// ProLayout 支持的api https://procomponents.ant.design/components/layout
export const layout: RunTimeLayoutConfig = ({ initialState, setInitialState }) => {
  return {
    actionsRender: () => [<Question key="doc" />, <SelectLang key="SelectLang" />],
    avatarProps: {
      src: initialState?.currentUser?.avatar,
      title: <AvatarName />,
      render: (_, avatarChildren) => {
        return <AvatarDropdown>{avatarChildren}</AvatarDropdown>;
      },
    },
    waterMarkProps: {
      content: initialState?.currentUser?.name,
    },
    footerRender: () => <Footer />,
    onPageChange: () => {
      const { location } = history;
      // 如果没有登录，重定向到 login
      if (!initialState?.currentUser && location.pathname !== loginPath) {
        history.push(loginPath);
      }
    },
    menuItemRender: (menuItemProps, defaultDom) => {
      if (menuItemProps.isUrl || !menuItemProps.path) {
        return defaultDom;
      }
      return (
        <Link to={menuItemProps.path}>
          <Row>{menuItemProps.pro_layout_parentKeys &&
            menuItemProps.pro_layout_parentKeys.length > 0 &&
            menuItemProps.icon}
            {defaultDom}
          </Row>
        </Link>
      );
    },
    menu: {
      params: initialState,
      locale: false,
      request: async (_,) => {
        if (initialState?.currentUser && initialState.currentUser?.menuList)
          return listToTree(initialState?.currentUser?.menuList);
        return [];
      },
    },
    // bgLayoutImgList: [
    //   {
    //     src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/D2LWSqNny4sAAAAAAAAAAAAAFl94AQBr',
    //     left: 85,
    //     bottom: 100,
    //     height: '303px',
    //   },
    //   {
    //     src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/C2TWRpJpiC0AAAAAAAAAAAAAFl94AQBr',
    //     bottom: -68,
    //     right: -45,
    //     height: '303px',
    //   },
    //   {
    //     src: 'https://mdn.alipayobjects.com/yuyan_qk0oxh/afts/img/F6vSTbj8KpYAAAAAAAAAAAAAFl94AQBr',
    //     bottom: 0,
    //     left: 0,
    //     width: '331px',
    //   },
    // ],
    links: isDev
      ? [
        <Link key="openapi" to="/umi/plugin/openapi" target="_blank">
          <LinkOutlined />
          <span>OpenAPI 文档</span>
        </Link>,
      ]
      : [],
    menuHeaderRender: undefined,
    // 自定义 403 页面
    // unAccessible: <div>unAccessible</div>,
    // 增加一个 loading 的状态
    childrenRender: (children) => {
      // if (initialState?.loading) return <PageLoading />;
      return (
        <>
          {children}
          {isDev && (
            <SettingDrawer
              disableUrlParams
              enableDarkTheme
              settings={initialState?.settings}
              onSettingChange={(settings) => {
                setInitialState((preInitialState) => ({
                  ...preInitialState,
                  settings,
                }));
              }}
            />
          )}
        </>
      );
    },
    ...initialState?.settings,
  };
};


/**
 * @name request 配置，可以配置错误处理
 * 它基于 axios 和 ahooks 的 useRequest 提供了一套统一的网络请求和错误处理方案。
 * @doc https://umijs.org/docs/max/request#配置
 */
export const request = {
  ...errorConfig,
};
