import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, PlusOutlined, QuestionCircleOutlined, SettingOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Form, Input, message, Popconfirm, Row, Switch, Tooltip, TreeSelect } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import FormSubmitModal from '@/components/common/FormSubmitModal';
import FromTreeSelect, { FromTreeItem } from '@/components/common/FromTreeSelect';






const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);
  const [bingMenuModalOpen, handleBingMenuModalOpen] = useState<boolean>(false);
  const [roleData, setRoleData] = useState<{ key: number, title: string, value: any }[]>([]);
  const [jobTreeData, setJobTreeData] = useState<API.TreeNode<any>[]>([]);
  const actionRef = useRef<ActionType>();



  const [form] = Form.useForm();
  const [bingMenuForm] = Form.useForm();

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);

  useEffect(() => {
    post<{ id: number, roleName: string }[]>('/user/findRoles').then((data) => {
      if (data.code === 0 && data.data) {
        const listData: { key: number, title: string, value: any }[] = []
        data.data.forEach(item => {
          listData.push({
            key: item.id,
            value: item.id,
            title: item.roleName
          })
        });
        setRoleData(listData)
      }
    })
    post<API.TreeNode<any>[]>('/user/findDeptAndJobs').then((data) => {
      if (data.code === 0 && data.data) {
        setJobTreeData(data.data)
      }
    })
  }, [])


  const columns: ProColumns<any>[] = [
    {
      title: '用户姓名',
      dataIndex: 'realName',
      render: (value, record) => {
        return <Row><Tooltip title='绑定角色岗位'>
          <Button shape="circle" size='small' icon={<SettingOutlined />} style={{ marginRight: 10 }}
            onClick={async () => {
              const res = await post<any>('/user/findBindJobAndRole', { data: record.id });
              if (res.code === 0) {
                handleBingMenuModalOpen(true);
                bingMenuForm.setFieldsValue(res.data);
              }
            }}
          />
        </Tooltip><a href='#' onClick={() => {
          form.setFieldsValue(record)
          handleModalOpen(true);
        }}>{value}</a></Row>;
      }
    },
    {
      title: '账户名',
      dataIndex: 'username'
    },
    {
      title: '状态',
      dataIndex: 'status',
      search: false,
      render(dom, record) {
        return <Switch checkedChildren="开启" unCheckedChildren="关闭"
          onChange={async () => {
            const res = await post('/user/save', { data: { ...record, status: dom === 1 ? 0 : 1 } })
            if (res.code === 0) {
              actionRef.current?.reload();
            }
          }}
          checked={dom === 1} />;
      },
    },
    {
      title: '手机号',
      dataIndex: 'phone'
    },
    {
      title: '岗位',
      dataIndex: 'jobs',
      search: false
    },
    {
      title: '角色',
      dataIndex: 'roles',
      search: false
    }
  ];
  return (
    <PageContainer>
      <FormSubmitModal title="编辑用户" open={bingMenuModalOpen} onOk={() => { bingMenuForm.submit(); }}
        onCancel={() => { handleBingMenuModalOpen(false); }} >
        <Form form={bingMenuForm} onFinish={(val: any) => {
          const res = post('/user/bindJobAndRole', { data: val });
          res.then((re) => {
            if (re.code === 0) {
              handleBingMenuModalOpen(false);
              bingMenuForm.resetFields();
              actionRef.current?.reload()
            }
          });
        }}>
          <Form.Item
            name={'userId'}
            rules={[{ required: true }]}
            hidden={true}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'roleIds'}
            label={'角色'}
            rules={[{ required: true }]}
          >
            <TreeSelect treeData={roleData} treeCheckable/>
          </Form.Item>
          <Form.Item
            name={'jobIds'}
            label={'岗位'}
            rules={[{ required: true }]}
          >
            <TreeSelect treeData={jobTreeData} treeCheckable/>
          </Form.Item>
        </Form>
      </FormSubmitModal>
      <FormSubmitModal title="编辑用户" open={modalOpen} onOk={() => { form.submit(); }}
        onCancel={() => { handleModalOpen(false); }}>
        <Form form={form} onFinish={(val: any) => {
          const res = post('/user/save', { data: val });
          res.then((re) => {
            if (re.code === 0) {
              handleModalOpen(false);
              form.resetFields();
              actionRef.current?.reload()
            }
          });
        }}>
          <Form.Item name={'id'} label={'id'} hidden={true}>
            <Input />
          </Form.Item>
          <Form.Item
            name={'realName'}
            label={'用户名称'}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'username'}
            label={'账户名称'}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'phone'}
            label={'手机号'}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'email'}
            label={'邮箱'}
          >
            <Input />
          </Form.Item>
        </Form>
      </FormSubmitModal>
      <ProTable
        actionRef={actionRef}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              handleModalOpen(true);
            }}
          >
            <PlusOutlined />新建
          </Button>, <Popconfirm
            title="删除数据"
            description="是否删除当前数据?"
            icon={<QuestionCircleOutlined style={{ color: 'red' }} />}
            onConfirm={async () => {
              const res = await post('/user/delete', { data: [...selectedRowKeys] })
              if (res.code === 0) {
                message.info('删除成功');
                actionRef.current?.reload();
              }
            }}
          ><Button danger > <DeleteOutlined />删除 </Button>
          </Popconfirm>
        ]}
        columns={columns}
        rowSelection={{
          onChange: (selectedRowKeys) => {
            setSelectedRowKeys(selectedRowKeys);
          },
        }}
        request={async (params) => {
          const res = await post<API.Page<any>>('/user/page', { pageSize: params.pageSize, current: params.current, data: { ...params } });
          if (res && res.data) {
            return {
              data: res.data.list,
              total: res.data.total,
              success: true
            };
          }
          return {};
        }}
      />
    </PageContainer>
  );
};

export default Page;
