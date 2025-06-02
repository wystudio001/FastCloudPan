<h1 align="center">FastCloudPan</h1>

<p align="center">
<img alt="Version" src="https://img.shields.io/badge/version-1.0-3f51b5.svg?style=flat-square"/>
<img alt="Author" src="https://img.shields.io/badge/author-WYstudio-red.svg?style=flat-square"/>
<img alt="Download" src="https://img.shields.io/badge/download-8.62M-brightgreen.svg?style=flat-square"/>
<a href="https://github.com/wystudio001/TiePlugin-AutoBackup/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-orange.svg?style=flat-square"/></a>
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

## API文档
### 通用
> 上传回调监听：[onUploadListener.md](./doc/api/onUploadListener.md)
### 123云盘
> 123云盘实现类：[OneTwoThreePan.md](./doc/123pan/123pan.md)  
> 123云盘文件实现类：[OneTwoThreeFile.md](./doc/123pan/OneTwoThreeFile.md)  
> 123云盘用户信息实现类：[OneTwoThreeUserInfo.md](./doc/123pan/OneTwoThreeUserInfo.md)

## 使用示例
### 123云盘
需要你去[123云盘开放平台](https://www.123pan.com/developer)申请key，才能使用  
```java
String client_id = "your-client_id";
String client_secret = "your-client_secret";
//获取accesToken
String accesToken = OneTwoThreePan.getInstance().getAccessToken()[0];

//初始化云盘
OneTwoThreePan.getInstance().init(client_id, client_secret,accesToken);
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
  
> 注意：  
> 123云盘的操作可能会抛出异常，所以需要进行try{}包裹
> ```java
> try{
>     OneTwoThreePan.getInstance().xxx();
> } catch (AccessTokenInvalidException e) {
>     System.out.println("AccessToken错误：" + e.getMsg());
> } catch (RequestTooManyException e) {
>     System.out.println("请求过于频繁：" + e.getMsg());
> } catch (UnkonwnException e) {
>     System.out.println("请求错误：" + e.getMsg());
> } catch (Exception e) {
>     System.out.println("请求错误：" + e);
> }
> ```

## License
<a href="https://github.com/wystudio001/TiePlugin-AutoBackup/blob/main/LICENSE"><img alt="License" src="https://img.shields.io/badge/license-MIT-orange.svg?style=flat-square"/></a>

根据 MIT 许可证开源