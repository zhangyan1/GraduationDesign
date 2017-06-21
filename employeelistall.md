## 	获取当前部门及其子部门下人员列表


* https请求方式: GET

* 请求接口地址: https://openapi.e.uban360.com/platform/switch/employee/listAll`

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
	"status": 0,
	"data":{
		"users":[
			{
				"deptId": 78,
				"sequence": 1,
				"orgId": "AQADAAAAAAAAAA==",
				"mobile": "15868764532",
				"title": "程序员",
				"name": "王二",
				"uid": "AQADAAAAAAAAAImO7DneWwAA"
				"privilege": "mydeptonly"
			}
				]
			},
	"success": true
}
```

* data.user数据字段说明:


名称 | 类型 | 说明
------------ | ------------- | -------------
title | String |  用户职位
deptId| String |  部门id
orgId | String |  企业Id
mobile| String |  用户手机号
sequence| int |  用户排序
name| String |  用户名称
uid | String |  用户id
privilege | String | 权限，值为“mydeptonly”时表示用户权限为仅能看见本部门信息，无法查看其他部门信息，但是其他部门人员可以看见该用户




失败响应数据格式:

* content-type:application/json; charset=utf-8

```javascript
{
    "status":4800, //状态码0表示成功, 其他表示失败
    "message":"消息格式错误" //错误提示消息
	"success":false
}
```