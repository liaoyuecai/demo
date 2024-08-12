import { DownOutlined, SettingOutlined } from '@ant-design/icons';
import { Button, Checkbox, CheckboxOptionType, Col, Form, Input, Row, Space, Table } from 'antd';
import React, { useEffect, useRef, useState } from 'react';


import { ColumnType } from 'antd/lib/table';
import { post } from '@/services/ant-design-pro/api';
import ButtonTooltip from './ButtonTooltip';


export type SearchColumnType = {
    label: string;
    name: string;
    vaule?: any
}

export type OrderType = {
    direction: 'asc' | 'desc';
    columnName: string;
}

export type CommonTableProps<T = any> = {
    columns: ColumnType<T>[];
    request: string;
    sort?: OrderType[];
    searchColumns?: SearchColumnType[];
    searchParams?: any;
    response?: (value: any) => { pageTotal: number, list: T[] };
    refreshIndex?: number;
};
const CommonTable: React.FC<CommonTableProps> = (prop) => {

    // 表格数据
    const [dataSource, setDataSource] = useState<any>([]);
    const [pageNo, setPageNo] = useState<number>(1);
    const [pageTotal, setPageTotal] = useState<number>(1);
    const [pageSize, setPageSize] = useState<number>(10);
    // 筛选参数
    const [searchParams, setSearchParams] = useState<any>();
    useEffect(() => {
        initDataSource();
    }, [pageSize, pageNo, searchParams]);
    const isMounted = useRef(false);
    const isRefresh = useRef(false);
    const [lastSearchParams, setLastSearchParams] = useState<any>({});

    useEffect(() => {
        // 检查父组件参数是否初次传入，初次传入不做处理， 
        if (isMounted.current) {
            if (JSON.stringify(lastSearchParams) != JSON.stringify(prop.searchParams)) {
                // 只在父组件参数变换后执行  
                setLastSearchParams(prop.searchParams);
                initDataSource()
            }
        } else {
            // 标记
            isMounted.current = true;
        }
    }, [prop.searchParams]);

    useEffect(() => {
        if (isRefresh.current) initDataSource();
        else isRefresh.current = true;
    }, [prop.refreshIndex]);

    function initDataSource() {
        const pageParams = { current: pageNo, pageSize: pageSize, orders: prop.sort };
        const res = post<API.Page<any>>(prop.request, {
            ...pageParams,
            data: { ...searchParams, ...prop.searchParams },
        });
        res.then((val) => {
            if (prop.response) {
                const data = prop.response(val)
                setDataSource(data.list);
                setPageTotal(data.pageTotal);
            } else {
                if (val && val.data) {
                    setDataSource(val.data.list);
                    setPageTotal(val.data.total);
                }
            }
        });
    }

    prop.columns.map((value, index) => {
        value.key = index.toString()
    })

    const [selectedRowKeys, setSelectedRowKeys] = useState<any>([]);
    const onSelectChange = (rowKeys: any) => {
        setSelectedRowKeys(rowKeys);
    };
    const rowSelection = {
        selectedRowKeys,
        onChange: onSelectChange,
    };


    const options = prop.columns.map(({ key, title }) => ({
        label: title,
        value: key,
    }));
    const defaultCheckedList = prop.columns.map((item) => item.key as string);
    const [checkedList, setCheckedList] = useState(defaultCheckedList);
    const newColumns = prop.columns.map((item) => ({
        ...item,
        hidden: !checkedList.includes(item.key as string),
    }));

    const [expand, setExpand] = useState(false);

    const [form] = Form.useForm();
    const getFields = () => {
        if (prop.searchColumns) {
            let max = prop.searchColumns.length % 3 === 0 ? prop.searchColumns.length + 1 : prop.searchColumns.length;
            while (max % 3 !== 0) {
                max += 1;
            }
            const count = expand ? max : 3;
            const children: any[] = [];
            for (let i = 0; i < count; i++) {
                if (i === count - 1) {
                    children.push(<Col span={8} key={i}>
                        <div style={{ textAlign: 'right' }}>
                            <Space size="small">
                                <Button type="primary" htmlType="submit">
                                    查询
                                </Button>
                                <Button
                                    onClick={() => {
                                        form.resetFields();
                                    }}
                                >
                                    重置
                                </Button>
                                <a
                                    style={{ fontSize: 14 }}
                                    onClick={() => {
                                        setExpand(!expand);
                                    }}
                                >
                                    <DownOutlined rotate={expand ? 180 : 0} /> {expand ? '收起' : '展开'}
                                </a>
                            </Space>
                        </div>
                    </Col>)
                } else {
                    if (i >= prop.searchColumns.length) {
                        children.push(<Col span={8} key={i}></Col>)
                    } else {
                        const searchColumn = prop.searchColumns[i]
                        children.push(
                            <Col span={8} key={i}>
                                <Form.Item
                                    name={searchColumn.name}
                                    label={searchColumn.label}
                                >
                                    {searchColumn.vaule ? searchColumn.vaule : <Input />}
                                </Form.Item>
                            </Col>
                        )
                    }
                }
            }
            return children;
        }
        return [];
    }
    return (
        <div>
            {prop.searchColumns && <div style={{ padding: 20, backgroundColor: 'white', borderRadius: 5, marginBottom: 20 }}>
                <Form form={form} name="advanced_search" onFinish={(values: any) => setSearchParams(values)}>
                    <Row gutter={24}>{getFields()}</Row>
                </Form>
            </div>
            }

            <Row>
                <Col span={2} offset={22}>
                    <ButtonTooltip btnIcon={<SettingOutlined />}
                        btnStyle={{ border: 'none' }}
                        title='列设置'
                        content={<div><Checkbox.Group
                            value={checkedList}
                            options={options as CheckboxOptionType[]}
                            onChange={(value) => {
                                setCheckedList(value as string[]);
                            }}
                        /></div>} />
                </Col>
            </Row>
            <Row>
                <Col span={24}>
                    <div>
                        <Table
                            rowSelection={rowSelection}
                            rowKey={'id'}
                            columns={newColumns}
                            dataSource={dataSource}
                            // bordered
                            style={{ borderCollapse: 'collapse', borderSpacing: 0 }}
                            // 减少表格字体大小
                            size="small"
                            pagination={{
                                total: pageTotal,
                                pageSize,
                                defaultPageSize: pageNo,
                                showSizeChanger: true,
                                showTotal() {
                                    return `共${pageTotal}条数据`;
                                },
                                onChange: (_page: number, size?: number) => {
                                    setPageNo(_page);
                                    if (size) {
                                        setPageSize(size);
                                    }
                                },
                            }}
                        />
                    </div>
                </Col>
            </Row>
        </div>
    );
};

export default CommonTable;
