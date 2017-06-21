## 	获取部门列表


* https请求方式: GET

* 请求接口地址: `https://openapi.e.uban360.com/platform/switch/branch/deptList`

* header参数说明：

参数 | 是否必须 | 描述
------------ | ------------- | -------------
orgId | 是 |  企业id
accessToken | 是 | 接口访问凭据

* 请求参数说明：

参数 | 是否必须 | 描述
------------ | ------------- | -------------
deptId | 是 |  部门id(根部门传0)


* 返回说明：
正常情况下开放平台会返回下述JSON数据包给用户：

* content-type:application/json; charset=utf-8

```javascript
{
    "status": 0, //返回码，0：表示成功
    "data": {
        "departments": [
            {
                "deptId": 1,
                "parentId": 0,
                "name": "技术部",
                "order": 1
            }
        ]
    }
	"success": true
}
```

* data.user数据字段说明:


名称 | 类型 | 说明
------------ | ------------- | -------------
deptId | long |  部门id
name | String | 部门名称
parentId | long |  用户id
order | int | 部门排序


失败响应数据格式:

* content-type:application/json; charset=utf-8

```javascript
{
    "status":4800, //状态码0表示成功, 其他表示失败
    "message":"消息格式错误" //错误提示消息
	"success":false
}
```