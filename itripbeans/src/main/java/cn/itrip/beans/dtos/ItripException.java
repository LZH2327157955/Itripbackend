package cn.itrip.beans.dtos;

/**
 * Created by zezhong.shang on 17-5-9.
 */
public class ItripException extends Exception{
    //异常码
    private String exceptionCode;
    //异常信息
    private String message;

    public ItripException(String exceptionCode) {
        super();
        this.exceptionCode = exceptionCode;
    }

    public ItripException(String exceptionCode, String message) {
        super();
        this.exceptionCode = exceptionCode;
        this.message = message;
    }

    public String getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(String exceptionCode) {
        this.exceptionCode = exceptionCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
