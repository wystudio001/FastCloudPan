package xyz.wystudio.CloudPan.api;

import java.util.List;

public interface Pan<T extends PanFile,V extends UserInfo> {
    public List<T> getFileList(String path) throws Exception;
    public boolean deleteFile(String path) throws Exception;
    public boolean renameFile(String oldPath, String newPath) throws Exception;
    public boolean moveFile(String oldPath, String newPath) throws Exception;
    public void uploadFile(String path, String fileName, onUploadListener listener) throws Exception;
    public T getFileInfo(String path) throws Exception;
    public String createFolder(String parent,String name) throws Exception;
    public V getUserInfo() throws Exception;
}
