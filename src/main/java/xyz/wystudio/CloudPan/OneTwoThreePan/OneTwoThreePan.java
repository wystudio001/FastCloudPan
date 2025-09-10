package xyz.wystudio.CloudPan.OneTwoThreePan;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.*;
import xyz.wystudio.CloudPan.OneTwoThreePan.exception.AccessTokenInvalidException;
import xyz.wystudio.CloudPan.OneTwoThreePan.exception.RequestTooManyException;
import xyz.wystudio.CloudPan.OneTwoThreePan.exception.UnkonwnException;
import xyz.wystudio.CloudPan.api.Pan;
import xyz.wystudio.CloudPan.api.onUploadListener;
import xyz.wystudio.CloudPan.util.FileUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 此类为 123云盘 的相关操作实现类
 */
public class OneTwoThreePan implements Pan<OneTwoThreeFile,OneTwoThreeUserInfo> {
    private static OneTwoThreePan instance;

    private final String apiUrl = "https://open-api.123pan.com";
    private String clientId = "";
    private String clientSecret = "";
    private String accessToken;

    private OkHttpClient okHttpClient;

    public static OneTwoThreePan getInstance(){
        if (instance == null) {
            instance = new OneTwoThreePan();
        }
        return instance;
    }

    /**
     * 需要提前调用此方法进行初始化
     *
     * @param clientId 你的 clientId
     * @param clientSecret 你的 clientSecret
     */
    public void init(String clientId, String clientSecret, String accessToken){
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.accessToken = accessToken;
        this.okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * 通过 client_id 和 client_secret 获取 access_token 和 expiredAt(过期时间)
     *
     * @return 返回一个字符串数组，第一个为accessToken，第二个为expiredAt(过期时间) <br/>
     * 如果没有正确请求到或响应为空，则返回null
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    public String[] getAccessToken() throws Exception {
        String url = apiUrl + "/api/v1/access_token";
        OkHttpClient client = okHttpClient;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("clientID", clientId);
        data.put("clientSecret", clientSecret);

        RequestBody body = RequestBody.create(data.toString(), MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url(url)
                .header("platform","open_platform")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        JsonNode resultData = handleResponseByData(response);

        if (resultData == null) {
            return null;
        }

        accessToken = resultData.get("accessToken").asText();
        String expiredAt = resultData.get("expiredAt").asText();

        return new String[]{accessToken, String.valueOf(expiredAt)};
    }

    /**
     * 获取用户信息 ，账户uid、手机号
     *
     * @return 返回一个OneTwoThreeUserInfo对象 </br>
     * 如果没有正确请求到或响应为空，则返回null
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public OneTwoThreeUserInfo getUserInfo() throws Exception {
        String url = apiUrl + "/api/v1/user/info";
        OkHttpClient client = okHttpClient;

        Request request = createRequest("GET",url,"");

        Response response = client.newCall(request).execute();
        JsonNode resultData = handleResponseByData(response);

        if (resultData == null) {
            return null;
        }

        return new OneTwoThreeUserInfo(resultData);
    }

    /**
     * 根据文件夹Id获取文件列表 </br>
     * 将循环获取整个文件夹内的所有文件
     *
     * @param folderId 文件夹Id 0为根目录
     * @return 返回一个装有OneTwoThreeFile的List </br>
     * 如果没有正确请求到或响应为空，则返回null
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public List<OneTwoThreeFile> getFileList(String folderId) throws Exception {
        String url = apiUrl + "/api/v2/file/list?parentFileId=" + folderId + "&limit=100&lastFileId=";
        List<OneTwoThreeFile> list = new ArrayList<>();

        int lastFileId = 0;

        while (lastFileId != -1) {
            OkHttpClient client = okHttpClient;
            Request request = createRequest("GET",url + lastFileId,"");
            Response response = client.newCall(request).execute();

            JsonNode resultData = handleResponseByData(response);

            if (resultData == null) {
                return null;
            }

            JsonNode fileList = resultData.get("fileList");
            for (int i = 0; i < fileList.size(); i++) {
                list.add(new OneTwoThreeFile(fileList.get(i)));
            }
            lastFileId = resultData.get("lastFileId").asInt();
        }
        return list;
    }

    /**
     * 将文件移动到回收站
     *
     * @param fileId 文件Id
     * @return 移动成功则返回真，否则返回假
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    public boolean moveFileToTrash(String fileId) throws Exception {
        String url = apiUrl + "/api/v1/file/trash";
        OkHttpClient client = okHttpClient;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.putArray("fileIDs").add(Integer.parseInt(fileId));

        Request request = createRequest("POST",url,data.toString());

        Response response = client.newCall(request).execute();

        return handleResponseByMessage(response);
    }

    /**
     * 彻底删除文件 </br>
     * 若文件不在回收站，会自动移动到回收站再删除
     *
     * @param fileId 文件Id
     * @return 删除成功返回真，否则返回假
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public boolean deleteFile(String fileId) throws Exception {
        // 彻底删除文件前，要先将文件移动到回收站
        if (!moveFileToTrash(fileId)) {
            return false;
        }

        String url = apiUrl + "/api/v1/file/delete";
        OkHttpClient client = okHttpClient;

        ObjectMapper mapper = new ObjectMapper();
        ArrayNode data = mapper.createObjectNode().arrayNode();
        data.add(Integer.parseInt(fileId));

        Request request = createRequest("POST",url,data.toString());

        Response response = client.newCall(request).execute();

        return handleResponseByMessage(response);
    }

    /**
     * 通过文件Id对文件进行重命名
     *
     * @param fileId 文件Id
     * @param newName 新文件名
     * @return 成功返回真，失败返回假
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public boolean renameFile(String fileId, String newName) throws Exception {
        String url = apiUrl + "/api/v1/file/name";
        OkHttpClient client = okHttpClient;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("fileId", Integer.parseInt(fileId));
        data.put("fileName", newName);

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByMessage(response);
    }

    /**
     * 移动文件
     *
     * @param fileId 文件Id
     * @param newFolderId 新文件夹Id
     * @return 成功返回真，失败返回假
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public boolean moveFile(String fileId, String newFolderId) throws Exception {
        String url = apiUrl + "/api/v1/file/move";
        OkHttpClient client = okHttpClient;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("toParentFileID", Integer.parseInt(newFolderId));
        data.putArray("fileIDs").add(Integer.parseInt(fileId));

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByMessage(response);
    }

    /**
     * 通过文件Id获取文件详细信息
     *
     * @param fileId 文件Id
     * @return 成功则返回对应文件的 OneTwoThreeFile 对象 </br>
     * 如果没有正确请求到或响应为空，则返回null
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public OneTwoThreeFile getFileInfo(String fileId) throws Exception {
        String url = apiUrl + "/api/v1/file/detail?fileID=" + fileId;
        OkHttpClient client = okHttpClient;

        Request request = createRequest("GET",url,"");
        Response response = client.newCall(request).execute();

        JsonNode resultData = handleResponseByData(response);
        if (resultData == null) {
            return null;
        }

        return new OneTwoThreeFile(resultData);
    }

    /**
     * 创建目录
     *
     * @param parent 父目录ID，根目录为0
     * @param name 目录名
     * @return 创建成功返回目录ID </br>
     * 如果没有正确请求到或响应为空，则返回null
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    @Override
    public String createFolder(String parent, String name) throws Exception {
        String url = apiUrl + "/api/v1/file/mkdir";
        OkHttpClient client = okHttpClient;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("name", name);
        data.put("parentID", Integer.parseInt(parent));

        Request request = createRequest("POST",url,data.toString());

        Response response = client.newCall(request).execute();
        JsonNode resultData = handleResponseByData(response);

        if (resultData == null) {
            return null;
        }

        return resultData.get("dirID").asText();
    }

    @Override
    public void uploadFile(String folderId, String path, onUploadListener listener) throws Exception {
        listener.onStateChanged("文件开始上传...");
        String fileMD5 = FileUtil.getFileMD5(path);
        File file = new File(path);
        String fileName = file.getName();
        long fileSize = file.length();

        listener.onStateChanged("创建文件...");
        JsonNode createNode = createOrReUseFile(Integer.parseInt(folderId), fileName, fileMD5, fileSize);
        System.out.println(createNode);
        if (createNode == null) {
            listener.onFailure("createNode为null");
            return;
        }

        //触发秒传，直接上传成功
        if (createNode.get("reuse").asBoolean()) {
            listener.onSuccess();
            return;
        }

        String preuploadID = createNode.get("preuploadID").asText();
        long sliceSize = createNode.get("sliceSize").asLong();

        //文件总字节数：fileSize，总分片数：totalSlices，分片大小：sliceSize
        int totalSlices = 1;
        if(fileSize > sliceSize){
            totalSlices = (int) Math.ceil((double) fileSize / sliceSize);
        }
        listener.onStateChanged("开始上传分片：0/" + totalSlices + "...");

        try(FileInputStream fis = new FileInputStream(file)){
            for (int sliceNo = 1; sliceNo <= totalSlices; sliceNo++) {
                JsonNode SliceUrlNode = getUploadSliceUrl(preuploadID, sliceNo);
                System.out.println(SliceUrlNode);
                if (SliceUrlNode == null) {
                    listener.onFailure("SliceUrlNode为null");
                    return;
                }

                String uploadUrl = SliceUrlNode.get("presignedURL").asText();
                listener.onStateChanged("开始上传分片：" + sliceNo + "/" + totalSlices + "...");

                long start = (sliceNo - 1) * sliceSize;
                long end = Math.min(sliceNo * sliceSize, fileSize);
                try (InputStream sliceStream = new FileInputStream(file)) {
                    sliceStream.skip(start);
                    byte[] buffer = new byte[(int) (end - start)];
                    sliceStream.read(buffer);
                    //上传分片
                    if (!uploadSlice(uploadUrl, buffer)){
                        listener.onFailure("分片" + sliceNo + " 上传失败！");
                        return;
                    }
                }
            }

            //列举已上传分片
            if (fileSize > sliceSize) {
                JsonNode listUploadedSliceNode = listUploadedSlice(preuploadID);
                System.out.println(listUploadedSliceNode);
                if (listUploadedSliceNode == null) {
                    listener.onFailure("listUploadedSliceNode为null");
                    return;
                }
                ArrayNode parts = (ArrayNode) listUploadedSliceNode.get("parts");
                int uploadedSlices = parts.size();
                if (uploadedSlices != totalSlices) {
                    listener.onFailure("已上传分片数" + uploadedSlices + "与总分片数" + totalSlices + "不同！");
                    return;
                }
            }

            listener.onStateChanged("通知服务器合并文件...");
            JsonNode completeNode = noticeUploadComplete(preuploadID);
            System.out.println(completeNode);
            if (completeNode == null) {
                listener.onFailure("completeNode为null");
                return;
            }

            if (completeNode.get("completed").asBoolean()) {
                listener.onSuccess();
                return;
            }

            // 异步轮询上传结果
            listener.onStateChanged("正在查询是否上传成功...");

            final int MAX_RETRY_TIMES = 30; // 最大重试次数
            final long POLL_INTERVAL_MS = 1200; // 轮询间隔时间(毫秒)

            boolean isCompleted = false;
            int retryCount = 0;

            while (!isCompleted && retryCount < MAX_RETRY_TIMES) {
                retryCount++;
                listener.onStateChanged(String.format("查询结果，第%d次...", retryCount));

                JsonNode resultNode = asyncGetUploadResult(preuploadID);
                if (resultNode == null) {
                    listener.onFailure(String.format("第%d次查询时获取结果为空", retryCount));
                    return;
                }

                isCompleted = resultNode.get("completed").asBoolean();

                if (!isCompleted) {
                    try {
                        Thread.sleep(POLL_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        listener.onFailure("轮询上传结果过程被中断");
                        return;
                    }
                }
            }

            if (isCompleted) {
                listener.onSuccess();
            } else {
                listener.onFailure(String.format("超过最大重试次数(%d次)仍未完成上传", MAX_RETRY_TIMES));
            }
        }
    }

    /**
     * 上传文件流程-1：创建文件或恢复文件 <br/>
     * 若有重名文件，则覆盖上传 <br/>
     * ● 恢复文件：即实现秒传效果 <br/>
     * ● 生成分片ID：给定限制让你根据ID和大小先拆分文件，再上传，最后再合并。
     *
     * @param parentFileID 父目录id，上传到根目录时填写 0
     * @param fileName 文件名
     * @param md5 文件md5
     * @param size  文件大小，单位为byte字节
     * @return 返回一个JsonNode对象(可能为null)：<br/>
     * fileID：文件ID。当123云盘已有该文件,则会发生秒传。此时会将文件ID字段返回。唯一 <br/>
     * preuploadID：预上传ID(如果 reuse 为 true 时,该字段不存在) <br/>
     * reuse：是否秒传，返回true时表示文件已上传成功 <br/>
     * sliceSize：分片大小，必须按此大小生成文件分片再上传
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    private JsonNode createOrReUseFile(int parentFileID,String fileName,String md5,long size) throws Exception {
        String url = apiUrl + "/upload/v1/file/create";

        OkHttpClient client = okHttpClient;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("parentFileID", parentFileID);
        data.put("fileName", fileName);
        data.put("etag", md5);
        data.put("size", size);
        data.put("duplicate","2");

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByData(response);
    }

    /**
     * 获取分片上传地址
     *
     * @param preuploadID 文件预上传ID
     * @param sliceNo 分片序号，从1开始递增
     * @return 一个JsonNode对象 <br/>
     * presignedURL：上传地址
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    private JsonNode getUploadSliceUrl(String preuploadID,int sliceNo) throws Exception {
        String url = apiUrl + "/upload/v1/file/get_upload_url";

        OkHttpClient client = okHttpClient;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("preuploadID", preuploadID);
        data.put("sliceNo", sliceNo);

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByData(response);
    }

    /**
     * 上传分片
     *
     * @param url 分片上传url
     * @param data 分片数据，为字节数组byte[]
     * @return 是否上传成功
     * @throws Exception 出现异常则抛出
     */
    private boolean uploadSlice(String url,byte[] data) throws Exception {
        OkHttpClient client = okHttpClient;

        Request request = new Request.Builder()
                .url(url)
                .put(RequestBody.create(data, MediaType.get("application/octet-stream")))
                .build();
        Response response = client.newCall(request).execute();
        return response.isSuccessful();
    }

    /**
     * 列举已上传分片 <br/>
     * 该接口用于最后一片分片上传完成时，列出云端分片供用户自行比对。比对正确后调用上传完毕接口。<br/>
     * 当文件大小小于 sliceSize 分片大小时，无需调用该接口，该结果将返回空值。
     *
     * @param preuploadID 文件预上传ID
     * @return 一个JsonNode对象 <br/>
     * parts：分片列表 <br/>
     * parts[*].partNumber：分片编号 <br/>
     * parts[*].size：分片大小 <br/>
     * parts[*].etag：分片md5
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    private JsonNode listUploadedSlice(String preuploadID) throws Exception {
        String url = apiUrl + "/upload/v1/file/list_upload_parts";
        OkHttpClient client = okHttpClient;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("preuploadID", preuploadID);

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByData(response);
    }

    /**
     * 通知服务器分片上传完毕开始合并文件
     *
     * @param preuploadID 文件预上传ID
     * @return 一个JsonNode对象 <br/>
     * async：是否需要异步查询上传结果。false为无需异步查询,已经上传完毕。true 为需要异步查询上传结果。 <br/>
     * completed：上传是否完成 <br/>
     * fileID：上传完成文件id
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    private JsonNode noticeUploadComplete(String preuploadID) throws Exception {
        String url = apiUrl + "/upload/v1/file/upload_complete";
        OkHttpClient client = okHttpClient;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("preuploadID", preuploadID);

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByData(response);
    }

    /**
     * 异步轮询获取上传结果
     *
     * @param preuploadID 文件预上传ID
     * @return 一个JsonNode对象 <br/>
     * completed：上传合并是否完成,如果为false,请至少1秒后发起轮询 <br/>
     * fileID：上传完成返回对应fileID
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    private JsonNode asyncGetUploadResult(String preuploadID) throws Exception {
        String url = apiUrl + "/upload/v1/file/upload_async_result";
        OkHttpClient client = okHttpClient;
        ObjectMapper mapper = new ObjectMapper();
        ObjectNode data = mapper.createObjectNode();
        data.put("preuploadID", preuploadID);

        Request request = createRequest("POST",url,data.toString());
        Response response = client.newCall(request).execute();

        return handleResponseByData(response);
    }

    /**
     * 获取文件下载链接
     *
     * @param fileId 文件id
     * @return 文件下载链接
     * @throws Exception 如果响应不为空，但code也不为0，则抛出装有message字段对应的异常
     */
    public String getFileDownloadUrl(String fileId) throws Exception {
        String url = apiUrl + "/api/v1/file/download_info" + "?fileId=" + fileId;
        OkHttpClient client = okHttpClient;

        Request request = createRequest("GET",url,"");
        Response response = client.newCall(request).execute();

        return handleResponseByData(response).get("downloadUrl").asText();
    }

    /**
     * 根据请求方法不同，获取不同的Request
     *
     * @param method 请求方法
     * @param url 请求链接
     * @param body 请求体
     * @return 对应的Request <br/>
     * 如果传入的 method 在方法中没有表示，则返回一个默认的Request
     * @throws Exception 如果出现错误则抛出对应异常
     */
    private Request createRequest(String method, String url, String body) throws Exception {
        return switch (method) {
            case "GET" -> new Request.Builder()
                    .url(url)
                    .get()
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("platform", "open_platform")
                    .build();
            case "POST" -> new Request.Builder()
                    .url(url)
                    .post(RequestBody.create(body, MediaType.get("application/json; charset=utf-8")))
                    .addHeader("Authorization", "Bearer " + accessToken)
                    .addHeader("platform", "open_platform")
                    .build();
            default -> new Request.Builder()
                    .build();
        };
    }

    /**
     * 获取响应中 data 字段内容
     *
     * @param response okhttp响应
     * @return 返回data字段JsonNode </br>
     * 若请求未成功或响应为空，则返回null
     * @throws Exception 若出现错误则抛出对应异常
     */
    private JsonNode handleResponseByData(Response response) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        if (!response.isSuccessful()) {
            throw new Exception("响应未成功，状态码：" + response.code());
        }

        if (response.body() == null) {
            return null;
        }
        JsonNode result = mapper.readTree(response.body().string());

        if (result.get("code").asInt() != 0) {
            handleErrorCode(result.get("code").asInt(),result.get("message").asText());
        }

        return result.get("data");
    }

    /**
     * 获取响应 message 字段
     *
     * @param response okhttp响应
     * @return 若 message 字段内容为 ok，则返回true </br>
     * 否则返回false
     * @throws Exception 若出现错误则抛出对应异常
     */
    private boolean handleResponseByMessage(Response response) throws Exception {
        if (!response.isSuccessful()) {
            throw new Exception("响应未成功，状态码：" + response.code());
        }

        if (response.body() == null) {
            return false;
        }

        JsonNode result = new ObjectMapper().readTree(response.body().string());
        if (result.get("code").asInt() != 0) {
            handleErrorCode(result.get("code").asInt(),result.get("message").asText());
        }

        return result.get("message").asText().equals("ok");
    }

    /**
     * 用于处理 code 字段不为 1 时的情况
     *
     * @param code 响应的code字段
     * @param msg 响应的message字段
     * @throws Exception 返回对应情况的异常
     */
    private void handleErrorCode(int code, String msg) throws Exception {
        switch (code) {
            case 401:
                throw new AccessTokenInvalidException(msg);
            case 429:
                throw new RequestTooManyException(msg);
            default:
                throw new UnkonwnException(msg);
        }
    }
}
