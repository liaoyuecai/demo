# demo

## java 环境
##### jdk-22
##### spring boot 3.3.3

## 用户认证
### spring security 
为方便拆卸，单独使用直接拷贝core.authentication
<br>单独使用需要添加google guava与gson依赖
<br>主要改动在于实现json传参与返回，登录请求示例
```
request:
{
    "username":"admin","password":"123456"
}
response:
{
    "code":0,
    "data":{
        "token":"akjwuyeqwi29123g1ihas87&^*&"
    }
}
```
