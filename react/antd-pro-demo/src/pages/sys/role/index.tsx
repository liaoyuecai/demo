import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, PlusOutlined, QuestionCircleOutlined, SettingOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Form, Input, message, Popconfirm, Row, Select, Switch, Tooltip } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import FormSubmitModal from '@/components/common/FormSubmitModal';
import FromTreeSelect, { FromTreeItem } from '@/components/common/FromTreeSelect';






const RolePage: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);
  const [bingMenuModalOpen, handleBingMenuModalOpen] = useState<boolean>(false);
  const [menuTreeData, setMenuTreeData] = useState<FromTreeItem[]>([]);
  const actionRef = useRef<ActionType>();



  const [form] = Form.useForm();
  const [bingMenuForm] = Form.useForm();

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);

  useEffect(() => {
    post<{ id: number, parentId?: number, menuName: string }[]>('/menu/own').then((data) => {
      if (data.code === 0 && data.data) {
        const treeData: FromTreeItem[] = []
        const treeMap: Record<number, FromTreeItem> = {};
        data.data.forEach(item => {
          const id = item.id;
          const treeItem: FromTreeItem = {
            key: item.id,
            parentKey: item.parentId,
            value: item.id,
            title: item.menuName
          };
          treeMap[id] = treeItem;
          if (!item.parentId) {
            treeData.push(treeItem);
          }
        });
        data.data.forEach(item => {
          const id = item.id;
          const currentItem = treeMap[id];
          if (item.parentId !== undefined) {
            const parentId = item.parentId;
            if (treeMap[parentId]) {
              if (treeMap[parentId].children)
                treeMap[parentId].children.push(currentItem);
              else treeMap[parentId].children = [currentItem]
            }
          }
        });
        setMenuTreeData(treeData)
      }
    })
  }, [])


  const columns: ProColumns<any>[] = [
    {
      title: '角色名称',
      dataIndex: 'roleName',
      render: (value, record) => {
        return <Row><Tooltip title='绑定菜单'>
          <Button shape="circle" size='small' icon={<SettingOutlined />} style={{ marginRight: 10 }}
            onClick={async () => {

              const res = await post<number[]>('/role/findBindMenus', { data: record.id });
              if (res.code === 0) {
                handleBingMenuModalOpen(true);
                bingMenuForm.setFieldsValue({ roleId: record.id, menuIds: res.data });
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
      title: '状态',
      dataIndex: 'status',
      search: false,
      render(dom, record) {
        return <Switch checkedChildren="开启" unCheckedChildren="关闭"
          onChange={async () => {
            const res = await post('/role/save', { data: { ...record, status: dom === 1 ? 0 : 1 } })
            if (res.code === 0) {
              actionRef.current?.reload();
            }
          }}
          checked={dom === 1} />;
      },
    },
    {
      title: '角色编码',
      dataIndex: 'roleKey'
    },
    {
      title: '角色类型',
      dataIndex: 'roleType',
      render(dom) {
        switch (dom) {
          case 1: return '公共角色'
          case 2: return '非公共角色'
        }
        return ''
      }
    },
    {
      title: '绑定菜单',
      dataIndex: 'menus',
      search: false
    }
  ];
  return (
    <PageContainer>
      <FormSubmitModal title="编辑角色" open={bingMenuModalOpen} onOk={() => { bingMenuForm.submit(); }}
        onCancel={() => { handleBingMenuModalOpen(false); }} >
        <Form form={bingMenuForm} onFinish={(val: any) => {
          const res = post('/role/bindMenu', { data: val });
          res.then((re) => {
            if (re.code === 0) {
              handleBingMenuModalOpen(false);
              bingMenuForm.resetFields();
              actionRef.current?.reload()
            }
          });
        }}>
          <Form.Item
            name={'roleId'}
            rules={[{ required: true }]}
            hidden={true}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'menuIds'}
            label={'菜单'}
            rules={[{ required: true }]}
          >
            <FromTreeSelect treeData={menuTreeData} />
          </Form.Item>
        </Form>
      </FormSubmitModal>
      <FormSubmitModal title="编辑角色" open={modalOpen} onOk={() => { form.submit(); }}
        onCancel={() => { handleModalOpen(false); }}>
        <Form form={form} onFinish={(val: any) => {
          const res = post('/role/save', { data: val });
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
            name={'roleName'}
            label={'角色名称'}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'roleKey'}
            label={'角色编码'}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'roleType'}
            label={'角色类型'}
            initialValue={2}
            rules={[{ required: true }]}
          >
            <Select >
              <Select.Option value={1}>公共角色</Select.Option>
              <Select.Option value={2}>非公共角色</Select.Option>
            </Select>
          </Form.Item>
          <Form.Item
            name={'description'}
            label={'角色描述'}
          >
            <Input.TextArea />
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
              const res = await post('/role/delete', { data: [...selectedRowKeys] })
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
          const res = await post<API.Page<any>>('/role/page', { pageSize: params.pageSize, current: params.current, data: { ...params } });
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

export default RolePage;
