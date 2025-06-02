package xyz.wystudio.CloudPan.api;

/**
 * 上传文件时的监听状态Listener
 */
public interface onUploadListener {
    //上传成功
    void onSuccess();
    //上传失败
    void onFailure(String error);
    //上传进度
    void onProgress(int progress);
    //上传状态发生变化
    void onStateChanged(String state);
}
