package xyz.wystudio.CloudPan.OneTwoThreePan.exception;

/**
 * 请求太频繁异常 <br/>
 * code 为 429 ， 即抛出此异常
 */
public class RequestTooManyException extends Exception{
    private final String msg;

    public RequestTooManyException(String msg){
        super();
        this.msg = msg;
    }

    public String getMsg(){
        return msg;
    }
}
