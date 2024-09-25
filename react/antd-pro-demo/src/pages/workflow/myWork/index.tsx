import { post } from '@/services/ant-design-pro/api';
import { ExclamationCircleFilled, PlusOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Card, Col, DatePicker, Form, Input, message, Modal, Row, Steps, theme } from 'antd';
import React, { useEffect, useRef, useState } from 'react';
import Work, { WorkflowInputAndData } from '../work';
import { set } from 'lodash';






const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);
  const actionRef = useRef<ActionType>();
  const [workflowId, setWorkflowId] = useState<number>(0);
  const [workflowName, setWorkflowName] = useState<string>('');
  const [workflowInputAndData, setWorkflowInputAndData] = useState<WorkflowInputAndData | undefined>();
  const [workflowSelect, setWorkflowSelect] = useState<any>();
  const [workflowContent, setWorkflowContent] = useState<any>();
  const [form] = Form.useForm();
  const [bingMenuForm] = Form.useForm();

  const workRef = useRef<{ submit: () => void }>();

  useEffect(() => {

  }, [])


  const columns: ProColumns<any>[] = [
    {
      title: '任务名称',
      dataIndex: 'workflowName'
    }, {
      title: '操作',
      dataIndex: 'id',
      render: (value, recode) => {
        return <Button onClick={() => {
          post<any>('/workflow/active/handle', { data: value }).then(res => {
            if (res.code === 0) {
              post<WorkflowInputAndData>('/workflow/active/workEdit', { data: { workflowId: recode.workflowId, nodeId: recode.nodeId } }).then(res => {
                if (res && res.data) {
                  handleModalOpen(true)
                  setWorkflowInputAndData(res.data)
                  setWorkflowId(recode.id)
                  setWorkflowName(recode.workflowName)
                }
              })
            }
          })
        }}>处理</Button>;
      }
    }
  ];


  const { confirm } = Modal;
  return (
    <PageContainer>
      <Modal title='开启流程' open={modalOpen} onOk={() => { workRef.current?.submit(); }} onCancel={() => {
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
        <Work ref={workRef} onOK={() => handleModalOpen(false)} workflowInputAndData={workflowInputAndData} workflowId={workflowId} workflowName={workflowName} />
      </Modal>
      <ProTable
        actionRef={actionRef}
        columns={columns}
        rowKey={'id'}
        search={false}
        request={async (params) => {
          const res = await post<API.Page<any>>('/workflow/active/workPage', { pageSize: params.pageSize, current: params.current, data: { ...params } });
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
