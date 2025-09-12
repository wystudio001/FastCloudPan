package xyz.wystudio.CloudPan.OneTwoThreePan;

import com.fasterxml.jackson.databind.JsonNode;
import xyz.wystudio.CloudPan.api.PanFile;

/**
 * 此类为 123云盘 的文件实现类
 */
public class OneTwoThreeFile implements PanFile {
    // 文件名
    String fileName;
    // 文件Id
    int fileId;
    // 文件类型 0-文件  1-文件夹
    int type;
    // 文件大小
    long size;
    // 文件md5值
    String etag;
    // 文件审核状态。 大于 100 为审核驳回文件
    int status;
    // 目录ID
    int parentFileId;
    // 文件分类：0-未知 1-音频 2-视频 3-图片
    int category;
    // 文件是否在回收站标识：0 否 1是
    int trashed;
    // 文件修改日期
    String updateAt;

    public OneTwoThreeFile(JsonNode node){
        this.fileName = node.get("filename").asText();

        if (node.has("fileId")){
            this.fileId = node.get("fileId").asInt();
        } else if (node.has("fileID")) {
            this.fileId = node.get("fileID").asInt();
        }

        this.type = node.get("type").asInt();
        this.size = node.get("size").asLong();
        this.etag = node.get("etag").asText();
        this.status = node.get("status").asInt();

        if (node.has("parentFileId")){
            this.parentFileId = node.get("parentFileId").asInt();
        } else if (node.has("parentFileID")) {
            this.parentFileId = node.get("parentFileID").asInt();
        } else {
            this.parentFileId = 0;
        }

        if (node.has("category")){
            this.category = node.get("category").asInt();
        }else {
            this.category = 0;
        }

        this.trashed = node.get("trashed").asInt();

        if (node.has("updateAt")){
            this.updateAt = node.get("updateAt").asText();
        } else if (node.has("createAt")) {
            this.updateAt = node.get("createAt").asText();
        }else {
            this.updateAt = "未知";
        }
    }

    @Override
    public String getName() {
        return fileName;
    }

    @Override
    public Long getSize() {
        return size;
    }

    public int getId() {
        return fileId;
    }

    public String getEtag() {
        return etag;
    }

    public int getStatus() {
        return status;
    }

    public int getParentFileId() {
        return parentFileId;
    }

    public int getCategory() {
        return category;
    }

    public int getTrashed() {
        return trashed;
    }

    @Override
    public boolean isDirectory() {
        return type == 1;
    }

    @Override
    public boolean isFile() {
        return type == 0;
    }

    @Override
    public String getLastModified() {
        return updateAt;
    }
}