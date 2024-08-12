import { ExclamationCircleFilled, QuestionCircleOutlined } from '@ant-design/icons';
import { Modal } from 'antd';
import React, { useEffect, useState } from 'react';

interface FormSubmitModalProp {
    title?: string;
    onOk?: () => void;
    onCancel?: () => void;
    open?: boolean;
    children?: React.ReactNode;
    width?:string
}

const FormSubmitModal: React.FC<FormSubmitModalProp> = (prop) => {

    const [opne, setOpen] = useState<boolean>()

    useEffect(() => {
        setOpen(prop.open ?? false)
    }, [prop])

    const onOk = () => {
        setOpen(false)
    }

    const onCancel = () => {
        setOpen(false)
    }
    const { confirm } = Modal;
    return <Modal  title={prop.title ?? 'modal'} width={prop.width} open={opne} onOk={prop.onOk ?? onOk}
        onCancel={() => {
            confirm({
                title: '确定要取消吗？',
                icon: <ExclamationCircleFilled />,
                content: '您的更改尚未保存，确定要离开吗？',
                onOk() {
                    if (prop.onCancel)
                        prop.onCancel()
                    else
                        setOpen(false)
                },
                onCancel() {
                },
            });

        }}
    >
        {prop.children}
    </Modal>
}

export default FormSubmitModal;