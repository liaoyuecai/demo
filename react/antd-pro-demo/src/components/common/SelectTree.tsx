import { deepClone } from "@/services/ant-design-pro/api";
import { Input, Tree } from "antd";
import React, { useState, useEffect } from "react";


export type SelectTreeProps = {
    listData?: { id: number, parentId?: number }[];
    setTitle?: (item: any) => string;
    onSelect?: (selectedKeys: any[], info: {
        event: 'select';
        selected: boolean;
        node: any;
        selectedNodes: any;
        nativeEvent: MouseEvent;
    }) => void;
};


const SelectTree: React.FC<SelectTreeProps> = (props) => {
    const [dataList, setDataList] = useState<{ id: number, parentId?: number }[]>([]);
    const [treeNodeMap, setTreeNodeMap] = useState<Record<number, { id: number, parentId?: number, children?: any[] }>>({});
    const [treeData, setTreeData] = useState<API.TreeNode<any>[]>([]);
    const [searchValue, setSearchValue] = useState<string>();
    useEffect(() => {
        if (props.listData && props.listData.length > 0) {
            setDataList(props.listData)
            const map: Record<number, { id: number, key: number, parentId?: number, title?: string, value?: number, children?: any[] }> = {};
            props.listData.forEach(item => {
                if (props.setTitle)
                    map[item.id] = { ...item, title: props.setTitle(item), key: item.id, value: item.id };
            });
            props.listData.forEach(item => {
                const currentNode = map[item.id];
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
            setTreeNodeMap(map);
        }
    }, [props.listData]);

    const findNode = (searchValue: string, node: API.TreeNode<any> | null): API.TreeNode<any> | null => {
        if (!node) return null;

        let children: API.TreeNode<any>[] = [];
        if (node.children) {
            children = node.children.filter((item: API.TreeNode<any>) => {
                const found = findNode(searchValue, item);
                return found !== null;
            });
        }

        if (children.length > 0) {
            node.children = children;
            return node;
        }

        if (node.title.toLowerCase().includes(searchValue.toLowerCase())) {
            // 如果没有子节点匹配，但当前节点匹配，也返回该节点  
            node.children = []; // 清空子节点为安全起见  
            return node;
        }

        return null;
    };

    useEffect(() => {
        if (dataList && dataList.length > 0 && props.setTitle) {
            const rootNode: API.TreeNode<any>[] = [];
            if (searchValue) {
                const map = deepClone(treeNodeMap);
                dataList.forEach(item => {
                    if (!item.parentId)
                        rootNode.push(map[item.id])
                })

                const treeData: API.TreeNode<any>[] = [];
                rootNode.map(item => {
                    const n = findNode(searchValue, item)
                    if (n) treeData.push(n)
                })
                setTreeData(treeData)
            } else {
                dataList.forEach(item => {
                    if (!item.parentId)
                        rootNode.push(treeNodeMap[item.id])
                })
                setTreeData(rootNode)
            }
        }
    }, [dataList, searchValue]);



    return (<div>
        <Input onChange={(e) => { setSearchValue(e.target.value) }} style={{ marginBottom: 8 }} />
        {treeData.length > 0 && <Tree treeData={treeData}
        onSelect={props.onSelect} defaultExpandAll />}
    </div>);
}

export default SelectTree;