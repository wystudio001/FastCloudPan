# OneTwoThreeFile API 文档

## 类定义
```java
public class OneTwoThreeFile implements PanFile
```
- **功能**：表示123云盘中的文件或文件夹元数据，实现通用接口 `PanFile`。
- **数据来源**：通过API返回的JSON数据构造（`JsonNode`）。
- **线程安全**：非线程安全（字段均为基本类型或不可变字符串）。

---

## 字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| `fileName` | `String` | 文件/文件夹名称 |
| `fileId` | `int` | 文件唯一ID |
| `type` | `int` | 类型：`0`=文件，`1`=文件夹 |
| `size` | `long` | 文件大小（字节），文件夹为 `0` |
| `etag` | `String` | 文件MD5哈希值 |
| `status` | `int` | 审核状态（≥100表示审核驳回） |
| `parentFileId` | `int` | 父目录ID |
| `category` | `int` | 文件分类：`0`=未知，`1`=音频，`2`=视频，`3`=图片 |
| `trashed` | `int` | 回收站标记：`0`=否，`1`=是 |
| `updateAt` | `String` | 最后修改时间（格式：`yyyy-MM-dd HH:mm:ss`） |

---

## 构造方法

### `OneTwoThreeFile(JsonNode node)`
```java
public OneTwoThreeFile(JsonNode node)
```
- **描述**：通过API返回的JSON数据构造文件对象。
- **参数**：
    - `node`：包含文件元数据的JSON节点（字段名需与类字段对应）。
- **示例**：
  ```java
  JsonNode json = mapper.readTree("{\"filename\":\"test.txt\", \"fileId\":123, ...}");
  OneTwoThreeFile file = new OneTwoThreeFile(json);
  ```

---

## 核心方法（实现 `PanFile` 接口）

### `getName()`
```java
@Override
public String getName()
```
- **返回**：文件/文件夹名称（同 `fileName` 字段）。

---

### `getSize()`
```java
@Override
public Long getSize()
```
- **返回**：文件大小（字节），如果是文件夹则返回 `0L`。

---

### `isDirectory()`
```java
@Override
public boolean isDirectory()
```
- **返回**：`true` 表示是文件夹（`type == 1`）。

---

### `isFile()`
```java
@Override
public boolean isFile()
```
- **返回**：`true` 表示是文件（`type == 0`）。

---

### `getLastModified()`
```java
@Override
public String getLastModified()
```
- **返回**：最后修改时间字符串（同 `updateAt` 字段）。

---

## 扩展方法（123云盘特有）

### `getId()`
```java
public int getId()
```
- **返回**：文件唯一ID（同 `fileId` 字段）。

---

### `getEtag()`
```java
public String getEtag()
```
- **返回**：文件MD5哈希值（用于秒传校验）。

---

### `getStatus()`
```java
public int getStatus()
```
- **返回**：审核状态码（正常文件通常为 `0`）。

---

### `getParentFileId()`
```java
public int getParentFileId()
```
- **返回**：父目录ID（根目录为 `0`）。

---

### `getCategory()`
```java
public int getCategory()
```
- **返回**：文件分类码：
    - `0`：未知
    - `1`：音频
    - `2`：视频
    - `3`：图片

---

### `getTrashed()`
```java
public int getTrashed()
```
- **返回**：回收站标记（`1`=在回收站中）。

---

## 使用示例

### 1. 判断文件类型
```java
OneTwoThreeFile file = new OneTwoThreeFile(jsonNode);
if (file.isDirectory()) {
    System.out.println("这是一个文件夹");
} else if (file.isFile()) {
    System.out.println("文件大小：" + file.getSize() + " bytes");
}
```

### 2. 获取文件详情
```java
System.out.println("文件名：" + file.getName());
System.out.println("最后修改时间：" + file.getLastModified());
System.out.println("MD5：" + file.getEtag());
```

---

## 注意事项
1. **字段默认值**：未被API返回的字段可能为 `0`/`null`（如文件夹的 `size`）。
2. **时间格式**：`updateAt` 的格式为 `yyyy-MM-dd HH:mm:ss`（如 `"2023-10-25 15:30:00"`）。
3. **状态码**：`status` 和 `category` 的具体含义需参考官方文档。

---

> **版本**：1.0  
> **最后更新**：2025-6-2