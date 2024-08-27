import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, ExclamationCircleFilled, PlusOutlined, QuestionCircleOutlined, SettingOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Form, Input, message, Modal, Popconfirm, Row, Switch, Tooltip, TreeSelect } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import WorkflowEdit from './update'


export type SimpleUser = {
  id: number;
  realName: string;
  phone?: string;
}


const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);
  const [bingMenuModalOpen, handleBingMenuModalOpen] = useState<boolean>(false);
  const [userList, setUserList] = useState<API.TreeNode<any>[]>([]);
  const [jobTreeData, setJobTreeData] = useState<API.TreeNode<any>[]>([]);
  const actionRef = useRef<ActionType>();



  const [form] = Form.useForm();
  const [bingMenuForm] = Form.useForm();

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);

  useEffect(() => {
    post<SimpleUser[]>('/workflow/record/users').then((data) => {
      if (data.code === 0 && data.data) {
        const treeData:API.TreeNode<any>[] = [];
        data.data.map(n=>{
          treeData.push({
            key:n.id,
            value:n.id,
            title:n.realName+' '+n.phone
          })
        })
        setUserList(treeData)
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
      title: '流程名称',
      dataIndex: 'workflowName',
      render: (value, record) => {
        return <a href='#' onClick={() => {
          form.setFieldsValue(record)
          handleModalOpen(true);
        }}>{value}</a>;
      }
    },
    {
      title: '文本状态',
      search: false,
      dataIndex: 'workflowStatus',
      render(dom) {
        switch (dom) {
          case 0: return '草稿';
          case 1: return '发布';
        }
        return dom;
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      search: false,
      render(dom, record) {
        return <Switch checkedChildren="开启" unCheckedChildren="关闭"
          onChange={async () => {
            const res = await post('/workflow/record/save', { data: { ...record, status: dom === 1 ? 0 : 1 } })
            if (res.code === 0) {
              actionRef.current?.reload();
            }
          }}
          checked={dom === 1} />;
      },
    }
  ];
  const { confirm } = Modal;
  return (
    <PageContainer>
      <Modal title={'编辑流程'} width={1000} open={modalOpen} onOk={() => { }}
        onCancel={() => {
          confirm({
            title: '确定要取消吗？',
            icon: <ExclamationCircleFilled />,
            content: '您的更改尚未保存，确定要离开吗？',
            onOk() {
              handleModalOpen(false)
            },
            onCancel() {
            },
          });

        }}
      >
        <WorkflowEdit users={userList} jobs={jobTreeData}/>
      </Modal>
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
              const res = await post('/workflow/record/delete', { data: [...selectedRowKeys] })
              if (res.code === 0) {
                message.info('删除成功');
                actionRef.current?.reload();
              }
            }}
          ><Button danger > <DeleteOutlined />删除 </Button>
          </Popconfirm>
        ]}
        columns={columns}
        rowKey={'id'}
        rowSelection={{
          onChange: (selectedRowKeys) => {
            setSelectedRowKeys(selectedRowKeys);
          },
        }}
        request={async (params) => {
          const res = await post<API.Page<any>>('/workflow/record/page', { pageSize: params.pageSize, current: params.current, data: { ...params } });
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
