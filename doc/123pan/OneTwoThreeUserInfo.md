# OneTwoThreeUserInfo API 文档

## 类定义
```java
public class OneTwoThreeUserInfo implements UserInfo
```
- **功能**：表示123云盘用户的账户信息和存储空间详情，实现通用接口 `UserInfo`
- **数据来源**：通过用户信息API返回的JSON数据构造（`JsonNode`）
- **线程安全**：非线程安全（字段均为基本类型或不可变字符串）

---

## 字段说明

| 字段名              | 类型        | 描述                                 |
|------------------|-----------|------------------------------------|
| `uid`            | `int`     | 用户唯一ID                             |
| `nickName`       | `String`  | 用户昵称                               |
| `headImage`      | `String`  | 头像URL                              |
| `passport`       | `String`  | 绑定的手机号                             |
| `mail`           | `String`  | 绑定的邮箱地址                            |
| `spaceUsed`      | `long`    | 已使用的存储空间（字节）                       |
| `spacePermanent` | `long`    | 永久存储空间配额（字节）                       |
| `spaceTemp`      | `long`    | 临时存储空间配额（字节）                       |
| `spaceTempExpr`  | `String`  | 临时空间到期时间（格式：`yyyy-MM-dd HH:mm:ss`） |
| `isVip`          | `boolean` | 是否是VIP会员                           |
| `directTraffic`  | `int`     | 剩余直链流量（MB）                         |
| `isHideUID`      | `boolean` | 直链是否隐藏用户UID                        |

---

## 构造方法

### `OneTwoThreeUserInfo(JsonNode node)`
```java
public OneTwoThreeUserInfo(JsonNode node)
```
- **描述**：通过API返回的JSON数据构造用户信息对象
- **参数**：
    - `node`：包含用户信息的JSON节点（字段名需与类字段对应）
- **示例**：
  ```java
  JsonNode json = mapper.readTree("{\"uid\":1001, \"nickname\":\"张三\", ...}");
  OneTwoThreeUserInfo user = new OneTwoThreeUserInfo(json);
  ```

---

## 核心方法（实现 `UserInfo` 接口）

### `getName()`
```java
@Override
public String getName()
```
- **返回**：用户昵称（同 `nickName` 字段）

---

## 扩展方法（123云盘特有）

### `getUid()`
```java
public int getUid()
```
- **返回**：用户唯一ID

---

### `getHeadImage()`
```java
public String getHeadImage()
```
- **返回**：头像URL（可能为空字符串）

---

### `getPassport()`
```java
public String getPassport()
```
- **返回**：绑定的手机号（可能为空字符串）

---

### `getMail()`
```java
public String getMail()
```
- **返回**：绑定的邮箱地址（可能为空字符串）

---

### 存储空间相关方法

| 方法名                   | 返回类型     | 说明         |
|-----------------------|----------|------------|
| `getSpaceUsed()`      | `long`   | 已用空间（字节）   |
| `getSpacePermanent()` | `long`   | 永久空间配额（字节） |
| `getSpaceTemp()`      | `long`   | 临时空间配额（字节） |
| `getSpaceTempExpr()`  | `String` | 临时空间到期时间   |

---

### 会员相关方法

| 方法名                  | 返回类型      | 说明         |
|----------------------|-----------|------------|
| `isVip()`            | `boolean` | 是否是VIP会员   |
| `getDirectTraffic()` | `int`     | 剩余直链流量（MB） |
| `isHideUID()`        | `boolean` | 直链是否隐藏UID  |

---

## 使用示例

### 1. 获取基础用户信息
```java
OneTwoThreeUserInfo user = pan.getUserInfo();
System.out.println("用户名：" + user.getName());
System.out.println("UID：" + user.getUid());
System.out.println("头像：" + user.getHeadImage());
```

### 2. 检查存储空间
```java
long usedGB = user.getSpaceUsed() / 1024 / 1024 / 1024;
long totalGB = (user.getSpacePermanent() + user.getSpaceTemp()) / 1024 / 1024 / 1024;
System.out.printf("已用空间：%dGB/%dGB%n", usedGB, totalGB);
```

### 3. 检查会员状态
```java
if (user.isVip()) {
    System.out.println("VIP会员，剩余直链流量：" + user.getDirectTraffic() + "MB");
}
```

---

## 注意事项
1. **敏感信息**：`passport`(手机号)和`mail`(邮箱)可能为空，取决于用户绑定情况
2. **空间计算**：`spaceUsed`包含永久和临时空间的使用量总和
3. **时间格式**：`spaceTempExpr` 的格式为 `yyyy-MM-dd HH:mm:ss`
4. **流量单位**：`directTraffic` 单位为MB（兆字节）

---

> **版本**：1.0  
> **最后更新**：2025-6-2