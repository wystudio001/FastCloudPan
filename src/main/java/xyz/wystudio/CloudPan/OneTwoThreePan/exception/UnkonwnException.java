package xyz.wystudio.CloudPan.OneTwoThreePan.exception;

/**
 * 未知异常 <br/>
 * code 不为 0 、401 、429 ，即抛出此异常
 */
public class UnkonwnException extends Exception{
    private final String msg;

    public UnkonwnException(String msg){
        super();
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
