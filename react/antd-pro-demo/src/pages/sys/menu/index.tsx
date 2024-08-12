import { post } from '@/services/ant-design-pro/api';
import { DeleteOutlined, PlusOutlined, QuestionCircleOutlined, SearchOutlined } from '@ant-design/icons';
import type { ActionType, ProColumns } from '@ant-design/pro-components';
import {
  PageContainer,
  ProTable,
} from '@ant-design/pro-components';
import { Button, Form, Input, InputNumber, message, Popconfirm, Switch, TreeSelect } from 'antd';
import React, { useRef, useState } from 'react';
import IconSelect from '@/components/utils/IconSelect';
import FormSubmitModal from '@/components/common/FormSubmitModal';
import CreateIcon from '@/components/common/CreateIcon';
export type SysMenu = {
  id?: number;
  parentId?: number;
  status?: number;
  menuSort?: number;
  menuName?: string;
  menuPath?: string;
  menuIcon?: string;
}

type SysMenuTreeNode = {
  id: number;
  key?: number;
  value?: number;
  status?: number;
  title?: string;//title = menuName
  parentId?: number;
  menuSort?: number;
  menuName?: string;
  menuPath?: string;
  menuIcon?: string;
  children?: SysMenuTreeNode[]
}




const MenuPage: React.FC = () => {
  const [modalOpen, handleModalOpen] = useState<boolean>(false);


  const actionRef = useRef<ActionType>();

  // 表格数据
  const [dataSource, setDataSource] = useState<any>([]);


  const [form] = Form.useForm();

  // 用于展示自定义图标组件
  const [iconVisible, setIconVisible] = useState<boolean>(false)
  // 当前展示图标
  const [currentIcon, setCurrentIcon] = useState<string>('')

  // 将子组件选择的icon进行存储
  const setIcon = (icon: string) => {
    form.setFieldValue('menuIcon', icon)
    setCurrentIcon(icon)
    setIconVisible(false)
  }



  /**  
  * 将扁平的菜单列表转换为树形结构  
  * @param menus 扁平的菜单列表  
  * @returns 转换后的树形结构  
  */
  const listToTree = (menus: SysMenu[]): SysMenuTreeNode[] => {
    const map: Record<number, SysMenuTreeNode> = {}; // 用于存储已转换的树节点  
    const rootNodes: SysMenuTreeNode[] = []; // 存储根节点（没有parentId的节点）  

    // 第一步：遍历菜单列表，构建map  
    menus.forEach(menu => {
      const treeNode: SysMenuTreeNode = {
        id: menu.id ? menu.id : 0,
        key: menu.id,
        value: menu.id,
        status: menu.status,
        title: menu.menuName,
        parentId: menu.parentId,
        menuSort: menu.menuSort,
        menuName: menu.menuName,
        menuPath: menu.menuPath,
        menuIcon: menu.menuIcon,
      };
      map[menu.id ? menu.id : 0] = treeNode;

      if (!menu.parentId) {
        // 如果没有parentId，则是根节点  
        rootNodes.push(treeNode);
      }
    });

    menus.forEach(menu => {
      const currentNode = map[menu.id ? menu.id : 0];
      if (menu.parentId !== undefined) {
        // 查找父节点并添加到其子节点数组中  
        const parentNode = map[menu.parentId];
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
      title: '菜单名称',
      dataIndex: 'menuName',
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
      title: '排序',
      dataIndex: 'menuSort'
    },
    {
      title: '图标',
      dataIndex: 'menuIcon',
      render(dom) {
        if (!dom)
          return '';
        if (dom == '-' || dom == '')
          return '';
        return <CreateIcon name={dom.toString()} />;
      },
    },
    {
      title: '路由',
      dataIndex: 'menuPath'
    },
  ];

  return (
    <PageContainer>
      <FormSubmitModal title="编辑菜单" open={modalOpen} onOk={() => { form.submit(); }}
        onCancel={() => { handleModalOpen(false); }}>
        <Form form={form} onFinish={(val: any) => {
          const res = post('/menu/save', { data: val });
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
            name={'menuName'}
            label={'菜单名称'}
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
            name={'menuIcon'}
            label={'菜单图标'}
          >
            <Input onClick={() => setIconVisible(true)}
              prefix={<CreateIcon name={form.getFieldValue('menuIcon')} />}
              onKeyDown={(e) => e.preventDefault()} suffix={<SearchOutlined />} />
          </Form.Item>
          <IconSelect visible={iconVisible} parentKey={form.getFieldValue('menuIcon')} cancelView={() => setIconVisible(false)} submitView={setIcon} />
          <Form.Item
            name={'menuSort'}
            label={'菜单顺序'}
            rules={[{ required: true }]}
          >
            <InputNumber step={1} min={1} />
          </Form.Item>
          <Form.Item
            name={'menuPath'}
            label={'菜单路由'}
            rules={[{ required: true }]}
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
              const res = await post('/menu/delete', { data: [...selectedRowKeys] })
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
          const res = await post<SysMenu[]>('/menu/page', { data: {} });
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

export default MenuPage;
