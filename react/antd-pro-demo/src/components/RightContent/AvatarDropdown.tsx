import { post } from '@/services/ant-design-pro/api';
import { ExclamationCircleFilled, FormOutlined, LogoutOutlined, SettingOutlined, UploadOutlined, UserOutlined } from '@ant-design/icons';
import { history, useModel } from '@umijs/max';
import { Button, Card, Col, Form, GetProp, Input, Modal, Row, Spin, Upload, UploadFile, UploadProps } from 'antd';
import { createStyles } from 'antd-style';
import { stringify } from 'querystring';
import type { MenuInfo } from 'rc-menu/lib/interface';
import React, { useCallback, useState } from 'react';
import { flushSync } from 'react-dom';
import HeaderDropdown from '../HeaderDropdown';
import ImgCrop from 'antd-img-crop';

type FileType = Parameters<GetProp<UploadProps, 'beforeUpload'>>[0];
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
  const [editUserModalOpen, setEditUserModalOpen] = useState<boolean>(false);
  const [editUserForm] = Form.useForm();
  const { initialState, setInitialState } = useModel('@@initialState');
  const [fileList, setFileList] = useState<UploadFile[]>([
    {
      uid: '-1',
      name: 'image.png',
      status: 'done',
      url: '/api/' + initialState?.currentUser?.avatar,
    },
  ]);
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
      if (key === 'center') {
        editUserForm.setFieldsValue({
          realName: initialState?.currentUser?.name,
          email: initialState?.currentUser?.email,
          phone: initialState?.currentUser?.phone,
        })
        setEditUserModalOpen(true);
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
      key: 'center',
      icon: <UserOutlined />,
      label: '个人中心',
    },
    {
      key: 'resetPassword',
      icon: <FormOutlined />,
      label: '重置密码',
    },
  ];


  const onChange: UploadProps['onChange'] = ({ file: file, fileList: newFileList }) => {
    if (file.status === 'done' && file.response.code === 0 && file.response.data) {
      setInitialState((s) => ({ ...s, currentUser: { ...currentUser, avatar: file.response.data } }));
    }
    setFileList(newFileList);
  };

  const onPreview = async (file: UploadFile) => {
    let src = file.url as string;
    if (!src) {
      src = await new Promise((resolve) => {
        const reader = new FileReader();
        reader.readAsDataURL(file.originFileObj as FileType);
        reader.onload = () => resolve(reader.result as string);
      });
    }
    const image = new Image();
    image.src = src;
    const imgWindow = window.open(src);
    imgWindow?.document.write(image.outerHTML);
  };
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
      {/* 这里的个人信息比较少，所以没有单独弄一个页面，如果信息较多，还是建议单独列页面 */}
      <Modal title={'修改个人信息'} open={editUserModalOpen} onOk={() => {
        editUserForm.submit();
      }}
        onCancel={() => {
          setEditUserModalOpen(false);
          restPwdForm.resetFields();
        }}
      >
        <Row gutter={24}>
          <Col span={10}>
            <Card style={{ width: '160px' }}>
              <ImgCrop rotationSlider>
                <Upload
                  headers={{ Authorization: sessionStorage.getItem('Authorization') }}
                  listType="picture-card"
                  fileList={fileList}
                  onChange={onChange}
                  onPreview={onPreview}
                  action={'/api/user/uploadAvatar'}
                >
                  {fileList.length < 1 && '+ Upload'}
                </Upload>
              </ImgCrop>
            </Card>
          </Col>
          <Col span={14}>
            <Form form={editUserForm} onFinish={(val: any) => {
              const res = post('/user/updateSelf', { data: val });
              res.then((re) => {
                if (re.code === 0) {
                  confirm({
                    title: '提示',
                    icon: <ExclamationCircleFilled />,
                    content: '修改成功，请刷新页面',
                    onOk() {

                    },
                  });
                  setEditUserModalOpen(false);
                }
              });
            }}
            >
              <Form.Item
                name={'realName'}
                label={'姓名'}
                rules={[{ required: true }]}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name={'phone'}
                label={'手机号'}
              >
                <Input />
              </Form.Item>
              <Form.Item
                name={'email'}
                label={'邮箱'}>
                <Input />
              </Form.Item>

            </Form>
          </Col>
        </Row>

      </Modal>
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
              validator(_, value) {
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
              validator(_, value) {
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
