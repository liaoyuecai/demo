import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, EditOutlined, ExclamationCircleFilled, PlusOutlined, QuestionCircleOutlined, SettingOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Card, Col, Form, Input, message, Modal, Popconfirm, Row, Switch, Tree, TreeSelect } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import WorkflowEdit from './update'
import { set } from 'lodash';
import FormSubmitModal from '@/components/common/FormSubmitModal';


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
  const [workflowType, setWorkflowType] = useState<number>();
  const [typeList, setTypeList] = useState<any[]>();
  const [workflow, setWorkflow] = useState<any>();
  const actionRef = useRef<ActionType>();
  const [typeForm] = Form.useForm();
  const [typeFormOpen, setTypeFormOpen] = useState<boolean>(false);
  const editer = useRef<{ nodes: any[], reset: () => void }>();

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);
  const [currentNodes, setCurrentNodes] = useState<string>();

  const [workflowTreeData, setWorkflowTreeData] = useState<API.TreeNode<any>[]>([]);

  const reloadWorkflowTreeData = () => {
    post<{ id: number, workflowName: string }[]>('/workflow/record/findRecords').then(data => {
      if (data.code === 0 && data.data) {
        const nodes: API.TreeNode<any>[] = [];
        data.data.map(d => {
          nodes.push({ key: d.id, title: d.workflowName, value: d.id})
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
    reloadTypeList();
  }, [])


  const reloadTypeList = () => {
    post<API.TreeNode<any>[]>('/workflow/type/list').then((data) => {
      if (data.code === 0 && data.data) {
        setTypeList(data.data.map(n => {
          return { title: n.typeName, key: n.id,value: n.id }
        }))
      }
    })
  }

  const columns: ProColumns<any>[] = [
    {
      title: '流程名称',
      dataIndex: 'workflowName',
      render: (value, record) => {
        return <a href='#' onClick={() => {
          setWorkflowName(value + '');
          setWorkflowType(record.id);
          setWorkflow(record)
          setCurrentNodes(record.workflowNodes)
          handleModalOpen(true);
        }}>{value}</a>;
      }
    },
    {
      title: '流程类型',
      dataIndex: 'typeId',
      render: (value) => {
        let type = ''
        typeList?.map(n => {
          if (n.key == value)
            type = n.title
        })
        return type;
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

  const saveSuccess = () => {
    message.info('保存成功！');
    actionRef.current?.reload();
    setWorkflowName('')
    setWorkflowType(undefined)
    handleModalOpen(false)
    setCurrentNodes(undefined)
    editer.current?.reset();
  }
  return (
    <PageContainer>
      <Row gutter={24}>
        <Col span={6}>
          <Card title={'流程类型'} extra={[<Button type='primary' size='small' onClick={() => {
            setTypeFormOpen(true);
            typeForm.resetFields();
          }}>+</Button>]}>
            <Tree treeData={typeList}
              titleRender={node => {
                return <>{node.title}<Button size='small' icon={<EditOutlined />} style={{ marginLeft: '3px' }} onClick={() => {
                  setTypeFormOpen(true);
                  typeForm.setFieldsValue({ id: node.key, typeName: node.title });
                }} />
                  <Button size='small' style={{ marginLeft: '3px' }} danger icon={<DeleteOutlined />} onClick={() => {
                    confirm({
                      title: '确定要删除该条数据吗？',
                      onOk() {
                        post('/workflow/type/delete', { data: [node.key] }).then(res => {
                          if (res.code === 0) {
                            reloadTypeList();
                          }
                        })
                      },
                      onCancel() {
                      },
                    });

                  }}
                  /></>
              }}
            />
          </Card>
          <FormSubmitModal title='编辑流程类型' open={typeFormOpen} onCancel={() => setTypeFormOpen(false)} onOk={() => typeForm.submit()}>
            <Form form={typeForm} onFinish={(val) => {
              post('/workflow/type/save', { data: val }).then(res => {
                if (res.code === 0) {
                  reloadTypeList();
                  setTypeFormOpen(false);
                }
              })
            }}>
              <Form.Item name={'id'} hidden >
                <Input disabled />
              </Form.Item>
              <Form.Item name={'typeName'} label='类型名称' >
                <Input />
              </Form.Item>
            </Form>
          </FormSubmitModal>
        </Col>
        <Col span={18}>
          <Modal title={<Row><span style={{ paddingTop: '5px' }}>流程名称：</span>
            <Input onChange={(val) => setWorkflowName(val.target.value)} style={{ width: '300px' }} value={workflowName} />
            <span style={{ paddingTop: '5px',marginLeft:'5px' }}>流程类型：</span><TreeSelect
            style={{width:'200px'}} 
            value={workflowType} onChange={val=>{
              setWorkflowType(val)
              }} treeData={typeList}/>
          </Row>} width={1000} open={modalOpen}
            onCancel={() => {
              confirm({
                title: '确定要取消吗？',
                icon: <ExclamationCircleFilled />,
                content: '您的更改尚未保存，确定要离开吗？',
                onOk() {
                  handleModalOpen(false)
                  setWorkflowName('')
                  setWorkflowType(undefined)
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
                  setWorkflowType(undefined)
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
                          id: workflow?.id,
                          workflowName: workflowName,
                          typeId: workflowType,
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
                              id: workflow?.id,
                              workflowName: workflowName,
                              typeId: workflowType,
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
                          id: workflow?.id,
                          workflowName: workflowName,
                          typeId: workflowType,
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
            <WorkflowEdit ref={editer} users={userList} jobs={jobTreeData} nodes={currentNodes} workfolws={workflowTreeData} />
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
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Page;
