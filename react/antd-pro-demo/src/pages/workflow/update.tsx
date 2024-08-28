
import { DragOutlined, ExclamationCircleFilled, ExpandAltOutlined, } from '@ant-design/icons';
import { Button, Col, Form, Input, message, Modal, Row, Select, Switch, Tooltip, TreeSelect } from 'antd';


import React, { forwardRef, MouseEvent, useEffect, useImperativeHandle, useRef, useState } from 'react';


type Node = {
    key: number;//标识
    type: 1 | 2 | 3 | 4 | 5 | 6;//1开始，2结束，3任务节点，4子流程节点，5决策节点,6连接线
    name?: string;//名称
    x: number;//坐标x
    y: number;//坐标y
    radius?: number;//半径
    width?: number;//宽
    heigth?: number;//高
    childWorkflowId?: number;//子流程id
    userIds?: number[];//绑定用户
    jobIds?: number[];//绑定岗位
    isCheck: boolean;//是否选中
    startNode?: number;//开始节点--作连接线时使用
    endNode?: number;//结束节点--作连接线时使用
    ifReturn?: boolean;
    ifCondition?: boolean;
    //连接线线路
    ponits?: { start: { x: number, y: number }, center?: { x: number, y: number }[], end: { x: number, y: number }, arrow?: { x: number, y: number }[] };
    endPoint?: { x: number, y: number };//结束点--作连接线未完成画虚线时使用
}
type WorkflowEditProps = {
    users: API.TreeNode<any>[];
    jobs: API.TreeNode<any>[];
    nodes?: string;
    workfolws?: API.TreeNode<any>[]
}
const WorkflowEdit = forwardRef((prop: WorkflowEditProps, ref) => {
    const canvasRef = useRef<HTMLCanvasElement>(null);
    const [nodes, setNodes] = useState<Record<number, Node | undefined>>({});
    const [checkButton, setCheckButton] = useState<number>();
    const [checkNode, setCheckNode] = useState<number>(0);
    const [currentConnetLine, setCurrentConnetLine] = useState<number>(0);
    const [mouseDown, setMouseDown] = useState<boolean>(false);
    const [selectFrom] = Form.useForm();
    const [selectMadolOpen, setSelectMadolOpen] = useState<boolean>(false);

    useImperativeHandle(ref, () => ({
        nodes: nodes
    }));
    /**
     * 检查鼠标是否点击到线段
     * @param click 鼠标坐标
     * @param line 线段起始结束点
     * @param width 线段宽度
     */
    const checkClickLine = (click: { x: number, y: number }, line: { start: { x: number, y: number }, end: { x: number, y: number } }, width: number): boolean => {
        const { x: startX, y: startY } = line.start;
        const { x: endX, y: endY } = line.end;
        // 计算线段的方向向量  
        const dx = endX - startX;
        const dy = endY - startY;
        // 计算线段长度  
        const length = Math.sqrt(dx * dx + dy * dy);
        // 如果线段长度为0，则直接比较点是否相同  
        if (length === 0) {
            const distance = Math.sqrt(Math.pow(click.x - startX, 2) + Math.pow(click.y - startY, 2));
            return distance <= width / 2;
        }
        // 计算点击点到线段所在直线的垂直距离  
        const u = ((click.x - startX) * dx + (click.y - startY) * dy) / (length * length);
        // 检查点击点是否在线段上  
        if (u < 0 || u > 1) {
            return false;
        }
        // 计算最近点  
        const xClosest = startX + u * dx;
        const yClosest = startY + u * dy;
        // 计算点击点到最近点的距离  
        const distance = Math.sqrt(Math.pow(click.x - xClosest, 2) + Math.pow(click.y - yClosest, 2));
        // 检查距离是否小于线段宽度的一半  
        return distance <= width / 2;
    }
    const checkNodeSelect = (node: Node, e: MouseEvent): boolean => {
        const canvas = canvasRef.current;
        if (canvas) {
            const rect = canvas.getBoundingClientRect();
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            switch (node.type) {
                case 1:
                case 2:
                    if (node.radius) {
                        const distance = Math.sqrt(Math.pow(x - node.x, 2) + Math.pow(y - node.y, 2));
                        return distance <= node.radius
                    }
                    break;
                case 3:
                case 4:
                    if (node.heigth && node.width) {
                        const leftTop = { x: node.x - node.width / 2, y: node.y - node.heigth / 2 };
                        const rightBottom = { x: node.x + node.width / 2, y: node.y + node.heigth / 2 };
                        return x >= leftTop.x && x <= rightBottom.x && y >= leftTop.y && y <= rightBottom.y;
                    }
                    break;
                case 5:
                    if (node.radius) {
                        const halfSize = node.radius / 2 - 3;
                        const dx = Math.abs(x - node.x);
                        const dy = Math.abs(y - node.y);
                        return dx * dx + dy * dy <= halfSize * halfSize;;
                    }
                    break
                case 6:
                    if (node.ponits) {
                        let check = false;
                        const ponits = node.ponits;
                        if (ponits.center) {
                            for (let i = 0, l = ponits.center.length; i < l; i++) {
                                if (!check) {
                                    if (i == 0) {
                                        check = checkClickLine({ x, y }, { start: ponits.start, end: ponits.center[i] }, 5);
                                        if (!check) {
                                            check = checkClickLine({ x, y }, { start: ponits.center[i], end: ponits.center[i + 1] }, 5);
                                        }
                                    }
                                    else if (i == l - 1) {
                                        check = checkClickLine({ x, y }, { start: ponits.center[i], end: ponits.end }, 5);
                                    } else {
                                        check = checkClickLine({ x, y }, { start: ponits.center[i], end: ponits.center[i + 1] }, 5);
                                    }
                                }
                            }
                        } else {
                            check = checkClickLine({ x, y }, { start: ponits.start, end: ponits.end }, 5);

                        }
                        return check;
                    }
                    break;

            }
        }
        return false
    }
    const [form] = Form.useForm();
    /**
     * 获取节点的上下左右几个端点
     * @param node 
     * @param direction 上下左右|1234
     */
    const getNodePonit = (node: Node, direction: 1 | 2 | 3 | 4): { x: number, y: number } => {
        switch (node.type) {
            case 1: case 2:
                switch (direction) {
                    case 1:
                        return { x: node.x, y: node.y - (node.radius ? node.radius : 0) };
                    case 2:
                        return { x: node.x, y: node.y + (node.radius ? node.radius : 0) };
                    case 3:
                        return { x: node.x - (node.radius ? node.radius : 0), y: node.y };
                    case 4:
                        return { x: node.x + (node.radius ? node.radius : 0), y: node.y };
                }
            case 5:
                switch (direction) {
                    case 1:
                        return { x: node.x, y: node.y - (node.radius ? node.radius : 0) / 2 };
                    case 2:
                        return { x: node.x, y: node.y + (node.radius ? node.radius : 0) / 2 };
                    case 3:
                        return { x: node.x - (node.radius ? node.radius : 0) / 2, y: node.y };
                    case 4:
                        return { x: node.x + (node.radius ? node.radius : 0) / 2, y: node.y };
                }
            case 3: case 4:
                switch (direction) {
                    case 1:
                        return { x: node.x, y: node.y - (node.heigth ? node.heigth / 2 : 0) };
                    case 2:
                        return { x: node.x, y: node.y + (node.heigth ? node.heigth / 2 : 0) };
                    case 3:
                        return { x: node.x - (node.width ? node.width / 2 : 0), y: node.y };
                    case 4:
                        return { x: node.x + (node.width ? node.width / 2 : 0), y: node.y };
                }
        }
        return { x: 0, y: 0 };
    }

    /**
    * 获取箭头的三个点坐标
    * @param node 顶点
    * @param type 1,箭头向上，2箭头向下，3，箭头向左，4箭头向右
    */
    const getArrow = (node: { x: number, y: number }, type: 1 | 2 | 3 | 4): { x: number, y: number }[] => {
        const width = 8;
        const height = 14;
        switch (type) {
            case 1:
                return [{ x: node.x, y: node.y }, { x: node.x - width / 2, y: node.y + height }, { x: node.x + width / 2, y: node.y + height }]
            case 2:
                return [{ x: node.x, y: node.y }, { x: node.x - width / 2, y: node.y - height }, { x: node.x + width / 2, y: node.y - height }]
            case 3:
                return [{ x: node.x, y: node.y }, { x: node.x + height, y: node.y - width / 2 }, { x: node.x + height, y: node.y + width / 2 }]
            case 4:
                return [{ x: node.x, y: node.y }, { x: node.x - height, y: node.y - width / 2 }, { x: node.x - height, y: node.y + width / 2 }]
        }
    }


    /**
     * 生成一个新节点编号
     * @param record 
     * @returns 
     */
    function findNewKey<T>(record: Record<number, T | undefined>): number {
        let lastKey = 0;
        for (const key in record) {
            if (record.hasOwnProperty(key) && !isNaN(Number(key)) && Number(key) > lastKey) {
                lastKey = Number(key);
            }
        }
        return lastKey + 1;
    }
    /**
     * 根据连接的两点计算线路点
     * @param startNode 
     * @param endNode 
     * @returns 
     */
    const getConnetPoints = (startNode: Node, endNode: Node): { start: { x: number, y: number }, center?: { x: number, y: number }[], end: { x: number, y: number }, arrow?: { x: number, y: number }[] } => {
        if (startNode.x === endNode.x) {

            if (startNode.y > endNode.y) {
                //终点在正上方
                const end = getNodePonit(endNode, 2);
                return { start: getNodePonit(startNode, 1), end, arrow: getArrow(end, 1) }
            } else {
                const end = getNodePonit(endNode, 1);
                return { start: getNodePonit(startNode, 2), end, arrow: getArrow(end, 2) }
            }
        } else if (startNode.y === endNode.y) {
            if (startNode.x > endNode.x) {
                //终点在正左方
                const end = getNodePonit(endNode, 4);
                return { start: getNodePonit(startNode, 3), end, arrow: getArrow(end, 3) }
            } else {
                const end = getNodePonit(endNode, 3);
                return { start: getNodePonit(startNode, 4), end, arrow: getArrow(end, 4) }
            }
        } else if (startNode.x > endNode.x) {
            if (startNode.y > endNode.y) {
                if ((startNode.y - endNode.y) > (startNode.x - endNode.x)) {
                    const start = getNodePonit(startNode, 1);
                    const end = getNodePonit(endNode, 2);
                    const centerY = start.y - (start.y - end.y) / 2;
                    const center = [{ x: start.x, y: centerY }, { x: end.x, y: centerY }]
                    const arrow = getArrow(end, 1)
                    return { start, center, end, arrow }
                } else {
                    const start = getNodePonit(startNode, 3);
                    const end = getNodePonit(endNode, 4);
                    const centerX = start.x - (start.x - end.x) / 2;
                    const center = [{ x: centerX, y: start.y }, { x: centerX, y: end.y }]
                    const arrow = getArrow(end, 3)
                    return { start, center, end, arrow }
                }
            } else {
                if ((endNode.y - startNode.y) > (startNode.x - endNode.x)) {
                    const start = getNodePonit(startNode, 2);
                    const end = getNodePonit(endNode, 1);
                    const centerY = end.y - (end.y - start.y) / 2;
                    const center = [{ x: start.x, y: centerY }, { x: end.x, y: centerY }]
                    const arrow = getArrow(end, 2)
                    return { start, center, end, arrow }
                } else {
                    const start = getNodePonit(startNode, 3);
                    const end = getNodePonit(endNode, 4);
                    const centerX = start.x - (start.x - end.x) / 2;
                    const center = [{ x: centerX, y: start.y }, { x: centerX, y: end.y }]
                    const arrow = getArrow(end, 3)
                    return { start, center, end, arrow }
                }
            }
        } else {
            if (startNode.y > endNode.y) {
                if ((startNode.y - endNode.y) > (endNode.x - startNode.x)) {
                    const start = getNodePonit(startNode, 1);
                    const end = getNodePonit(endNode, 2);
                    const centerY = start.y - (start.y - end.y) / 2;
                    const center = [{ x: start.x, y: centerY }, { x: end.x, y: centerY }]
                    const arrow = getArrow(end, 1)
                    return { start, center, end, arrow }
                } else {
                    const start = getNodePonit(startNode, 4);
                    const end = getNodePonit(endNode, 3);
                    const centerX = end.x - (end.x - start.x) / 2;
                    const center = [{ x: centerX, y: start.y }, { x: centerX, y: end.y }]
                    const arrow = getArrow(end, 4)
                    return { start, center, end, arrow }
                }
            } else {
                if ((endNode.y - startNode.y) > (endNode.x - startNode.x)) {
                    const start = getNodePonit(startNode, 2);
                    const end = getNodePonit(endNode, 1);
                    const centerY = end.y - (end.y - start.y) / 2;
                    const center = [{ x: start.x, y: centerY }, { x: end.x, y: centerY }]
                    const arrow = getArrow(end, 2)
                    return { start, center, end, arrow }
                } else {
                    const start = getNodePonit(startNode, 4);
                    const end = getNodePonit(endNode, 3);
                    const centerX = end.x - (end.x - start.x) / 2;
                    const center = [{ x: centerX, y: start.y }, { x: centerX, y: end.y }]
                    const arrow = getArrow(end, 4)
                    return { start, center, end, arrow }
                }
            }
        }
    }

    useEffect(() => {
        if (canvasRef.current) {
            const ctx = canvasRef.current.getContext('2d');
            if (ctx) {
                ctx.clearRect(0, 0, canvasRef.current.width, canvasRef.current.height);
                if (nodes && Object.values(nodes).length > 0) {
                    Object.values(nodes).map(node => {
                        if (node) {
                            switch (node.type) {
                                case 1:
                                case 2:
                                    if (node.radius) {
                                        ctx.beginPath();
                                        ctx.arc(node.x, node.y, node.radius, 0, 2 * Math.PI);
                                        ctx.fillStyle = node.type === 1 ? '#90EE90' : '#FFA07A';
                                        ctx.fill();
                                        ctx.lineWidth = 0.5;
                                        ctx.strokeStyle = 'black';
                                        ctx.stroke();
                                        // 添加文字  
                                        ctx.fillStyle = '#000000'; // 文字颜色  
                                        ctx.font = '12px Arial'; // 文字样式  
                                        ctx.textAlign = 'center'; // 文字对齐方式  
                                        ctx.fillText(node.type === 1 ? '开始' : '结束', node.x, node.y + 2);
                                        ctx.stroke();
                                        if (node.isCheck) {
                                            // 设定正方形的位置和尺寸
                                            const halfSize = node.radius + 2;
                                            // 绘制正方形的虚线边框  
                                            ctx.setLineDash([5, 5]); // 设置虚线样式，5个单位实线，5个单位间隔  
                                            ctx.beginPath();
                                            ctx.rect(node.x - halfSize, node.y - halfSize, halfSize * 2, halfSize * 2);
                                            ctx.strokeStyle = 'blue'; // 边框颜色  
                                            ctx.stroke();
                                            //绘制完虚线后重置线型为实线  
                                            ctx.setLineDash([]);
                                        }
                                    }
                                    break;
                                case 3:
                                case 4:
                                    if (node.heigth && node.width) {
                                        ctx.beginPath();
                                        const radius = 20; // 圆角的大小 
                                        const x = node.x - node.width / 2;
                                        const y = node.y - node.heigth / 2;
                                        ctx.strokeStyle = node.type == 3 ? '#FF0000' : '#ADD8E6';
                                        // 设置边框宽度  
                                        ctx.lineWidth = 0.5;
                                        ctx.moveTo(x + radius, y);
                                        ctx.arcTo(x + node.width, y, x + node.width, y + node.heigth, radius);
                                        ctx.arcTo(x + node.width, y + node.heigth, x, y + node.heigth, radius);
                                        ctx.arcTo(x, y + node.heigth, x, y, radius);
                                        ctx.arcTo(x, y, x + radius, y, radius);
                                        ctx.stroke();
                                        ctx.fillStyle = 'white'; // 矩形颜色  
                                        ctx.fill();
                                        // 添加文字  
                                        ctx.fillStyle = '#000000'; // 文字颜色  
                                        ctx.font = '12px Arial'; // 文字样式  
                                        ctx.textAlign = 'left'; // 文字对齐方式  
                                        const text = node.name ? node.name : '节点' + node.key;
                                        if (text.length < 8) {
                                            ctx.fillText(text, x + (5 * (8 - text.length)), y + 27);
                                        } else if (text.length < 15) {
                                            ctx.fillText(text.slice(0, 7), x + 8, y + 20);
                                            const twoLine = text.slice(7, text.length);
                                            ctx.fillText(twoLine, x + 8 + (5.7 * (7 - twoLine.length)), y + 38);
                                        } else {
                                            ctx.fillText(text.slice(0, 7), x + 8, y + 20);
                                            const twoLine = text.slice(7, 13) + '...';
                                            ctx.fillText(twoLine, x + 8, y + 38);
                                        }
                                        ctx.stroke();
                                        if (node.isCheck) {
                                            ctx.setLineDash([5, 5]);
                                            ctx.beginPath();
                                            ctx.rect(node.x - node.width / 2 - 2, node.y - node.heigth / 2 - 2, node.width + 4, node.width / 2 + 4);
                                            ctx.strokeStyle = 'blue';
                                            ctx.stroke();
                                            ctx.setLineDash([]);
                                        }
                                    }
                                    break;
                                case 5:
                                    if (node.radius) {
                                        ctx.lineWidth = 0.5;
                                        ctx.strokeStyle = 'black';
                                        // 开始绘制菱形路径  
                                        ctx.beginPath();
                                        ctx.moveTo(node.x, node.y - node.radius / 2);
                                        ctx.lineTo(node.x + node.radius / 2, node.y);
                                        ctx.lineTo(node.x, node.y + node.radius / 2);
                                        ctx.lineTo(node.x - node.radius / 2, node.y);
                                        ctx.closePath();
                                        // 绘制边框  
                                        ctx.stroke();
                                        // 设置填充样式并填充颜色  
                                        ctx.fillStyle = 'white';
                                        ctx.fill();
                                        // 设置填充样式并填充颜色  
                                        ctx.beginPath();
                                        ctx.fillStyle = 'black';
                                        ctx.fill();
                                        ctx.font = '18px Arial';
                                        ctx.textAlign = 'center';
                                        ctx.textBaseline = 'middle';
                                        ctx.fillText('if', node.x, node.y + 2);
                                        ctx.stroke();
                                        if (node.isCheck) {
                                            const halfSize = node.radius / 2 + 2;
                                            ctx.setLineDash([5, 5]);
                                            ctx.beginPath();
                                            ctx.rect(node.x - halfSize, node.y - halfSize, halfSize * 2, halfSize * 2);
                                            ctx.strokeStyle = 'blue';
                                            ctx.stroke();
                                            ctx.setLineDash([]);
                                        }
                                    }
                                    break
                                case 6:
                                    if (node.endPoint) {
                                        // 设置虚线样式  
                                        ctx.setLineDash([5, 5]); // 第一个数字是实线部分长度，第二个数字是间隔长度  
                                        ctx.lineDashOffset = 0; // 虚线偏移量  
                                        ctx.lineWidth = 2; // 线条宽度  
                                        const x2 = node.x;
                                        const y2 = node.y;
                                        const x1 = node.endPoint.x;
                                        const y1 = node.endPoint.y;
                                        const dx = x2 - x1;
                                        const dy = y2 - y1;
                                        const distanceP1P2 = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                                        const ratio = 20 / distanceP1P2;
                                        const p3 = { x: x1 + (x2 - x1) * ratio, y: y1 + (y2 - y1) * ratio };
                                        const unitX = dx / distanceP1P2;
                                        const unitY = dy / distanceP1P2;
                                        const nx = -dy / distanceP1P2;
                                        const ny = dx / distanceP1P2;
                                        const p4 = { x: p3.x + nx * 5, y: p3.y + ny * 5 };
                                        const p5 = { x: p3.x - nx * 5, y: p3.y - ny * 5 };
                                        const p6 = { x: x1 + unitX * 5, y: y1 + unitY * 5 };

                                        // 绘制箭头主体（直线）  
                                        ctx.beginPath();
                                        ctx.moveTo(node.x, node.y);
                                        ctx.lineTo(p6.x, p6.y);
                                        ctx.stroke();
                                        // 计算顶点p1到中垂线起点p2的向量  
                                        ctx.setLineDash([]);
                                        ctx.beginPath();
                                        ctx.moveTo(node.endPoint.x, node.endPoint.y);
                                        ctx.lineTo(p4.x, p4.y);
                                        ctx.lineTo(p5.x, p5.y);
                                        ctx.closePath();
                                        ctx.fillStyle = 'black'; // 箭头颜色  
                                        ctx.fill();
                                        // 重置虚线样式，如果之后还需要绘制实线或其他虚线样式  

                                    } else {
                                        if (node.ponits && node.endNode) {
                                            const ponits = node.ponits;
                                            const endNode = nodes[node.endNode]
                                            ctx.lineWidth = 2; // 线条宽度  
                                            ctx.strokeStyle = node.isCheck ? 'blue' : (endNode?.ifCondition ? 'green' : 'black'),
                                                ctx.beginPath();
                                            ctx.moveTo(ponits.start.x, ponits.start.y);
                                            if (ponits.center) {
                                                ponits.center.map(n => {
                                                    ctx.lineTo(n.x, n.y);
                                                })
                                            }
                                            ctx.lineTo(ponits.end.x, ponits.end.y);
                                            ctx.stroke();
                                            if (ponits.arrow) {
                                                ctx.beginPath();
                                                ctx.lineWidth = 2;
                                                ctx.moveTo(ponits.arrow[0].x, ponits.arrow[0].y);
                                                ctx.lineTo(ponits.arrow[1].x, ponits.arrow[1].y);
                                                ctx.lineTo(ponits.arrow[2].x, ponits.arrow[2].y);
                                                ctx.closePath();
                                                ctx.fillStyle = node.isCheck ? 'blue' : (endNode?.ifCondition ? 'green' : 'black'), // 箭头颜色  
                                                    ctx.fill();
                                            }
                                        }
                                    }
                                    break;
                            }
                        }
                    })
                }
            }
        }

    }, [nodes])



    const onMouseDown = (e: MouseEvent) => {
        const canvas = canvasRef.current;
        if (e.button === 0) {
            setMouseDown(true)
        } else {
            setMouseDown(false)
        }
        if (canvas) {
            const rect = canvas.getBoundingClientRect();
            const key = findNewKey(nodes);
            const x = e.clientX - rect.left;
            const y = e.clientY - rect.top;
            switch (checkButton) {
                case 0:
                    if (e.button === 0) {
                        let check = false;
                        Object.values(nodes).map(node => {
                            if (node) {
                                if (!check && checkNodeSelect(node, e)) {
                                    setCheckNode(node.key);
                                    node.isCheck = true;
                                    form.resetFields();
                                    form.setFieldsValue(node);
                                    check = true;
                                } else {
                                    node.isCheck = false;
                                }
                            }
                        })
                        if (!check) {
                            setCheckNode(0)
                        }
                        setNodes({ ...nodes })

                    } else {
                        setCheckNode(0);
                    }
                    break
                case 1:
                case 2:
                    if (e.button === 0) {
                        nodes[key] = { key, name: checkButton === 1 ? '开始' : '结束', type: checkButton, x, y, radius: 20, isCheck: false };
                        setNodes({ ...nodes })
                    }
                    break
                case 3:
                case 4:
                    if (e.button === 0) {
                        nodes[key] = { key, name: checkButton === 3 ? '任务节点' : '子流程节点', type: checkButton, x, y, isCheck: false, width: 100, heigth: 50 };
                        setNodes({ ...nodes })
                    }
                    break
                case 5:
                    nodes[key] = { key, type: checkButton, name: '决策节点', x, y, isCheck: false, radius: 40 };
                    setNodes({ ...nodes })
                    break
                case 6:
                    const currentConnetLine: Node = { key, type: checkButton, name: '连接线', x, y, isCheck: false }
                    setCurrentConnetLine(key)
                    let check = false;
                    Object.values(nodes).map(node => {
                        if (node) {
                            if (!check && checkNodeSelect(node, e)) {
                                currentConnetLine.startNode = node.key
                                check = true;
                            }
                        }
                    })
                    nodes[key] = currentConnetLine;
                    setNodes({ ...nodes })
                    break;
            }
        }
    };

    const onMouseMove = (e: MouseEvent) => {
        const canvas = canvasRef.current;
        if (canvas && mouseDown) {
            const rect = canvas.getBoundingClientRect();
            switch (checkButton) {
                case 0:
                    const node = nodes[checkNode];
                    if (node) {
                        node.x = e.clientX - rect.left;
                        node.y = e.clientY - rect.top;
                        //重新计算连接线
                        Object.values(nodes).map(node => {
                            if (node?.type === 6 && node.startNode && node.endNode) {
                                const startNode = nodes[node.startNode];
                                const endNode = nodes[node.endNode];
                                if (startNode && endNode) {
                                    node.ponits = getConnetPoints(startNode, endNode)
                                }
                            }
                        });
                        setNodes({ ...nodes })
                    }
                    break;
                case 6:
                    const lineNode = nodes[currentConnetLine];
                    if (lineNode) {
                        lineNode.endPoint = { x: e.clientX - rect.left, y: e.clientY - rect.top }
                        setNodes({ ...nodes })
                    }
                    break;

            }

        }
    };

    const handleKeyDown = (event: any) => {
        if (event.key === 'Delete' && checkNode) {
            nodes[checkNode] = undefined
            Object.values(nodes).map(node => {
                if (node?.type === 6 && (node.startNode == checkNode || node.endNode == checkNode)) {
                    nodes[node.key] = undefined;
                }
            })
            setNodes({ ...nodes })
        }
    };

    useEffect(() => {

        document.addEventListener('keydown', handleKeyDown);
        return () => {
            document.removeEventListener('keydown', handleKeyDown);
        };
    }, [checkNode]);


    useEffect(() => {
        if (prop.nodes) {
            setNodes(JSON.parse(prop.nodes))
        } else {
            setNodes({})
        }
    }, [prop.nodes]);

    const onMouseUp = (e: MouseEvent) => {
        if (e.button === 0) {
            switch (checkButton) {
                case 6:
                    const canvas = canvasRef.current;
                    const connetLine = nodes[currentConnetLine];
                    if (canvas && connetLine) {
                        if (!connetLine.startNode) {
                            nodes[currentConnetLine] = undefined;
                            setCurrentConnetLine(0)
                            setNodes({ ...nodes })
                            return;
                        }
                        const rect = canvas.getBoundingClientRect();
                        const x = e.clientX - rect.left;
                        const y = e.clientY - rect.top;
                        if (x === connetLine.x && y === connetLine.y) {
                            nodes[currentConnetLine] = undefined;
                            setCurrentConnetLine(0)
                        } else {
                            let check = false;
                            Object.values(nodes).map(node => {
                                if (node) {
                                    if (!check && checkNodeSelect(node, e) && connetLine.startNode !== node.key) {
                                        connetLine.endNode = node.key;
                                        check = true;
                                    }
                                }
                            })
                            if (!check) {
                                nodes[currentConnetLine] = undefined;
                            } else {
                                connetLine.endPoint = undefined;
                                if (connetLine.endNode) {
                                    const startNode = nodes[connetLine.startNode];
                                    const endNode = nodes[connetLine.endNode];
                                    if (startNode && endNode) {
                                        connetLine.ponits = getConnetPoints(startNode, endNode)
                                        nodes[currentConnetLine] = connetLine;
                                    }
                                }
                                setCurrentConnetLine(0)
                            }
                            setNodes({ ...nodes })
                        }
                        break;
                    }
                    break;
            }
            setMouseDown(false)
        }
    };

    const changeButton = (btn: number) => {
        setCheckButton(btn);
        switch (btn) {
            case 1: case 2: case 3: case 4: case 5:
                setCheckNode(0);
                Object.values(nodes).map(node => {
                    if (node)
                        node.isCheck = false;
                })
                setNodes({ ...nodes })
                break;
        }

    }
    const { confirm } = Modal;

    const toolDivStyle = {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100%',
        cursor: 'pointer',
    }

    const toolCheckDivStyle = {
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        height: '100%',
        cursor: 'pointer',
        border: '1.5px solid #000'
    }
    return (<Row gutter={24}>
        <Col span={6}>
            <div style={{ border: '2px solid #000', borderRadius: '15px', padding: '10px', backgroundColor: '#e7e6e6', height: '400px', width: '100%' }}>
                <div style={{
                    width: '100%', height: '120px', padding: '3px', display: 'grid', gap: '1px', backgroundColor: 'white',
                    gridTemplateColumns: 'repeat(4, 1fr)', gridTemplateRows: 'repeat(3, 1fr)'
                }}>
                    <div style={{
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        height: '100%', gridColumn: 'span 4', fontWeight: 'bold'
                    }}>绘制工具</div>
                    <Tooltip title='选择'>
                        <div style={checkButton === 0 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(0) }}><DragOutlined style={{ fontSize: '28px' }} /></div>
                    </Tooltip>
                    <Tooltip title='开始节点'>
                        <div style={checkButton === 1 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(1) }}><div style={{ border: '1.5px solid #000', backgroundColor: '#90EE90', borderRadius: '15px', width: '28px', height: '28px' }}></div></div>
                    </Tooltip>
                    <Tooltip title='结束节点'>
                        <div style={checkButton === 2 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(2) }}><div style={{ border: '1.5px solid #000', backgroundColor: '#FFA07A', borderRadius: '15px', width: '28px', height: '28px' }}></div></div>
                    </Tooltip>
                    <Tooltip title='决策节点'>
                        <div style={checkButton === 5 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(5) }}><div style={{ fontWeight: 'bold' }}>if</div></div>
                    </Tooltip>
                    <Tooltip title='任务节点'>
                        <div style={checkButton === 3 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(3) }}><div style={{ border: '1.5px solid #FF0000', borderRadius: '10px', width: '30px', height: '26px' }}></div></div>
                    </Tooltip>
                    <Tooltip title='子流程节点'>
                        <div style={checkButton === 4 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(4) }}><div style={{ border: '1.5px solid #ADD8E6', borderRadius: '10px', width: '30px', height: '26px' }}></div></div>
                    </Tooltip>
                    <Tooltip title='连接线'>
                        <div style={checkButton === 6 ? toolCheckDivStyle : toolDivStyle}
                            onClick={() => { changeButton(6) }}><ExpandAltOutlined style={{ fontSize: '28px' }} /></div>
                    </Tooltip>
                </div>
                <div style={{
                    width: '100%', height: '255px', padding: '3px', display: 'flex', gap: '1px', backgroundColor: 'white',
                    flexDirection: 'column',
                    borderTop: '1.5px solid #000'
                }}>
                    <div style={{
                        display: 'flex', alignItems: 'center', justifyContent: 'center',
                        height: '35px', fontWeight: 'bold'
                    }}>节点属性</div>
                    {checkNode !== 0 && <Form form={form} size='small' onFinish={(val) => {
                        nodes[checkNode] = { ...nodes[checkNode], ...val }
                        message.info('节点属性保存成功');
                        setNodes({ ...nodes })
                    }}
                    >
                        <Form.Item
                            name={'key'}
                            label={'key'}
                            hidden
                        >
                            <Input />
                        </Form.Item>
                        <Form.Item
                            name={'name'}
                            label={'节点名称'}
                            style={{ marginBottom: '2px' }}
                            rules={[({ }) => ({
                                validator(_, value) {
                                    if (value.trim() == '') {
                                        return Promise.reject('节点名称不能为空');
                                    }
                                    return Promise.resolve();
                                }
                            })]}
                        >
                            <Input disabled={nodes[checkNode]?.type === 6} style={{ width: '120px', height: '24px' }} />
                        </Form.Item>
                        <Form.Item
                            name={'type'}
                            label={'节点类型'}
                            style={{ marginBottom: '2px' }}
                        >
                            <Select disabled style={{ width: '120px', height: '24px' }} >
                                <Select.Option value={1}>开始节点</Select.Option>
                                <Select.Option value={2}>结束节点</Select.Option>
                                <Select.Option value={3}>任务节点</Select.Option>
                                <Select.Option value={5}>决策节点</Select.Option>
                                <Select.Option value={4}>子流程节点</Select.Option>
                                <Select.Option value={6}>连接线</Select.Option>
                            </Select>
                        </Form.Item>
                        {(nodes[checkNode]?.type === 1 || nodes[checkNode]?.type === 3 ||
                            nodes[checkNode]?.type === 4 || nodes[checkNode]?.type === 5) && <div>
                                <Form.Item
                                    name={'userIds'}
                                    label={'节点用户'}
                                    style={{ marginBottom: '2px' }}

                                >
                                    <TreeSelect disabled onClick={() => {
                                        setSelectMadolOpen(true)
                                        selectFrom.setFieldsValue(form.getFieldsValue());
                                    }} treeData={prop.users} treeCheckable style={{ width: '120px', height: '24px' }} />
                                </Form.Item>
                                <Form.Item
                                    name={'jobIds'}
                                    label={'节点岗位'}
                                    style={{ marginBottom: '2px' }}
                                >
                                    <TreeSelect disabled onClick={() => {
                                        setSelectMadolOpen(true)
                                        selectFrom.setFieldsValue(form.getFieldsValue());
                                    }} treeData={prop.jobs} treeCheckable style={{ width: '120px', height: '24px' }} />
                                </Form.Item>

                                {nodes[checkNode]?.type === 4 && <Form.Item
                                    name={'childWorkflowId'}
                                    label={'子流程'}
                                    style={{ marginBottom: '2px' }}
                                >
                                    <TreeSelect disabled onClick={() => {
                                        setSelectMadolOpen(true)
                                        selectFrom.setFieldsValue(form.getFieldsValue());
                                    }} treeData={prop.workfolws} treeCheckable style={{ width: '120px', height: '24px' }} />
                                </Form.Item>}
                                {nodes[checkNode]?.type !== 1 && <Form.Item
                                    name={'ifReturn'}
                                    label={'支持回退'}
                                    style={{ marginBottom: '2px' }}
                                >
                                    <Switch />
                                </Form.Item>}

                            </div>}
                        {(nodes[checkNode]?.type === 2 || nodes[checkNode]?.type === 3 ||
                            nodes[checkNode]?.type === 4) && <Form.Item
                                name={'ifCondition'}
                                label={'条件通过'}
                                style={{ marginBottom: '2px' }}
                            >
                                <Switch />
                            </Form.Item>}
                    </Form>}
                    {(nodes[checkNode]?.type === 1 || nodes[checkNode]?.type === 2 || nodes[checkNode]?.type === 3 ||
                        nodes[checkNode]?.type === 4 || nodes[checkNode]?.type === 5) && <div style={{ display: 'flex', justifyContent: 'flex-end' }}><Button type='primary' onClick={() => {
                            form.submit();
                        }}>保存</Button></div>}
                </div>
            </div>
        </Col>
        <Col span={18}>
            <canvas onMouseDown={onMouseDown}
                onMouseMove={onMouseMove}
                onMouseUp={onMouseUp}
                ref={canvasRef}
                width='700px'
                height='400px'
                style={{ border: '2px solid #000', borderRadius: '15px' }}

            />
        </Col>
        <Modal title={'选择绑定用户与岗位'} open={selectMadolOpen} onOk={() => { selectFrom.submit() }}
            onCancel={() => {
                confirm({
                    title: '确定要取消吗？',
                    icon: <ExclamationCircleFilled />,
                    content: '您的更改尚未保存，确定要离开吗？',
                    onOk() {
                        setSelectMadolOpen(false)
                    },
                    onCancel() {
                    },
                });

            }}
        >
            <Form form={selectFrom} onFinish={(val) => {
                form.setFieldsValue({ ...form.getFieldsValue(), ...val });
                setSelectMadolOpen(false);
            }}>
                <Form.Item
                    name={'userIds'}
                    label={'节点用户'}

                >
                    <TreeSelect showSearch treeData={prop.users} treeNodeFilterProp="title" treeCheckable />
                </Form.Item>
                <Form.Item
                    name={'jobIds'}
                    label={'节点岗位'}
                >
                    <TreeSelect showSearch treeData={prop.jobs} treeNodeFilterProp="title" treeCheckable />
                </Form.Item>

                {nodes[checkNode]?.type === 4 && <Form.Item
                    name={'childWorkflowId'}
                    label={'子流程'}
                >
                    <TreeSelect showSearch treeData={prop.workfolws} treeNodeFilterProp="title"  />
                </Form.Item>}
            </Form>
        </Modal>
    </Row>
    )
})

export default WorkflowEdit;


