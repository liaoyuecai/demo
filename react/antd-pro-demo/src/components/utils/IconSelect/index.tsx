import React, { useEffect, useState } from 'react';
import { Button, Card, Tabs } from 'antd';
import * as icons from "@ant-design/icons";
import { iconData } from "./iconData"
import { Tab } from "rc-tabs/lib/interface";
/**
 * copy by csdn 星辰皆燎原
 * https://blog.csdn.net/weixin_47137972/article/details/135341211
 * icon分类数据整合
 */
const IconSelect: React.FC<XH.ChildComponentProps> = (props) => {

  // @ts-ignore
  const [viewData, setViewData] = useState<Tab[]>([])
  const [cardStyle, setCardStyle] = useState<any>({ display: 'none' })
  useEffect(() => {
    // 定义风格分类数据
    const styleData: Tab[] = [];
    iconData.forEach(styleItem => {
      // 定义图标分类数据
      const typeData: Tab[] = [];
      // 遍历展示各个图标分类
      const typeIcons = styleItem.icons
      typeIcons.filter(typeItem => {
        // 将各分类下的图标遍历到页面
        const childData = typeItem.icons
        typeData.push({
          key: typeItem.key,
          label: typeItem.title,
          children: (
            <>
              {
                childData.map(item => {
                  return <Button key={item} onClick={() => change(item)} type="text"><Icon name={item} /></Button>
                })
              }
            </>
          )
        })
      })

      styleData.push({
        key: styleItem.key,
        label: styleItem.title,
        children: (
          <Tabs items={typeData} />
        )
      })
    })
    setViewData(styleData)
  }, [])

  const change = (value: string) => {
    if (props.submitView) {
      props.submitView(value)
    }
  }
  useEffect(() => {
    if (props.visible) {
      setCardStyle({})
    } else {
      setCardStyle({ display: 'none' })
    }

  }, [props.visible])


  return (
    <Card style={cardStyle}>
      <Tabs items={viewData} />
    </Card>
  )
}

export const Icon = (props: { name: string }) => {
  const { name } = props
  const antIcon: { [key: string]: any } = icons
  return React.createElement(antIcon[name])
}

export default IconSelect;

