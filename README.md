<h1 align="center">FastCloudPan</h1>

<p align="center">
<img alt="Version" src="https://img.shields.io/badge/version-1.0-3f51b5.svg?style=flat-square"/>
<img alt="Author" src="https://img.shields.io/badge/author-WYstudio-red.svg?style=flat-square"/>
<img alt="Download" src="https://img.shields.io/badge/download-28.15KB-brightgreen.svg?style=flat-square"/>
<a href="./LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-orange.svg?style=flat-square"/></a>
</p>

## 介绍
> FastCloudPan是一个聚合性网盘项目  
> 支持多种网盘的上传、下载、获取文件列表等基础功能  
>   
> 目前支持网盘：  
> - 123云盘
> - 蓝奏云网盘 (开发中)
> - FTP客户端 (开发中)
> - HTTP客户端 (开发中)
> - 敬请期待。。。

## 快速开始
### 环境要求
- JDK 8+
- Maven 3.6+（或 Gradle 7+）

### 引入依赖
> 目前暂未发布到公共 Maven 仓库，建议先本地构建后以本地依赖方式引入。  
> 如后续发布中央仓库，可在此处补充标准 Maven / Gradle 坐标。

### 推荐阅读顺序
1. 先阅读 `OneTwoThreePan`，了解核心入口能力。  
2. 再阅读 `OneTwoThreeFile`，了解文件模型字段。  
3. 最后阅读 `onUploadListener`，接入上传回调。  

## API文档
### 通用
> 上传回调监听：[onUploadListener.md](./doc/api/onUploadListener.md)
### 123云盘
> 123云盘实现类：[OneTwoThreePan.md](./doc/123pan/123pan.md)  
> 123云盘文件实现类：[OneTwoThreeFile.md](./doc/123pan/OneTwoThreeFile.md)  
> 123云盘用户信息实现类：[OneTwoThreeUserInfo.md](./doc/123pan/OneTwoThreeUserInfo.md)

## 功能支持矩阵
| 网盘/客户端 | 文件列表 | 上传 | 下载 | 状态 |
|---|---|---|---|---|
| 123云盘 | ✅ | ✅ | ✅ | 可用 |
| 蓝奏云网盘 | 🚧 | 🚧 | 🚧 | 开发中 |
| FTP客户端 | 🚧 | 🚧 | 🚧 | 开发中 |
| HTTP客户端 | 🚧 | 🚧 | 🚧 | 开发中 |

## 使用示例
### 123云盘
需要你去[123云盘开放平台](https://www.123pan.com/developer)申请key，才能使用  
```java
String clientId = "your-client_id";
String clientSecret = "your-client_secret";
// 获取 accessToken
String accessToken = OneTwoThreePan.getInstance().getAccessToken()[0];

//初始化云盘
OneTwoThreePan.getInstance().init(clientId, clientSecret, accessToken);
```  
  
获取用户手机号
```java
String phoneNumber = OneTwoThreePan.getInstance().getUserInfo().getPassport();
```  
  
获取根目录文件列表
```java
List<OneTwoThreeFile> fileList = OneTwoThreePan.getInstance().getFileList("0");
for(OneTwoThreeFile file : fileList){
    System.out.println("fileName:" + file.getName() + " fileId:" + file.getId());
}
```  
  
上传文件
```java
//要上传到的目录ID
String folderId = "0";
//要上传的文件路径
String filePath = "/xxx/xxx/1.png";
OneTwoThreePan.getInstance().uploadFile(folderId, filePath, new onUploadListener() {
    @Override
    public void onSuccess() {
        System.out.println("上传成功");
    }
    
    @Override
    public void onFailure(String error) {
        System.out.println("上传失败：" + error);
    }

    @Override
    public void onProgress(int progress) {
        System.out.println("上传进度：" + progress);
    }

    @Override
    public void onStateChanged(String state) {
        System.out.println(state);
    }
});
```

下载文件
```java
//要先获取文件的下载地址，然后再通过工具下载
String downloadUrl = OneTwoThreePan.getInstance().getFileDownloadUrl("fileId");
```
  
> 注意：  
> 123云盘的操作可能会抛出异常，所以建议使用 `try {}` 包裹
> ```java
> try{
>     OneTwoThreePan.getInstance().xxx();
> } catch (AccessTokenInvalidException e) {
>     System.out.println("AccessToken错误：" + e.getMsg());
> } catch (RequestTooManyException e) {
>     System.out.println("请求过于频繁：" + e.getMsg());
> } catch (UnknownException e) {
>     System.out.println("请求错误：" + e.getMsg());
> } catch (Exception e) {
>     System.out.println("请求错误：" + e);
> }
> ```

## 常见异常与处理建议
| 异常类型 | 触发场景 | 建议处理方式 |
|---|---|---|
| `AccessTokenInvalidException` | token 失效或错误 | 重新获取 `accessToken` 后重试 |
| `RequestTooManyException` | 请求频率过高 | 指数退避重试，降低并发 |
| `UnknownException` | 服务端或未知错误 | 记录日志并进行有限次数重试 |

## 版本与变更记录
- 当前版本：`1.0`  
- 建议新增 `CHANGELOG.md` 记录版本变更历史与升级注意事项。

## License
<a href="./LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-orange.svg?style=flat-square"/></a>

根据 MIT 许可证开源
