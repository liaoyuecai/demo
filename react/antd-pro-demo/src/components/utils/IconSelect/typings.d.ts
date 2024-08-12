// 首先看此文件的全名称，是以.d.ts结尾的。ts规定在.d.ts中声明的变量或者模块，其他位置不需要使用import也能直接使用，还包含提示
// 但是需要在tsconfig.json的include中配置.d.ts文件路径（父路径即可）
// .d.ts文件中的顶级声明必须以declare或export修饰符开头
/**
 * copy by csdn 星辰皆燎原
 * https://blog.csdn.net/weixin_47137972/article/details/135341211
 * icon分类数据整合
 */
declare namespace XH {
    // 统一返回对象
    type XingHuoResult = {
      code?: number;
      msg?: string;
      data?: any | CurrentUser | NoticeIconItem[];
      // 类型，目前只表示登录类型（密码登录与手机验证码登录）
      type?: string;
      // 分页查询总条数
      total?: number;
    }
    // 子组件接收对象
    type ChildComponentProps = {
      // 是否展示
      visible: boolean
      // 回调父组件函数-关闭
      cancelView?: () => void
      // 回调父组件函数-提交
      submitView?: (data: any) => void
      // 唯一键值（一般用于详细等功能使用）
      parentKey: string | number
    }
  }
  