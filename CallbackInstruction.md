## 	回调模式说明

### 什么是回调模式？
开放平台主动推事件给ISV业务方服务

### 回调地址？
 在应用申请的时候填写,每个应用都有一个回调地址. 并且该回调地址必须满足一下要求:
* 支持`HTTPS`协议
* 支持`POST`请求
* 支持`content-typt:application/json; utf-8`格式的`body`:
```javascript
{
	"eventType":1, //事件类型 1-应用订阅事件,2-通讯录变更事件
    "encryptData":"加密后的具体业务数据",
    "signature":"数据签名",
    "nonce":"随机字符串",
    "timestamp":1345567835534,//发送时间, 精确到秒       
}
```
* response必须返`content-typt:application/json; utf-8`的数据:
```javascript
{
    "status":0, //状态码0表示成功, 其他表示失败
    "message":"success" //"success"表示成功, 错误提示消息
}
```

### 回调模式的加密方式

回调模式下发送给业务方的数据统一使用`appSecret`作为`Key`对事件内容进行AES加密,
并对加密后的数据做一次签名校验:
* `appSecret=应用密钥`;
* `appToken=应用token`;
* `encryptData=AES128Encrypt(appSecret, eventData)`;
* `signature=md5(encrypt=encryptData&nonce=nonce& timestamp=timestamp&token=appToken)`;

### 回调模式的数据解密流程
1.解析`request.body`中的`json`数据:
```javascript
{
	"eventType":1, //事件类型 1-应用订阅事件,2-通讯录变更事件
    "encryptData":"加密后的具体业务数据",
    "signature":"数据签名",
    "nonce":"随机字符串",
    "timestamp":1345567835534,//发送时间, 精确到秒       
}
```

2.数据签名校验:
用应用配置的`appToken`和`body`里的`nonce,timestamp,encryptData`根据(回调模式的加密方式)生成`signature`, 并和body里的`signature`进行比较看是否一样,如果一样表示签名校验成功,数据没有被篡改.

3.解密`encryptData`:
参照`(回调模式的加密方式)`, 解密`body`里的事件数据`encryptData`,
`eventData=AES128Decrypt(appSecret, encryptData)`;

4.把上步解密出来的`eventData`根据`eventType`转换成不同的业务事件.


### 目前支持的事件有哪些?

事件名称 | 事件类型 |  说明
------------ | ------------- | -------------
应用订阅事件 | OrgSubscribeEvent | 当应用被企业订阅后通知
通讯录变更通知 | OrgDataChangeEvent | 当订阅应用的企业数据发生变化时通知



