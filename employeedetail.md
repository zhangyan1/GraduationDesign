## 	获取当前部门下人员列表


* https请求方式: GET

* 请求接口地址: `https://openapi.e.uban360.com/platform/switch/employee/detail`

* header参数说明：

参数 | 是否必须 | 描述
------------ | ------------- | -------------
orgId | 是 |  企业id
accessToken | 是 | 接口访问凭据

* 请求参数说明：

参数 | 是否必须 | 描述
------------ | ------------- | -------------
deptId | 是 |  部门id(根部门传0)
uid    | 是 |  人员uid

* 返回说明：
正常情况下开放平台会返回下述JSON数据包给用户：

* content-type:application/json; charset=utf-8

```javascript
{
	"status": 0,
	"data":{
		"title": "程序员",
		"orgId": "AQADAAAAAAAAAA==",
		"mobile": "15868764532",
		"deptId": 78,
		"sequence": 1,
		"name": "王二",
		"uid": "AQADAAAAAAAAAImO7DneWwAA"
	},
	"success": true
}
```

* data.user数据字段说明:


名称 | 类型 | 说明
------------ | ------------- | -------------
title | String |  用户职位
orgId | String |  企业Id
mobile| String |  用户手机号
deptId| int |  部门id
sequence| int |  用户排序
name| String |  用户名称
uid | String |  用户id


失败响应数据格式:

* content-type:application/json; charset=utf-8

```javascript
{
    "status":4800, //状态码0表示成功, 其他表示失败
    "message":"消息格式错误" //错误提示消息
	"success":false
}
```