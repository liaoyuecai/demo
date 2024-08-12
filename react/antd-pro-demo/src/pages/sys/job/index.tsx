import { listToTree, post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
  Search,
} from '@ant-design/pro-components';
import { Button, Card, Col, Form, Input, message, Popconfirm, Row, Switch, Tree, TreeDataNode, TreeSelect } from 'antd';
import React, { useEffect, useMemo, useRef, useState } from 'react';
import FormSubmitModal from '@/components/common/FormSubmitModal';
import { SysDept } from '../dept';
import SelectTree from '@/components/common/SelectTree';
export type SysJob = {
  id: number;
  parentId?: number;
  deptId?: number;
  status?: number;
  jobName: string;
  description?: string;
}



const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);


  const actionRef = useRef<ActionType>();
  const [searchValue, setSearchValue] = useState('');
  // 表格数据
  const [dataSource, setDataSource] = useState<any>([]);
  const [tableParams, setTableParams] = useState({});
  const [deptListData, setDeptListData] = useState<SysDept[]>([]);

  const [form] = Form.useForm();




  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);


  useEffect(() => {
    post<SysDept[]>('/dept/own', { data: {} }).then(res => {
      if (res.code === 0 && res.data) {
        setDeptListData(res.data)
      }
    })
  }, []);


  const columns: ProColumns<any>[] = [
    {
      title: '岗位名称',
      dataIndex: 'jobName',
      render: (value, record) => {
        return <a href='#' onClick={() => {
          form.setFieldsValue(record)
          handleModalOpen(true);
        }}>{value}</a>;
      }
    },
    {
      title: '状态',
      dataIndex: 'status',
      render(dom, record) {
        return <Switch checkedChildren="开启" unCheckedChildren="关闭"
          onChange={async () => {
            const res = await post('/menu/save', { data: { ...record, status: dom === 1 ? 0 : 1 } })
            if (res.code === 0) {
              actionRef.current?.reload();
            }
          }}
          checked={dom === 1} />;
      },
    },
    {
      title: '描述',
      dataIndex: 'description'
    }
  ];






  return (
    <PageContainer>
      <Row gutter={24}>
        <Col span={8} >
          <Card style={{ height: '400px' }}>
            {/* <Search style={{ marginBottom: 8 }} placeholder="Search" onChange={searchOnChange} /> */}
            <SelectTree listData={deptListData}
              onSelect={(_, e) => {
                if (e.selected) {
                  form.setFieldValue('deptId', e.node.id)
                  setTableParams({ deptId: e.node.id })
                } else {
                  form.setFieldValue('deptId', undefined)
                  setTableParams({})
                }
              }}
              setTitle={(item: SysDept) => item.departmentName} />
          </Card>
        </Col>
        <Col span={16}>
          <Card style={{ height: '400px' }}>
            <FormSubmitModal title="编辑岗位" open={modalOpen} onOk={() => { form.submit(); }}
              onCancel={() => { handleModalOpen(false); }}>
              <Form form={form} onFinish={(val: any) => {
                const res = post('/job/save', { data: val });
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
                <Form.Item name={'deptId'} label={'deptId'} hidden={true}>
                  <Input />
                </Form.Item>
                <Form.Item
                  name={'jobName'}
                  label={'岗位名称'}
                  rules={[{ required: true }]}
                >
                  <Input />
                </Form.Item>
                <Form.Item
                  name={'parentId'}
                  label={'上级岗位'}
                >
                  <TreeSelect treeData={dataSource} placeholder="请选择上级岗位" />
                </Form.Item>
                <Form.Item
                  name={'description'}
                  label={'岗位描述'}
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
                    const res = await post('/job/delete', { data: [...selectedRowKeys] })
                    if (res.code === 0) {
                      message.info('删除成功');
                      actionRef.current?.reload();
                    }
                  }}
                ><Button danger > <DeleteOutlined />删除 </Button>
                </Popconfirm>
              ]}
              columns={columns}
              search={false}
              rowSelection={{
                onChange: (selectedRowKeys) => {
                  setSelectedRowKeys(selectedRowKeys);
                },
              }}
              params={tableParams}
              request={async () => {
                const res = await post<SysJob[]>('/job/page', { data: tableParams });
                if (res && res.data) {
                  const tree = listToTree<SysJob>(res.data, (item) => item.jobName);
                  setDataSource(tree)
                  return {
                    data: tree,
                    success: true
                  };
                }
                return {};
              }}
            />
          </Card>
        </Col>
      </Row>
    </PageContainer>
  );
};

export default Page;
