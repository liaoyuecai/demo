import { post } from '@/services/ant-design-pro/api';
import { ExclamationCircleFilled, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Card, Col, DatePicker, Form, Input, message, Modal, Row, Steps, theme } from 'antd';
import React, { useEffect, useRef, useState } from 'react';






const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [current, setCurrent] = useState(0);

  const [workflowSelect, setWorkflowSelect] = useState<any>();
  const [workflowContent, setWorkflowContent] = useState<any>();
  const [form] = Form.useForm();
  const [bingMenuForm] = Form.useForm();

  type WorkflowInputAndData = {
    node: {
      id: number;
      nodeName: string;
      workflowId: number;
    }
    inputs?: {
      inputTitle: string,
      inputNecessary: number;
      inputType: number;
      inputValue?: string;
    }[];
    filePath?: string;
  }

  useEffect(() => {
    post<API.TreeNode<any>[]>('/workflow/active/createList').then((res => {
      if (res.code === 0 && res.data) {
        const select: any[] = [];
        res.data.map((node: API.TreeNode<any>) => {
          if (node.children && node.children.length > 0) {
            const options: any[] = [];
            node.children.map((child: API.TreeNode<any>) => {
              options.push(<Col span={8} key={child.title}><a onClick={() => {
                post<WorkflowInputAndData>('/workflow/active/start', { data: child.value }).then((res => {
                  if (res.code === 0 && res.data) {
                    

                    const formItems: any = [];
                    formItems.push(<Form.Item
                      name={'workflowId'}
                      key={'workflowId'}
                      rules={[{ required: true }]}
                      hidden={true}
                      initialValue={child.value}
                    >
                      <Input />
                    </Form.Item>);
                    formItems.push(<Form.Item
                      name={'workflowName'}
                      key={'workflowName'}
                      label='任务名称'
                      rules={[{ required: true }]}
                    >
                      <Input />
                    </Form.Item>);
                    formItems.push(<Form.Item
                      name={'nodeId'}
                      key={'nodeId'}
                      hidden={true}
                      initialValue={res.data.node.id}
                    >
                      <Input />
                    </Form.Item>);
                    const inputsMap:any = {};
                    if (res.data.inputs) {
                      res.data.inputs.map(input => {
                        inputsMap[input.inputTitle] = input;
                        switch (input.inputType) {
                          case 1:
                            formItems.push(<Form.Item
                              name={input.inputTitle}
                              key={input.inputTitle}
                              label={input.inputTitle}
                              rules={[{ required: input.inputNecessary === 1 }]}
                            >
                              <DatePicker showTime needConfirm={false} />
                            </Form.Item>);
                            break;
                          case 2:
                            formItems.push(<Form.Item
                              name={input.inputTitle}
                              key={input.inputTitle}
                              label={input.inputTitle}
                              rules={[{ required: input.inputNecessary === 1 }]}
                            >
                              <Input />
                            </Form.Item>);
                            break;
                          case 3:
                            formItems.push(<Form.Item
                              name={input.inputTitle}
                              key={input.inputTitle}
                              label={input.inputTitle}
                              rules={[{ required: input.inputNecessary === 1 }]}
                            >
                              <Input.TextArea />
                            </Form.Item>);
                            break;
                        }

                      })
                    }
                    setWorkflowContent(<Form form={form}
                      onFinish={(val) => {
                        const { workflowId, workflowName, nodeId, ...inputs } = val
                        const inputValues:any[] = []
                        Object.keys(inputsMap).forEach(key => {
                          const value = inputsMap[key];
                          value.inputValue = inputs[key]
                          inputValues.push(value)
                        });
                        post('/workflow/active/submit', {data:{ workflowId: workflowId, workflowName: workflowName, nodeId: nodeId, inputs: inputValues, activeStatus: 1 }}).then(res => {
                          if (res.code === 0) {
                            message.success('任务创建成功')
                            handleModalOpen(false);
                            actionRef.current?.reload();
                          }
                        })
                      }}
                    >{formItems}</Form>)

                    setCurrent(1)
                  }
                }))
              }}>{child.title}</a></Col>)
            })
            select.push(<Card title={node.title} key={node.title}>
              <Row gutter={24}>{options}</Row>
            </Card>)
          }
        })
        setWorkflowSelect(select);
      }
    }))
  }, [])


  const columns: ProColumns<any>[] = [
    {
      title: '流程名称',
      dataIndex: 'workflowName',
      render: (value) => {
        return <a>{value}</a>;
      }
    },
    {
      title: '当前节点',
      search: false,
      dataIndex: 'nodeName'
    },
    {
      title: '状态',
      dataIndex: 'status',
      search: false,
      render(dom) {
        switch (dom) {
          case 1: return '流转中';
          case 2: return '结束';
        }
        return dom;
      },
    },
    {
      title: '创建时间',
      dataIndex: 'createTime',
      search: false
    },
  ];
  const { token } = theme.useToken();
  const contentStyle: React.CSSProperties = {
    lineHeight: '260px',
    textAlign: 'center',
    color: token.colorTextTertiary,
    backgroundColor: token.colorFillAlter,
    borderRadius: token.borderRadiusLG,
    border: `1px dashed ${token.colorBorder}`,
    marginTop: 16,
  };

  const { confirm } = Modal;
  return (
    <PageContainer>
      <Modal title='开启流程' open={modalOpen} onOk={() => {form.submit(); }} onCancel={() => {
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
      }}>
        <Steps current={current} items={[{ title: '选择流程' }, { title: '填写流程信息' }]} />
        <div style={contentStyle}>{current == 0 ? workflowSelect : workflowContent}</div>
      </Modal>
      <ProTable
        actionRef={actionRef}
        toolBarRender={() => [
          <Button
            type="primary"
            key="primary"
            onClick={() => {
              setCurrent(0);
              handleModalOpen(true);
            }}
          >
            <PlusOutlined />新建
          </Button>
        ]}
        columns={columns}
        rowKey={'id'}

        request={async (params) => {
          const res = await post<API.Page<any>>('/workflow/active/page', { pageSize: params.pageSize, current: params.current, data: { ...params } });
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
