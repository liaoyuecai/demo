import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, PlusOutlined, QuestionCircleOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Form, Input, message, Popconfirm, Switch, TreeSelect } from 'antd';
import React, { useRef, useState } from 'react';
import FormSubmitModal from '@/components/common/FormSubmitModal';
export type SysDept = {
  id: number;
  parentId?: number;
  status?: number;
  departmentName: string;
  description?: string;
}

type TreeNode = SysDept & {
  key?: number;
  value?: number;
  title?: string;//title = menuName
  children?: TreeNode[]
}




const Page: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);


  const actionRef = useRef<ActionType>();

  // 表格数据
  const [dataSource, setDataSource] = useState<any>([]);


  const [form] = Form.useForm();





  /**  
  * 将扁平的列表转换为树形结构  
  * @param list 扁平的列表  
  * @returns 转换后的树形结构  
  */
  const listToTree = (list: SysDept[]): TreeNode[] => {
    const map: Record<number, TreeNode> = {}; // 用于存储已转换的树节点  
    const rootNodes: TreeNode[] = []; // 存储根节点（没有parentId的节点）  

    // 第一步：遍历菜单列表，构建map  
    list.forEach(item => {
      const treeNode: TreeNode = {
        key: item.id,
        value: item.id,
        title: item.departmentName, ...item
      };
      map[item.id ? item.id : 0] = treeNode;

      if (!item.parentId) {
        // 如果没有parentId，则是根节点  
        rootNodes.push(treeNode);
      }
    });

    list.forEach(item => {
      const currentNode = map[item.id ? item.id : 0];
      if (item.parentId !== undefined) {
        // 查找父节点并添加到其子节点数组中  
        const parentNode = map[item.parentId];
        if (parentNode) {
          if (!parentNode.children)
            parentNode.children = [currentNode];
          else
            parentNode.children.push(currentNode);
        }
      }
    });

    return rootNodes;
  };

  const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);




  const columns: ProColumns<any>[] = [
    {
      title: '部门名称',
      dataIndex: 'departmentName',
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
      <FormSubmitModal title="编辑部门" open={modalOpen} onOk={() => { form.submit(); }}
        onCancel={() => { handleModalOpen(false); }}>
        <Form form={form} onFinish={(val: any) => {
          const res = post('/dept/save', { data: val });
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
            name={'departmentName'}
            label={'部门名称'}
            rules={[{ required: true }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name={'parentId'}
            label={'上级菜单'}
          >
            <TreeSelect treeData={dataSource} placeholder="请选择上级菜单" />
          </Form.Item>
          <Form.Item
            name={'description'}
            label={'部门描述'}
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
              const res = await post('/dept/delete', { data: [...selectedRowKeys] })
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
        request={async () => {
          const res = await post<SysDept[]>('/dept/page', { data: {} });
          if (res && res.data) {
            const tree = listToTree(res.data);
            setDataSource(tree)
            return {
              data: tree,
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
