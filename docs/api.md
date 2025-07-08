# 计算端 API 说明

## 终端管理 API

### 查询终端列表

查询符合条件的终端设备列表，支持分页和多种筛选条件。

**请求方式：** `GET`  
**请求路径：** `/rest/v1/terminals`

#### 请求参数

| 参数名 | 类型 | 是否必填 | 默认值 | 描述 |
|--------|------|----------|--------|------|
| productId | String | 否 | - | 产品ID，用于筛选特定产品的终端设备 |
| terminalId | String | 否 | - | 终端ID，用于模糊查询终端设备 |
| terminalStatus | String | 否 | - | 终端状态，可选值见终端状态枚举 |
| createTimeBegin | String | 否 | - | 创建时间起始时间，格式：yyyy-MM-dd HH:mm:ss |
| createTimeEnd | String | 否 | - | 创建时间结束时间，格式：yyyy-MM-dd HH:mm:ss |
| page | Integer | 否 | 0 | 页码，从0开始 |
| size | Integer | 否 | 20 | 每页记录数 |

#### 终端状态枚举值

根据TerminalStatus枚举定义：
- `ONLINE` - 在线
- `OFFLINE` - 离线
- `ACTIVITY` - 激活

#### 响应格式

返回分页的Terminal对象列表：

```json
{
    "content": [
        {
            "id": "812ecf5f-1650-47ad-8f8a-93989cbb2db4",
            "terminalId": "mqttx_f9fa75e1",
            "name": null,
            "productId": "product-1",
            "status": "OFFLINE",
            "createdAt": "2025-07-01T07:29:03.181804Z",
            "updatedAt": "2025-07-01T07:29:14.213800Z"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 20,
        "sort": {
            "empty": false,
            "sorted": true,
            "unsorted": false
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "last": true,
    "totalPages": 1,
    "totalElements": 1,
    "first": true,
    "size": 20,
    "number": 0,
    "sort": {
        "empty": false,
        "sorted": true,
        "unsorted": false
    },
    "numberOfElements": 1,
    "empty": false
}
```

#### 请求示例

**1. 查询所有终端（默认分页）**
```http
GET /rest/v1/terminals
```

**2. 查询特定产品的终端**
```http
GET /rest/v1/terminals?productId=product123
```

**3. 根据状态查询终端**
```http
GET /rest/v1/terminals?terminalStatus=ONLINE
```

**4. 根据时间范围查询终端**
```http
GET /rest/v1/terminals?createTimeBegin=2024-01-01 00:00:00&createTimeEnd=2024-01-31 23:59:59
```

**5. 组合查询示例**
```http
GET /rest/v1/terminals?productId=product123&terminalStatus=ONLINE&page=1&size=10
```

**6. 使用curl命令示例**
```bash
curl -X GET "http://localhost:8080/rest/v1/terminals?productId=product123&terminalStatus=ONLINE&page=0&size=20"
```

#### 响应状态码

- `200 OK` - 查询成功
- `400 Bad Request` - 请求参数错误
- `500 Internal Server Error` - 服务器内部错误


