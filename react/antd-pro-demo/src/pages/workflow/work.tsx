
import { post } from '@/services/ant-design-pro/api';
import { Button, Card, DatePicker, Form, Input, message, Modal, Radio, Row } from 'antd';
import React, { forwardRef, useEffect, useImperativeHandle, useState } from 'react';

export type WorkflowHistory = {
  activeFile?: string;
  activeInput?: string;
  createTime: string;
  createUser: string;
  id: number;
  nodeId: number;
  nodeName: string;
}

export type WorkflowInputAndData = {
  node: {
    id: number;
    nodeType: number;
    nodeName: string;
    isReturn?: number;
    workflowId: number;
  },
  inputs?: InputData[],
  history?: WorkflowHistory[];
  active?: WorkflowHistory;
}

type WorkProps = {
  workflowName: string;
  workflowId: number;
  onOK?: () => void;
  workflowInputAndData?: WorkflowInputAndData;
}

type InputData = {
  inputTitle: string,
  inputNecessary: number;
  inputType: number;
  inputValue?: string;
}
const Work = forwardRef((prop: WorkProps, ref) => {

  const [nodes, setNodes] = useState<any[]>();
  const [modalOpen, setModalOpen] = useState<boolean>(false);

  function formatISODate(isoString: string | undefined) {
    if (!isoString)
      return ''
    // 将 ISO 字符串转换为 Date 对象  
    const date = new Date(isoString);
    const formattedDate = `${date.getUTCFullYear()}-${('0' + (date.getUTCMonth() + 1)).slice(-2)}-${('0' + date.getUTCDate()).slice(-2)} ${('0' + date.getUTCHours()).slice(-2)}:${('0' + date.getUTCMinutes()).slice(-2)}:${('0' + date.getUTCSeconds()).slice(-2)}`;
    return formattedDate;
  }

  useImperativeHandle(ref, () => ({
    submit: () => {
      form.submit();
    },
  }));

  useEffect(() => {
    const nodes: any[] = [];
    if (prop.workflowInputAndData) {
      if (prop.workflowInputAndData.history) {
        prop.workflowInputAndData.history.map((item: WorkflowHistory) => {
          if (item.activeInput) {
            const historyInputs: InputData[] = JSON.parse(item.activeInput);
            const inputs: any[] = [];
            historyInputs.map(input => {
              switch (input.inputType) {
                case 1:
                  inputs.push(<Form.Item label={input.inputTitle} key={input.inputTitle} name={input.inputTitle} initialValue={formatISODate(input.inputValue)}><Input disabled /></Form.Item>)
                  break;
                case 2:
                  inputs.push(<Form.Item label={input.inputTitle} key={input.inputTitle} name={input.inputTitle} initialValue={input.inputValue}><Input disabled /></Form.Item>)
                  break;
                case 3:
                  inputs.push(<Form.Item label={input.inputTitle} key={input.inputTitle} name={input.inputTitle} initialValue={input.inputValue}><Input.TextArea disabled /></Form.Item>)
                  break;
              }

            })
            nodes.push(<Row key={item.id}><Card style={{ width: '800px' }} extra={item.createUser} title={item.nodeName}><Form  >{inputs}</Form></Card></Row>)
          }

        })
      }
      if (prop.workflowInputAndData.active) {
        const item = prop.workflowInputAndData.active
        const formItems: any[] = [];
        let historyInputs: InputData[] = []
        if (item.activeInput) {
          historyInputs = JSON.parse(item.activeInput);
          historyInputs.map(input => {
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
          if (prop.workflowInputAndData.node.nodeType === 5) {
            formItems.push(<Form.Item
              name={'activeStatus'}
              key={'activeStatus'}
              label={'审核结果'}
              rules={[{ required: true }]}
            >
              <Radio.Group >
                <Radio value={3}>通过</Radio>
                <Radio value={4}>不通过</Radio>
              </Radio.Group>
            </Form.Item>);
            formItems.push(<Form.Item
              name={'opinion'}
              label='审核意见'
              rules={[{ required: true }]}
            >
              <Input.TextArea />
            </Form.Item>);
          }
          nodes.push(<Row key={item.id}><Card style={{ width: '800px' }} title={item.nodeName} extra={<>{prop.workflowInputAndData.node
            && prop.workflowInputAndData.node.isReturn === 1 &&
            <Button onClick={() => {
              setModalOpen(true)
              returnForm.setFieldsValue({ workflowName: prop.workflowName, workflowId: prop.workflowId, nodeId: item.nodeId, inputs: [], activeStatus: 2 });
            }}>回退</Button>}</>}><Form form={form} onFinish={(val) => {
              const {  opinion, activeStatus } = val
              historyInputs.forEach(input => {
                input.inputValue = val[input.inputTitle]
              })
              post('/workflow/active/submit', { data: { workflowId:prop.workflowId, workflowName:prop.workflowName, nodeId:prop.workflowInputAndData?.node.id, inputs: historyInputs, activeStatus, opinion } }).then(res => {
                if (res.code === 0) {
                  message.success('提交成功')
                  if (prop.onOK) {
                    prop.onOK();
                  }
                }
              })
            }}>{formItems}</Form></Card></Row>)
        }
      } else {
        const node = prop.workflowInputAndData.node;
        const formItems: any[] = [];
        if (prop.workflowInputAndData.inputs && prop.workflowInputAndData.inputs.length > 0) {
          prop.workflowInputAndData.inputs.forEach(input => {
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
        if (prop.workflowInputAndData.node.nodeType === 5) {
          formItems.push(<Form.Item
            name={'activeStatus'}
            key={'activeStatus'}
            label={'审核结果'}
            rules={[{ required: true }]}
          >
            <Radio.Group >
              <Radio value={3}>通过</Radio>
              <Radio value={4}>不通过</Radio>
            </Radio.Group>
          </Form.Item>);
          formItems.push(<Form.Item
            name={'opinion'}
            label='审核意见'
            rules={[{ required: true }]}
          >
            <Input.TextArea />
          </Form.Item>);
          nodes.push(<Row key={node.id}><Card style={{ width: '800px' }} title={node.nodeName} extra={<>{prop.workflowInputAndData.node
            && prop.workflowInputAndData.node.isReturn === 1 &&
            <Button onClick={() => {
              setModalOpen(true)
              returnForm.setFieldsValue({ workflowName: prop.workflowName, workflowId: prop.workflowId, nodeId: node.id, inputs: [], activeStatus: 2 });
            }}>回退</Button>}</>}><Form form={form} onFinish={(val) => {
              const { opinion, activeStatus } = val
              const inputs: any[] = [];
              if (prop.workflowInputAndData && prop.workflowInputAndData.inputs) {
                prop.workflowInputAndData.inputs.forEach((input, index) => {
                  inputs[index] = input;
                  inputs[index].inputValue = val[input.inputTitle]
                })
              }
              post('/workflow/active/submit', { data: { workflowId:prop.workflowId, workflowName:prop.workflowName, nodeId:prop.workflowInputAndData?.node.id, inputs, activeStatus, opinion } }).then(res => {
                if (res.code === 0) {
                  message.success('提交成功')
                  if (prop.onOK) {
                    prop.onOK();
                  }
                }
              })
            }}>{formItems}</Form></Card></Row>)
        }

      }
      setNodes(nodes);
    }
  }, [prop])

  const [returnForm] = Form.useForm();

  const [form] = Form.useForm();
  return (
    <>
      <Modal title='填写回退理由' open={modalOpen} onCancel={() => { setModalOpen(false) }} onOk={() => { returnForm.submit() }}>
        <Form form={returnForm} onFinish={(val) => {
          post('/workflow/active/submit', val).then(res => {
            if (res.code === 0) {
              message.success('操作成功');
              setModalOpen(false);
            }
          })
        }}>
          <Form.Item
            name={'workflowName'}
            hidden
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'workflowId'}
            hidden
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'inputs'}
            hidden
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'activeStatus'}
            hidden
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'opinion'}
            label='回退意见'
            rules={[{ required: true }]}
          >
            <Input.TextArea />
          </Form.Item>
        </Form>
      </Modal>
      {nodes}
    </>
  );
})



export default Work;
