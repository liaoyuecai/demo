import { TreeSelect } from "antd";
import React, { useState, useEffect } from "react";

export type FromTreeItem = {
    key?: number | string;
    title?: string;
    parentKey?: number | string;
    value?: number | string;
    children?: FromTreeItem[];
};

export type FromTreeSelectProps = {
    value?: any;
    treeData?: FromTreeItem[];
    onChange?: (value: any) => void;
};

/**  
 * 解决antd TreeSelect 返回值不包含父节点问题  
 * @param props   
 * @returns   
 */
const FromTreeSelect: React.FC<FromTreeSelectProps> = (props) => {
    const [selectedKeys, setSelectedKeys] = useState<(string | number)[]>([]);
    const [treeDataMap, setTreeDataMap] = useState<Record<string | number, FromTreeItem>>({});


    const treeToMap = (tree: FromTreeItem[]): Record<string | number, FromTreeItem> => {
        const map: Record<string | number, FromTreeItem> = {};
        const traverse = (nodes: FromTreeItem[]) => {
            nodes.forEach(node => {
                node.key ? map[node.key] = node : null; // 将当前节点添加到映射中  
                if (node.children) {
                    traverse(node.children); // 递归遍历子节点  
                }
            });
        };
        traverse(tree); // 从根节点开始遍历  
        return map;
    };
    useEffect(() => {
        if (props.treeData)
            setTreeDataMap(treeToMap(props.treeData))
    }, [props.treeData]);

    useEffect(() => {
        // 初始化 selectedKeys  
        if (props.value && treeDataMap) {
            setSelectedKeys(getAllChildrenKey(props.value));
        }
    }, [treeDataMap, props.value]);

    /**  
     * 根据选中的key找到所有父节点key并一起返回  
     * @param selectKeys   
     */
    const getAllNodeKey = (selectKeys: (string | number)[]): (string | number)[] => {
        const allKeys = new Set<string | number>(selectKeys);;
        const traverse = (node: FromTreeItem) => {
            if (node.parentKey) {
                allKeys.add(node.parentKey)
                traverse(treeDataMap[node.parentKey])
            }
        };
        selectKeys.forEach(key => {
            traverse(treeDataMap[key])
        });
        return Array.from(allKeys);
    };

    /**  
     * 根据给定的值 ，找到给定节点是否选中了子节点，如选中了子节点，则删除当前节点
     * @param keys   
     * @param tree   
     */
    const getAllChildrenKey = (keys: (string | number)[]): (string | number)[] => {
        if (!keys)
            return []
        const allKeys = new Set<string | number>(keys);
        keys.forEach(key => {
            const node: FromTreeItem = treeDataMap[key];
            if (node.children) {
                node.children.forEach((child) => {
                    if (node.key && allKeys.has(node.key) && child.key && allKeys.has(child.key)) {
                        allKeys.delete(node.key)
                    }
                });
            }
        })
        return Array.from(allKeys);
    };

    const handleChange = (newKeys: (string | number)[]) => {
        setSelectedKeys(newKeys);
        if (props.onChange) {
            props.onChange(getAllNodeKey(newKeys));
        }
    };

    const { SHOW_ALL } = TreeSelect;
    return (
        <TreeSelect
            treeCheckable
            treeData={props.treeData}
            value={selectedKeys}
            onChange={handleChange}
            showCheckedStrategy={SHOW_ALL}
        />
    );
};

export default FromTreeSelect;