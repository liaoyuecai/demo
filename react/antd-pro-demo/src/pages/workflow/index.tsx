import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, ExclamationCircleFilled, PlusOutlined, QuestionCircleOutlined, SettingOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button,  Input, message, Modal, Popconfirm, Row, Switch} from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import WorkflowEdit from './update'
import { set } from 'lodash';


export type SimpleUser = {
  id: number;
  realName: string;
  phone?: string;
}


const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);
  const [userList, setUserList] = useState<API.TreeNode<any>[]>([]);
  const [jobTreeData, setJobTreeData] = useState<API.TreeNode<any>[]>([]);
  const [workflowName, setWorkflowName] = useState<string>();
  const [workflow, setWorkflow] = useState<any>();
  const actionRef = useRef<ActionType>();


  const editer = useRef<{ nodes: any[]}>();

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);
  const [currentNodes, setCurrentNodes] = useState<string>();

  const [workflowTreeData,setWorkflowTreeData] = useState<API.TreeNode<any>[]>([]);

  const reloadWorkflowTreeData = ()=>{
    post<{id:number,workflowName:string}[]>('/workflow/record/findRecords').then(data=>{
      if (data.code === 0 && data.data) {
        const nodes:API.TreeNode<any>[] = [];
        data.data.map(d=>{
          nodes.push({key:d.id,title:d.workflowName,value:d.id})
        })
        setWorkflowTreeData(nodes)
      }
    })
  }

  useEffect(() => {
    post<SimpleUser[]>('/workflow/record/users').then((data) => {
      if (data.code === 0 && data.data) {
        const treeData: API.TreeNode<any>[] = [];
        data.data.map(n => {
          treeData.push({
            key: n.id,
            value: n.id,
            title: n.realName + ' ' + n.phone
          })
        })
        setUserList(treeData)
      }
    })
    post<API.TreeNode<any>[]>('/user/findDeptAndJobs').then((data) => {
      if (data.code === 0 && data.data) {
        setJobTreeData([{ key: -1, value: -1, title: '上一节点处理人直属上级' }, 
          { key: -2, value: -2, title: '上一节点处理人部门负责人' }, 
          ...data.data])
      }
    })
  }, [])


  const columns: ProColumns<any>[] = [
    {
      title: '流程名称',
      dataIndex: 'workflowName',
      render: (value, record) => {
        return <a href='#' onClick={() => {
          setWorkflowName(value + '')
          setWorkflow(record)
          setCurrentNodes(record.workflowNodes)
          handleModalOpen(true);
        }}>{value}</a>;
      }
    },
    {
      title: '草稿/发布',
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

  const saveSuccess = ()=>{
    message.info('保存成功！');
    actionRef.current?.reload();
    setWorkflowName('')
    handleModalOpen(false)
    setCurrentNodes(undefined)
  }
  return (
    <PageContainer>
      <Modal title={<Row><span style={{ paddingTop: '5px' }}>流程名称：</span><Input onChange={(val) => setWorkflowName(val.target.value)} style={{ width: '300px' }} value={workflowName} /></Row>} width={1000} open={modalOpen}
        onCancel={() => {
          confirm({
            title: '确定要取消吗？',
            icon: <ExclamationCircleFilled />,
            content: '您的更改尚未保存，确定要离开吗？',
            onOk() {
              handleModalOpen(false)
              setWorkflowName('')
              setCurrentNodes(undefined)
            },
            onCancel() {
            },
          });
        }}
        
        footer={<div><Button style={{ marginRight: '15px' }} onClick={() => {
          confirm({
            title: '确定要取消吗？',
            icon: <ExclamationCircleFilled />,
            content: '您的更改尚未保存，确定要离开吗？',
            onOk() {
              handleModalOpen(false)
              setWorkflowName('')
              setCurrentNodes(undefined)
            },
            onCancel() {
            },
          });
        }}>取消</Button>
          {(!workflow || workflow.workflowStatus == 0) &&
            <Button style={{ marginRight: '15px' }} onClick={() => {
              if (!workflowName || workflowName?.trim() === '') {
                message.error('流程名不能为空')
              } else {
                if (editer.current) {
                  post('/workflow/record/save', {
                    data: {
                      id:workflow?.id,
                      workflowName: workflowName,
                      workflowStatus: 0, workflowNodes: JSON.stringify(editer.current.nodes)
                    }
                  }).then(data => {
                    if (data.code === 0) {
                      saveSuccess();
                    }
                  })
                }
              }
            }}>保存草稿</Button>}
          <Button style={{ marginRight: '15px' }} type='primary' onClick={() => {
            if (!workflowName || workflowName?.trim() === '') {
              message.error('流程名不能为空')
            } else {
              if (workflow && workflow.workflowStatus == 1) {
                confirm({
                  title: '确定要修改吗？',
                  icon: <ExclamationCircleFilled />,
                  content: '修改已发布的流程可能造成正在执行的流程被废弃，您确定要修改此流程吗？',
                  onOk() {
                    if (editer.current) {
                      post('/workflow/record/save', {
                        data: {
                          id:workflow?.id,
                          workflowName: workflowName,
                          workflowStatus: 1, workflowNodes: JSON.stringify(editer.current.nodes)
                        }
                      }).then(data => {
                        if (data.code === 0) {
                          saveSuccess();
                        }
                      })
                    }
                  },
                  onCancel() {
                  },
                });
              } else {
                if (editer.current) {
                  post('/workflow/record/save', {
                    data: {
                      id:workflow?.id,
                      workflowName: workflowName,
                      workflowStatus: 1, workflowNodes: JSON.stringify(editer.current.nodes)
                    }
                  }).then(data => {
                    if (data.code === 0) {
                      saveSuccess();
                    }
                  })
                }
              }

            }
          }}>发布</Button>
        </div>}
      >
        <WorkflowEdit ref={editer} users={userList} jobs={jobTreeData} nodes={currentNodes} workfolws={workflowTreeData}/>
      </Modal>
      <ProTable
        actionRef={actionRef}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCurrentNodes('{}')
              handleModalOpen(true);
              setWorkflow(undefined)
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
            reloadWorkflowTreeData();
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
