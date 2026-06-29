# 小智物联网平台 API


**简介**:小智物联网平台 API


**HOST**:http://localhost:8091


**联系人**:Joey


**Version**:5.0.0


**接口路径**:/v3/api-docs


[TOC]






# 音乐控制器


## uploadMusic


**接口地址**:`/api/file/music`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|file|上传的音乐文件|query|true|file||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 设备管理


## 处理OTA请求


**接口地址**:`/api/device/ota`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>返回OTA结果</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Device-Id|设备ID|header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 处理OTA请求


**接口地址**:`/api/device/ota`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>返回OTA结果</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Device-Id|设备ID|header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 更新设备信息


**接口地址**:`/api/device/{deviceId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新设备名称、角色、功能列表等信息</p>



**请求示例**:


```javascript
{
  "deviceName": "",
  "roleId": 0,
  "location": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|deviceId||path|true|string||
|deviceUpdateReq|更新设备请求|body|true|DeviceUpdateReq|DeviceUpdateReq|
|&emsp;&emsp;deviceName|设备名称||false|string||
|&emsp;&emsp;roleId|角色ID||false|integer(int32)||
|&emsp;&emsp;location|地理位置||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 删除设备


**接口地址**:`/api/device/{deviceId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>从当前用户账户中删除指定设备</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|deviceId||path|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 根据条件查询设备


**接口地址**:`/api/device`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回设备信息列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|设备分页查询|query|true|DevicePageReq|DevicePageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;deviceId|设备ID||false|string||
|&emsp;&emsp;deviceName|设备名称||false|string||
|&emsp;&emsp;roleName|角色名称||false|string||
|&emsp;&emsp;state|设备状态||false|string||
|&emsp;&emsp;roleId|角色ID||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 添加设备


**接口地址**:`/api/device`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>使用设备验证码添加设备到当前用户账户</p>



**请求示例**:


```javascript
{
  "code": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|deviceCreateReq|添加设备请求|body|true|DeviceCreateReq|DeviceCreateReq|
|&emsp;&emsp;code|设备验证码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 查询OTA激活状态


**接口地址**:`/api/device/ota/activate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回OTA激活状态</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|Device-Id|设备唯一标识|header|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 批量更新设备


**接口地址**:`/api/device/batchUpdate`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>批量更新多个设备的角色</p>



**请求示例**:


```javascript
{
  "deviceIds": "",
  "roleId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|deviceBatchUpdateReq|批量更新设备请求|body|true|DeviceBatchUpdateReq|DeviceBatchUpdateReq|
|&emsp;&emsp;deviceIds|设备ID列表，以逗号分隔||true|string||
|&emsp;&emsp;roleId|角色ID||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# MCP工具管理


## 批量设置角色排除工具


**接口地址**:`/api/mcpTool/role/{roleId}/exclude-tools`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>批量设置指定角色需要排除的工具列表</p>



**请求示例**:


```javascript
{
  "excludeTools": [],
  "serverName": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||
|mcpRoleExcludeToolsReq|批量设置角色排除工具请求|body|true|McpRoleExcludeToolsReq|McpRoleExcludeToolsReq|
|&emsp;&emsp;excludeTools|排除的工具列表||true|array|string|
|&emsp;&emsp;serverName|服务器名称||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 切换角色工具状态


**接口地址**:`/api/mcpTool/role/{roleId}/tools`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>启用或禁用指定角色的某个工具</p>



**请求示例**:


```javascript
{
  "toolName": "",
  "serverName": "",
  "enabled": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||
|mcpRoleToolStatusReq|切换角色 MCP 工具状态请求|body|true|McpRoleToolStatusReq|McpRoleToolStatusReq|
|&emsp;&emsp;toolName|工具名称||true|string||
|&emsp;&emsp;serverName|服务器名称||true|string||
|&emsp;&emsp;enabled|是否启用||true|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 切换全局工具状态


**接口地址**:`/api/mcpTool/global/tools`


**请求方式**:`PATCH`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>启用或禁用全局工具</p>



**请求示例**:


```javascript
{
  "toolName": "",
  "serverName": "",
  "enabled": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|mcpGlobalToolStatusReq|切换全局 MCP 工具状态请求|body|true|McpGlobalToolStatusReq|McpGlobalToolStatusReq|
|&emsp;&emsp;toolName|工具名称||true|string||
|&emsp;&emsp;serverName|服务器名称||true|string||
|&emsp;&emsp;enabled|是否启用||true|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 获取系统全局工具列表


**接口地址**:`/api/mcpTool/system-global`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取系统中所有可用的全局工具列表</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 获取禁用的工具列表


**接口地址**:`/api/mcpTool/role/{roleId}/disabled-tools`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>获取指定角色和全局禁用的工具列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 智能体管理


## 根据条件查询智能体


**接口地址**:`/api/agent`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回智能体列表信息，会自动查询平台当前存在的智能体并同步本地配置</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|智能体分页查询|query|true|AgentPageReq|AgentPageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;provider|服务提供商||false|string||
|&emsp;&emsp;agentName|智能体名称||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 用户管理


## 修改用户信息


**接口地址**:`/api/user/{userId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新用户个人信息</p>



**请求示例**:


```javascript
{
  "email": "",
  "tel": "",
  "password": "",
  "name": "",
  "avatar": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userId||path|true|integer(int32)||
|userUpdateReq|用户更新请求|body|true|UserUpdateReq|UserUpdateReq|
|&emsp;&emsp;email|新邮箱||false|string||
|&emsp;&emsp;tel|新手机号||false|string||
|&emsp;&emsp;password|新密码||false|string||
|&emsp;&emsp;name|新姓名/昵称||false|string||
|&emsp;&emsp;avatar|新头像||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 根据条件查询用户信息列表


**接口地址**:`/api/user`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回用户信息列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|用户分页查询|query|true|UserPageReq|UserPageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;name|姓名/昵称||false|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;tel|手机号||false|string||
|&emsp;&emsp;isAdmin|是否管理员||false|string||
|&emsp;&emsp;authRoleId|后台权限角色ID||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 用户注册


**接口地址**:`/api/user`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>新用户注册</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": "",
  "name": "",
  "email": "",
  "tel": "",
  "code": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userRegisterReq|用户注册请求|body|true|UserRegisterReq|UserRegisterReq|
|&emsp;&emsp;username|用户名||true|string||
|&emsp;&emsp;password|密码||true|string||
|&emsp;&emsp;name|姓名/昵称||false|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;tel|手机号||false|string||
|&emsp;&emsp;code|验证码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 微信登录


**接口地址**:`/api/user/wx-login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>使用微信 code 登录，未注册自动注册</p>



**请求示例**:


```javascript
{
  "code": "",
  "inviterId": 0
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userWechatLoginReq|微信登录请求|body|true|UserWechatLoginReq|UserWechatLoginReq|
|&emsp;&emsp;code|微信登录 code||true|string||
|&emsp;&emsp;inviterId|邀请人ID||false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 手机号验证码登录


**接口地址**:`/api/user/tel-login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>使用手机号和验证码登录，未注册自动注册</p>



**请求示例**:


```javascript
{
  "tel": "",
  "code": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userTelLoginReq|手机号验证码登录请求|body|true|UserTelLoginReq|UserTelLoginReq|
|&emsp;&emsp;tel|手机号||true|string||
|&emsp;&emsp;code|验证码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 发送短信验证码


**接口地址**:`/api/user/sendSmsCaptcha`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>向指定手机号发送验证码</p>



**请求示例**:


```javascript
{
  "email": "",
  "tel": "",
  "type": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userSendCaptchaReq|发送验证码请求|body|true|UserSendCaptchaReq|UserSendCaptchaReq|
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;tel|手机号||false|string||
|&emsp;&emsp;type|用途类型,可用值:register,forget||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 发送邮箱验证码


**接口地址**:`/api/user/sendEmailCaptcha`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>向指定邮箱发送验证码</p>



**请求示例**:


```javascript
{
  "email": "",
  "tel": "",
  "type": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userSendCaptchaReq|发送验证码请求|body|true|UserSendCaptchaReq|UserSendCaptchaReq|
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;tel|手机号||false|string||
|&emsp;&emsp;type|用途类型,可用值:register,forget||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 重置密码


**接口地址**:`/api/user/resetPassword`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>通过邮箱验证码重置密码</p>



**请求示例**:


```javascript
{
  "email": "",
  "code": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userResetPasswordReq|重置密码请求|body|true|UserResetPasswordReq|UserResetPasswordReq|
|&emsp;&emsp;email|邮箱||true|string||
|&emsp;&emsp;code|验证码||true|string||
|&emsp;&emsp;password|新密码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 刷新Token


**接口地址**:`/api/user/refresh-token`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>刷新Token有效期，返回新的Token</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 用户名密码登录


**接口地址**:`/api/user/login`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>使用用户名/邮箱/手机号和密码进行登录</p>



**请求示例**:


```javascript
{
  "username": "",
  "password": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|userLoginReq|用户名密码登录请求|body|true|UserLoginReq|UserLoginReq|
|&emsp;&emsp;username|用户名/邮箱/手机号||true|string||
|&emsp;&emsp;password|密码||true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 检查用户名和手机号是否已存在


**接口地址**:`/api/user/checkUser`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回检查结果</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|用户占用检查请求|query|true|UserCheckReq|UserCheckReq|
|&emsp;&emsp;username|用户名||false|string||
|&emsp;&emsp;email|邮箱||false|string||
|&emsp;&emsp;tel|手机号||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 检查Token有效性


**接口地址**:`/api/user/check-token`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>验证当前Token是否有效，有效则返回用户信息</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 文件上传控制器


## 文件上传


**接口地址**:`/api/file/upload`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>如果有配置腾讯云对象存储的话默认会存储到对象存储中</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|file|上传的文件|query|true|file||
|type|文件类型|query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 后台权限角色


## 获取后台权限角色授权配置


**接口地址**:`/api/auth-role/{authRoleId}/permissions`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回角色权限树和已选权限</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|authRoleId||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 更新后台权限角色授权配置


**接口地址**:`/api/auth-role/{authRoleId}/permissions`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>保存角色已选权限</p>



**请求示例**:


```javascript
[]
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|authRoleId||path|true|integer(int32)||
|integers|integer|body|true|array||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 根据条件查询后台权限角色


**接口地址**:`/api/auth-role`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回后台权限角色列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|后台权限角色分页查询|query|true|AuthRolePageReq|AuthRolePageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;authRoleName|角色名称||false|string||
|&emsp;&emsp;roleKey|角色标识||false|string||
|&emsp;&emsp;status|状态||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 消息管理


## 根据条件查询对话消息


**接口地址**:`/api/message`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回对话消息列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|消息分页查询|query|true|MessagePageReq|MessagePageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;deviceId|设备ID||false|string||
|&emsp;&emsp;deviceName|设备名称||false|string||
|&emsp;&emsp;sender|发送方||false|string||
|&emsp;&emsp;messageType|消息类型||false|string||
|&emsp;&emsp;roleId|角色ID||false|integer(int32)||
|&emsp;&emsp;startTime|开始时间||false|string(date-time)||
|&emsp;&emsp;endTime|结束时间||false|string(date-time)||
|&emsp;&emsp;sessionId|会话ID||false|string||
|&emsp;&emsp;source|消息来源: web|device||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 批量删除设备消息


**接口地址**:`/api/message`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>清除指定设备的所有聊天记录</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|deviceId||query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 查询用户的会话列表


**接口地址**:`/api/message/conversations`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回当前用户的历史会话列表，基于sessionId聚合</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|会话分页查询|query|true|ConversationPageReq|ConversationPageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;roleId|角色ID||false|integer(int32)||
|&emsp;&emsp;source|消息来源: web|device||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 删除对话消息


**接口地址**:`/api/message/{messageId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>删除指定的对话消息，逻辑删除</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|messageId||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# Web 聊天


## 开启聊天会话


**接口地址**:`/api/chat/open`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>创建或续接 Web 聊天会话并返回 sessionId</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||query|true|integer(int32)||
|sessionId||query|false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseMapStringString|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|object||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 关闭聊天会话


**接口地址**:`/api/chat/close`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>关闭 Web 聊天会话并释放资源</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sessionId||query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK||
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 流式聊天


**接口地址**:`/api/chat/stream`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`text/event-stream,*/*`


**接口描述**:<p>通过 SSE 返回 AI 回复 Token 流，包含 thinking 和 content 两种类型</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|sessionId||query|true|string||
|text||query|true|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ChatToken|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|type||string||
|text||string||
|thinking||boolean||
|content||boolean||


**响应示例**:
```javascript
[
	{
		"type": "",
		"text": "",
		"thinking": true,
		"content": true
	}
]
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
[
	{
		"code": 200,
		"message": "操作成功",
		"data": {}
	}
]
```


# 配置管理


## 更新配置信息


**接口地址**:`/api/config/{configId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新LLM/STT/TTS配置</p>



**请求示例**:


```javascript
{
  "configName": "",
  "configDesc": "",
  "configType": "",
  "modelType": "",
  "provider": "",
  "appId": "",
  "apiKey": "",
  "apiSecret": "",
  "ak": "",
  "sk": "",
  "apiUrl": "",
  "state": "",
  "isDefault": "",
  "enableThinking": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|configId||path|true|integer(int32)||
|configUpdateReq|配置更新请求|body|true|ConfigUpdateReq|ConfigUpdateReq|
|&emsp;&emsp;configName|配置名称||false|string||
|&emsp;&emsp;configDesc|配置描述||false|string||
|&emsp;&emsp;configType|配置类型||false|string||
|&emsp;&emsp;modelType|模型类型||false|string||
|&emsp;&emsp;provider|服务提供商||false|string||
|&emsp;&emsp;appId|服务提供商分配的AppId||false|string||
|&emsp;&emsp;apiKey|服务提供商分配的ApiKey||false|string||
|&emsp;&emsp;apiSecret|服务提供商分配的ApiSecret||false|string||
|&emsp;&emsp;ak|服务提供商分配的Access Key||false|string||
|&emsp;&emsp;sk|服务提供商分配的Secret Key||false|string||
|&emsp;&emsp;apiUrl|服务提供商的API地址||false|string||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||
|&emsp;&emsp;isDefault|是否默认配置(1是 0否)||false|string||
|&emsp;&emsp;enableThinking|是否启用思考模式(模型支持时生效)||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 删除配置信息


**接口地址**:`/api/config/{configId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>软删除指定配置</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|configId||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 根据条件查询配置


**接口地址**:`/api/config`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回配置信息列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|配置分页查询|query|true|ConfigPageReq|ConfigPageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;configType|配置类型||false|string||
|&emsp;&emsp;configName|配置名称||false|string||
|&emsp;&emsp;modelType|模型类型||false|string||
|&emsp;&emsp;provider|服务提供商||false|string||
|&emsp;&emsp;isDefault|是否默认配置(1是 0否)||false|string||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 添加配置信息


**接口地址**:`/api/config`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>添加新的LLM/STT/TTS配置</p>



**请求示例**:


```javascript
{
  "configName": "",
  "configDesc": "",
  "configType": "",
  "modelType": "",
  "provider": "",
  "appId": "",
  "apiKey": "",
  "apiSecret": "",
  "ak": "",
  "sk": "",
  "apiUrl": "",
  "state": "",
  "isDefault": "",
  "enableThinking": true
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|configCreateReq|配置创建请求|body|true|ConfigCreateReq|ConfigCreateReq|
|&emsp;&emsp;configName|配置名称||true|string||
|&emsp;&emsp;configDesc|配置描述||false|string||
|&emsp;&emsp;configType|配置类型||true|string||
|&emsp;&emsp;modelType|模型类型||false|string||
|&emsp;&emsp;provider|服务提供商||true|string||
|&emsp;&emsp;appId|服务提供商分配的AppId||false|string||
|&emsp;&emsp;apiKey|服务提供商分配的ApiKey||false|string||
|&emsp;&emsp;apiSecret|服务提供商分配的ApiSecret||false|string||
|&emsp;&emsp;ak|服务提供商分配的Access Key||false|string||
|&emsp;&emsp;sk|服务提供商分配的Secret Key||false|string||
|&emsp;&emsp;apiUrl|服务提供商的API地址||false|string||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||
|&emsp;&emsp;isDefault|是否默认配置(1是 0否)||false|string||
|&emsp;&emsp;enableThinking|是否启用思考模式(模型支持时生效)||false|boolean||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 角色管理


## 更新角色信息


**接口地址**:`/api/role/{roleId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新语音助手角色配置</p>



**请求示例**:


```javascript
{
  "roleName": "",
  "roleDesc": "",
  "avatar": "",
  "voiceName": "",
  "ttsPitch": 0,
  "ttsSpeed": 0,
  "state": "",
  "ttsId": 0,
  "modelId": 0,
  "sttId": 0,
  "temperature": 0,
  "topP": 0,
  "vadEnergyTh": 0,
  "vadSpeechTh": 0,
  "vadSilenceTh": 0,
  "vadSilenceMs": 0,
  "isDefault": "",
  "memoryType": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||
|roleUpdateReq|更新角色|body|true|RoleUpdateReq|RoleUpdateReq|
|&emsp;&emsp;roleName|角色名称||false|string||
|&emsp;&emsp;roleDesc|角色描述||false|string||
|&emsp;&emsp;avatar|角色头像||false|string||
|&emsp;&emsp;voiceName|语音名称||false|string||
|&emsp;&emsp;ttsPitch|语音音调||false|number(double)||
|&emsp;&emsp;ttsSpeed|语音语速||false|number(double)||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||
|&emsp;&emsp;ttsId|TTS服务ID||false|integer(int32)||
|&emsp;&emsp;modelId|模型ID||false|integer(int32)||
|&emsp;&emsp;sttId|STT服务ID||false|integer(int32)||
|&emsp;&emsp;temperature|温度参数||false|number(double)||
|&emsp;&emsp;topP|Top-P参数||false|number(double)||
|&emsp;&emsp;vadEnergyTh|语音活动检测-能量阈值||false|number(float)||
|&emsp;&emsp;vadSpeechTh|语音活动检测-语音阈值||false|number(float)||
|&emsp;&emsp;vadSilenceTh|语音活动检测-静音阈值||false|number(float)||
|&emsp;&emsp;vadSilenceMs|语音活动检测-静音毫秒数||false|integer(int32)||
|&emsp;&emsp;isDefault|是否默认角色(1是 0否)||false|string||
|&emsp;&emsp;memoryType|记忆类型||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 删除角色信息


**接口地址**:`/api/role/{roleId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>删除指定的语音助手角色</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 根据条件查询角色信息


**接口地址**:`/api/role`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回角色信息列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|角色分页查询|query|true|RolePageReq|RolePageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;roleId|角色ID||false|integer(int32)||
|&emsp;&emsp;roleName|角色名称||false|string||
|&emsp;&emsp;isDefault|是否默认角色(1是 0否)||false|string||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 添加角色信息


**接口地址**:`/api/role`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>添加新的语音助手角色</p>



**请求示例**:


```javascript
{
  "roleName": "",
  "roleDesc": "",
  "avatar": "",
  "voiceName": "",
  "ttsPitch": 0,
  "ttsSpeed": 0,
  "state": "",
  "ttsId": 0,
  "modelId": 0,
  "sttId": 0,
  "temperature": 0,
  "topP": 0,
  "vadEnergyTh": 0,
  "vadSpeechTh": 0,
  "vadSilenceTh": 0,
  "vadSilenceMs": 0,
  "isDefault": "",
  "memoryType": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleCreateReq|创建角色|body|true|RoleCreateReq|RoleCreateReq|
|&emsp;&emsp;roleName|角色名称||true|string||
|&emsp;&emsp;roleDesc|角色描述||false|string||
|&emsp;&emsp;avatar|角色头像||false|string||
|&emsp;&emsp;voiceName|语音名称||false|string||
|&emsp;&emsp;ttsPitch|语音音调||false|number(double)||
|&emsp;&emsp;ttsSpeed|语音语速||false|number(double)||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||
|&emsp;&emsp;ttsId|TTS服务ID||false|integer(int32)||
|&emsp;&emsp;modelId|模型ID||false|integer(int32)||
|&emsp;&emsp;sttId|STT服务ID||false|integer(int32)||
|&emsp;&emsp;temperature|温度参数||false|number(double)||
|&emsp;&emsp;topP|Top-P参数||false|number(double)||
|&emsp;&emsp;vadEnergyTh|语音活动检测-能量阈值||false|number(float)||
|&emsp;&emsp;vadSpeechTh|语音活动检测-语音阈值||false|number(float)||
|&emsp;&emsp;vadSilenceTh|语音活动检测-静音阈值||false|number(float)||
|&emsp;&emsp;vadSilenceMs|语音活动检测-静音毫秒数||false|integer(int32)||
|&emsp;&emsp;isDefault|是否默认角色(1是 0否)||false|string||
|&emsp;&emsp;memoryType|记忆类型||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 测试语音合成


**接口地址**:`/api/role/testVoice`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>测试指定配置的语音合成效果</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|param|测试语音合成请求|query|true|TestVoiceReq|TestVoiceReq|
|&emsp;&emsp;message|消息文本||true|string||
|&emsp;&emsp;provider|语音合成提供方||true|string||
|&emsp;&emsp;ttsId|TTS配置ID||false|integer(int32)||
|&emsp;&emsp;voiceName|音色名称||false|string||
|&emsp;&emsp;ttsPitch|语音音调(0.5-2.0)||false|number(double)||
|&emsp;&emsp;ttsSpeed|语音语速(0.5-2.0)||false|number(double)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 获取本地 sherpa-onnx 音色列表


**接口地址**:`/api/role/sherpaVoices`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>扫描配置的本地 TTS 模型目录，自动识别模型类型和 speaker</p>



**请求参数**:


暂无


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 提示词模板管理


## 更新角色模板


**接口地址**:`/api/template/{templateId}`


**请求方式**:`PUT`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>更新提示词模板信息</p>



**请求示例**:


```javascript
{
  "templateName": "",
  "templateDesc": "",
  "templateContent": "",
  "category": "",
  "isDefault": "",
  "state": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|templateId||path|true|integer(int32)||
|templateUpdateReq|提示词模板更新请求|body|true|TemplateUpdateReq|TemplateUpdateReq|
|&emsp;&emsp;templateName|模板名称||false|string||
|&emsp;&emsp;templateDesc|模板描述||false|string||
|&emsp;&emsp;templateContent|模板内容||false|string||
|&emsp;&emsp;category|模板分类||false|string||
|&emsp;&emsp;isDefault|是否默认模板(1是 0否)||false|string||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 删除角色模板


**接口地址**:`/api/template/{templateId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>删除提示词模板（逻辑删除）</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|templateId||path|true|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 根据条件查询角色模板


**接口地址**:`/api/template`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回模板列表</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|req|提示词模板分页查询|query|true|TemplatePageReq|TemplatePageReq|
|&emsp;&emsp;pageNo|页码||false|integer(int32)||
|&emsp;&emsp;pageSize|每页数量||false|integer(int32)||
|&emsp;&emsp;templateName|模板名称||false|string||
|&emsp;&emsp;category|模板分类||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 添加角色模板


**接口地址**:`/api/template`


**请求方式**:`POST`


**请求数据类型**:`application/x-www-form-urlencoded,application/json`


**响应数据类型**:`*/*`


**接口描述**:<p>添加新的提示词模板</p>



**请求示例**:


```javascript
{
  "templateName": "",
  "templateDesc": "",
  "templateContent": "",
  "category": "",
  "isDefault": "",
  "state": ""
}
```


**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|templateCreateReq|提示词模板创建请求|body|true|TemplateCreateReq|TemplateCreateReq|
|&emsp;&emsp;templateName|模板名称||true|string||
|&emsp;&emsp;templateDesc|模板描述||false|string||
|&emsp;&emsp;templateContent|模板内容||true|string||
|&emsp;&emsp;category|模板分类||true|string||
|&emsp;&emsp;isDefault|是否默认模板(1是 0否)||false|string||
|&emsp;&emsp;state|状态(1启用 0禁用)||false|string||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


# 记忆管理


## 查询指定角色的摘要记忆


**接口地址**:`/api/memory/summary/{roleId}/{deviceId}`


**请求方式**:`GET`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>返回摘要记忆列表，可按设备 ID 筛选</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||
|deviceId||path|true|string||
|pageNo||query|false|integer(int32)||
|pageSize||query|false|integer(int32)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


## 批量删除指定角色的摘要记忆


**接口地址**:`/api/memory/summary/{roleId}/{deviceId}`


**请求方式**:`DELETE`


**请求数据类型**:`application/x-www-form-urlencoded`


**响应数据类型**:`*/*`


**接口描述**:<p>根据角色 ID 和设备 ID 批量删除摘要记忆</p>



**请求参数**:


| 参数名称 | 参数说明 | 请求类型    | 是否必须 | 数据类型 | schema |
| -------- | -------- | ----- | -------- | -------- | ------ |
|roleId||path|true|integer(int32)||
|deviceId||path|true|string||
|id||query|false|integer(int64)||


**响应状态**:


| 状态码 | 说明 | schema |
| -------- | -------- | ----- | 
|200|OK|ApiResponseObject|
|400|Bad Request|ApiResponseObject|
|401|Unauthorized|ApiResponseObject|
|403|Forbidden|ApiResponseObject|
|404|Not Found|ApiResponseObject|
|408|Request Timeout|ApiResponseObject|
|409|Conflict|ApiResponseObject|
|500|Internal Server Error|ApiResponseObject|


**响应状态码-200**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-400**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-401**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-403**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-404**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-408**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-409**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```


**响应状态码-500**:


**响应参数**:


| 参数名称 | 参数说明 | 类型 | schema |
| -------- | -------- | ----- |----- | 
|code|状态码：200-成功，500-失败|integer(int32)|integer(int32)|
|message|返回消息|string||
|data|返回数据|string||


**响应示例**:
```javascript
{
	"code": 200,
	"message": "操作成功",
	"data": {}
}
```