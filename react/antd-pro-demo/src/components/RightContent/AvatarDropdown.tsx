import { post } from '@/services/ant-design-pro/api';
import { ExclamationCircleFilled, FormOutlined, LogoutOutlined, SettingOutlined, UserOutlined } from '@ant-design/icons';
import { history, useModel } from '@umijs/max';
import { Form, Input, Modal, Spin } from 'antd';
import { createStyles } from 'antd-style';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback, useState } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';

export type GlobalHeaderRightProps = {
  menu?: boolean;
  children?: React.ReactNode;
};

export const AvatarName = () => {
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState || {};
  return <span className="anticon">{currentUser?.name}</span>;
};

const useStyles = createStyles(({ token }) => {
  return {
    action: {
      display: 'flex',
      height: '48px',
      marginLeft: 'auto',
      overflow: 'hidden',
      alignItems: 'center',
      padding: '0 8px',
      cursor: 'pointer',
      borderRadius: token.borderRadius,
      '&:hover': {
        backgroundColor: token.colorBgTextHover,
      },
    },
  };
});

export const AvatarDropdown: React.FC<GlobalHeaderRightProps> = ({ menu, children }) => {
  const { confirm } = Modal;
  const [restPwdModalOpen, setRestPwdModalOpen] = useState<boolean>(false);
  const [restPwdForm] = Form.useForm();
  /**
   * 退出登录，并且将当前的 url 保存
   */
  const loginOut = async () => {
    await post('/auth/logout', {});
    sessionStorage.removeItem('Authorization')
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
  };
  const { styles } = useStyles();

  const { initialState, setInitialState } = useModel('@@initialState');

  const onMenuClick = useCallback(
    (event: MenuInfo) => {
      const { key } = event;
      if (key === 'logout') {
        flushSync(() => {
          setInitialState((s) => ({ ...s, currentUser: undefined }));
        });
        loginOut();
        return;
      }
      if (key === 'resetPassword') {
        setRestPwdModalOpen(true);
        return;
      }
      history.push(`/account/${key}`);
    },
    [setInitialState],
  );

  const loading = (
    <span className={styles.action}>
      <Spin
        size="small"
        style={{
          marginLeft: 8,
          marginRight: 8,
        }}
      />
    </span>
  );

  if (!initialState) {
    return loading;
  }

  const { currentUser } = initialState;

  if (!currentUser || !currentUser.name) {
    return loading;
  }

  const menuItems = [
    ...(menu
      ? [
        {
          key: 'center',
          icon: <UserOutlined />,
          label: '个人中心',
        },
        {
          key: 'settings',
          icon: <SettingOutlined />,
          label: '个人设置',
        },
        {
          type: 'divider' as const,
        },
      ]
      : []),
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
    },
    {
      key: 'resetPassword',
      icon: <FormOutlined />,
      label: '重置密码',
    },
  ];

  return (
    <div>
      <HeaderDropdown
        menu={{
          selectedKeys: [],
          onClick: onMenuClick,
          items: menuItems,
        }}
      >
        {children}

      </HeaderDropdown>
      <Modal title={'重置密码'} open={restPwdModalOpen} onOk={() => {
        restPwdForm.submit();
      }}
        onCancel={() => {
          setRestPwdModalOpen(false);
          restPwdForm.resetFields();
        }}
      >
        <Form form={restPwdForm} onFinish={(val: any) => {
          const res = post('/user/resetPassword', { data: val });
          res.then((re) => {
            if (re.code === 0) {
              confirm({
                title: '提示',
                icon: <ExclamationCircleFilled />,
                content: '重置密码成功，您需要重新登录！',
                onOk() {
                  restPwdForm.resetFields();
                  flushSync(() => {
                    setInitialState((s) => ({ ...s, currentUser: undefined }));
                  });
                  loginOut();
                }
              });

            }
          });
        }}
        >
          <Form.Item
            name={'oldPassword'}
            label={'旧密码'}
            rules={[{ required: true }]}
          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            name={'newPassword'}
            label={'新密码'}
            rules={[{ required: true },
            { pattern: new RegExp('^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[#?!@$%^_()&*-]).{8,}$'), message: '密码中必须包含数字、大小写字母和特殊符号，且长度不能小于8位' },
            ({ getFieldValue }) => ({
              validator(role, value) {
                //这里使用==，使用===时特殊符号匹配不上
                if (value == getFieldValue('oldPassword')) {
                  return Promise.reject('新密码不能与旧密码一致');
                }
                return Promise.resolve();
              }
            })
            ]}

          >
            <Input.Password />
          </Form.Item>
          <Form.Item
            name={'newPasswordAgain'}
            label={'重复密码'}
            rules={[{ required: true, message: '重复密码不能为空', },
            ({ getFieldValue }) => ({
              validator(role, value) {
                if (value !== getFieldValue('newPassword')) {
                  return Promise.reject('两次密码不一致');
                }
                return Promise.resolve();
              }
            })
            ]}
          >
            <Input.Password />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
};
