package xyz.wystudio.CloudPan.util;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtil {

    /**
     * 将异常信息转换为字符串，包含错误原因和堆栈跟踪
     * @param e 异常对象
     * @return 包含错误信息和堆栈跟踪的字符串
     */
    public static String getExceptionInfo(Exception e) {
        if (e == null) {
            return "Exception is null";
        }

        // 使用 StringWriter 和 PrintWriter 来捕获堆栈跟踪
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);

        // 构建完整的错误信息
        StringBuilder sb = new StringBuilder();
        sb.append("错误原因: ").append(e.getMessage()).append("\n");
        sb.append("异常类型: ").append(e.getClass().getName()).append("\n");
        sb.append("堆栈跟踪:\n").append(sw.toString());

        return sb.toString();
    }
}
