package xyz.wystudio.CloudPan.OneTwoThreePan.exception;

/**
 * AccessToken无效异常 <br/>
 * code 为 401 ，即抛出此异常
 */
public class AccessTokenInvalidException extends Exception {
    private final String msg;

    public AccessTokenInvalidException(String msg){
        super();
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
