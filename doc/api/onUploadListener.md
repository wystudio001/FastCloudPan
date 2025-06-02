# `onUploadListener` 接口

## 概述
`onUploadListener` 是一个回调接口，用于监听文件上传过程中的各种状态和事件。该接口定义了上传成功、失败、进度更新和状态变化时的回调方法。

## 包路径
`xyz.wystudio.CloudPan.api`

## 方法说明

### `onSuccess()`
**描述**: 当文件上传成功完成时调用此方法。

**参数**: 无

**返回值**: 无

### `onFailure(String error)`
**描述**: 当文件上传失败时调用此方法。

**参数**:
- `error` (String) - 描述失败原因的错误信息

**返回值**: 无

### `onProgress(int progress)`
**描述**: 在上传过程中定期调用，报告上传进度。

**参数**:
- `progress` (int) - 当前上传进度百分比(0-100)

**返回值**: 无

### `onStateChanged(String state)`
**描述**: 当上传状态发生变化时调用此方法。

**参数**:
- `state` (String) - 描述新状态的字符串

**返回值**: 无