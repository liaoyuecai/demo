import { Button, Tooltip } from "antd";
import React, { useState, useRef, useEffect, ReactNode } from "react";


export type ButtonTooltipProps = {
    title?: string;
    btnTxt?: string;
    btnStyle?: {};
    btnIcon?: ReactNode;
    content: any;
};

const ButtonTooltip: React.FC<ButtonTooltipProps> = (props) => {
    const [visible, setVisible] = useState(false)
    const tooltip: any = useRef()
    const button: any = useRef()
    useEffect(() => {
        document.addEventListener('click', outsideClick, true);
        return () => {
            document.removeEventListener('click', outsideClick, true);
        };
    }, []);
    const showDiv = () => {
        setVisible(true);
    };

    const hideDiv = () => {
        setVisible(false);
    };

    const outsideClick = (e: any) => {
        if (tooltip.current && !tooltip.current.contains(e.target)) {
            hideDiv();
        }
    };
    return (<Tooltip placement="topLeft" title={props.title}>
        <Button ref={button} style={props.btnStyle} onClick={showDiv} icon={props.btnIcon}>{props.btnTxt}
            {visible && (
                <div ref={tooltip}
                    style={{
                        position: 'absolute', backgroundColor: 'white', borderRadius: 5, paddingLeft: 10,
                        paddingRight: 10, paddingTop: 20, paddingBottom: 20,
                        border: '1px solid #ccc', zIndex: 1000, top: 32, right: 0
                    }}
                >{props.content}</div>
            )}
        </Button>
    </Tooltip>);
}

export default ButtonTooltip;