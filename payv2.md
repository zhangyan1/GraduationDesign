## 1. 风控接口(v2)
* __接口名称 :__ 风控接口
* __接口地址 :__ `https://openapi.e.uban360.com/platform/pay/prePayCheck`
* __请求方式 :__ `POST`


### Request请求说明：

#### Header参数说明：

| 名称          | 类型     | 必须    | 默认值   | 说明       |
| ----------- | ------ | ------ | ------ | --------- |
| accessToken  | string  | 是      |         | 接口访问凭据 |
| uid          | string  | 是      |         | 用户id       |
| orgSecret    | String  | 是      |         | 企业认证凭证 |

#### 二进制形式：

* __Content-Type :__ `application/json; charset=utf-8`

* `body`格式说明：

```json
{
    "orderId" : "1111111111111111111111",
    "price"   : 100,
    "extend"  : {
        // 建议接入方将订单详情放入extend，此处只是举个例子，并非强制接入方订单详情必须带有该参数或只带该参数
        "orderCreateTime" : "2017-05-09 17:55:55",
        "orderStatus"     : "PAY_ING"
    }
}
```

* `body`属性说明：

| 名称    | 类型     | 必须    | 默认值   | 说明                          |
| ------ | ------ | ------ | ------ | ------------------------- |
| orderId | string  | 是      |         | 订单id                        |
| price   | number  | 是      |         | 当前订单金额，单位分            |
| extend  | json    | 否      |         | 接入方订单详情                 |

### Response响应说明：

#### 成功响应数据格式:

* __Content-Type :__ `application/json; charset=utf-8`

```json
{
    "status"  : 0,    // 状态码是0表示成功
    "success" : true // 是否成功
}
```

#### 失败响应数据格式:

* __Content-Type :__ `application/json; charset=utf-8`

```json
{
    "status"  : 10000,     // 状态码
    "success" : false,    // 是否成功
    "message" : "错误信息" // 错误信息
}
```

## 2. 订单回调(v2)
* __接口名称 :__ 订单回调
* __接口地址 :__ `https://openapi.e.uban360.com/platform/pay/orderCallBack`
* __请求方式 :__ `POST`

### Request请求说明:

#### Header参数说明

| 名称          | 类型     | 必须    | 默认值   | 说明       |
| ----------- | ------ | ------ | ------ | --------- |
| accessToken  | string  | 是      |         | 接口访问凭据 |
| uid          | string  | 是      |         | 用户id       |
| orgSecret    | String  | 是      |         | 企业认证凭证 |

#### 二进制形式：

* __Content-Type :__ `application/json; charset=utf-8`

* `body`格式说明：

```json
{
    "orderId" : "1111111111111111111111",
    "orderStatus" : "PAY_SUCCESS",
    "price"   : 100,
    "extend"  : {
        // 建议接入方将订单详情放入extend，此处只是举个例子，并非强制接入方订单详情必须带有该参数或只带该参数
        "orderCreateTime" : "2017-05-09 17:55:55",
        "orderStatus"     : "PAY_ING"
    }
}
```

* `body`属性说明：

| 名称            | 类型     | 必须    | 默认值   | 说明                |
| ------------- | ------ | ------ | ------ | ----------------- |
| orderId         | string  | 是      |         | 订单id              |
| orderStatus     | number  | 是      |         | 订单状态            |
| price           | number  | 是      |         | 当前订单最终支付金额  |
| extend          | json    | 否      |         | 接入方订单详情       |

* `body`.`orderStatus`参数说明:

| 名称           | 说明       |
| ------------ | -------- |
| PAY_ING        | 订单支付中 |
| PAY_SUCCESS    | 支付成功   |
| REFUND_SUCCESS | 退款成功   |
| TRADE_CLOSE    | 交易关闭   |

### Response响应说明：

#### 成功响应数据格式:

* __Content-Type :__ `application/json; charset=utf-8`

```json
{
    "status"  : 0,    // 状态码是0表示成功
    "success" : true // 是否成功
}
```

#### 失败响应数据格式:

* __Content-Type :__ `application/json; charset=utf-8`

```json
{
    "status"  : 10000,     // 状态码
    "success" : false,    // 是否成功
    "message" : "错误信息" // 错误信息
}
```

## 3. 补全信息(v2)
* __接口名称 :__ 补全信息
* __接口地址 :__ `https://openapi.e.uban360.com/platform/pay/updateInfo`
* __请求方式 :__ `POST`

### Request请求说明:

#### Header参数说明

| 名称          | 类型     | 必须    | 默认值   | 说明       |
| ----------- | ------ | ------ | ------ | --------- |
| accessToken  | string  | 是      |         | 接口访问凭据 |
| uid          | string  | 是      |         | 用户id       |
| orgSecret    | String  | 是      |         | 企业认证凭证 |

#### 二进制形式：

* __Content-Type :__ `application/json; charset=utf-8`

* `body`格式说明：

```json
{
    "extend"      : {
        // 建议接入方将用户信息放入extend，此处只是举个例子，并非强制接入方用户信息必须带有该参数或只带该参数
        "idCard"  : "425424196710155420",
        "sex"     : "男"
    }
}
```

* `body`属性说明：

| 名称    | 类型     | 必须    | 默认值   | 说明                          |
| ------ | ------ | ------ | ------ | ------------------------- |
| extend  | string  | 否      |         | 接入方用户信息                 |

### Response响应说明：

#### 成功响应数据格式:

* __Content-Type :__ `application/json; charset=utf-8`

```json
{
    "status"  : 0,    // 状态码是0表示成功
    "success" : true // 是否成功
}
```

#### 失败响应数据格式:

* __Content-Type :__ `application/json; charset=utf-8`

```json
{
    "status"  : 10000,     // 状态码
    "success" : false,    // 是否成功
    "message" : "错误信息" // 错误信息
}
```
