package puzz.xsliu.detection2.detection.result;

import lombok.Data;

import java.io.Serializable;

/**
 * @description: <a href="mailto:xsl2011@outlook.com" />
 * @time: 2022/1/26/6:18 PM
 * @author: lxs
 */
@Data
public class Result<E> implements Serializable {
    private static final long serialVersionUID = -8899411550892526192L;

    protected String type;
    protected String errCode;
    protected String errMsg;
    protected boolean success;
    protected E data;

    public static <E> Result<E> success(E data){
        Result<E> result = new Result<>();
        result.setSuccess(true);
        result.setData(data);
        return result;
    }

    public static <E> Result<E> success(){
        return success(null);
    }

    public static <E> Result<E> failure(String errCode, String errMsg){
        Result<E> result = new Result<>();
        result.setSuccess(false);
        result.setErrCode(errCode);
        result.setErrMsg(errMsg);
        return result;
    }
    public static <E> Result<E> failure(String errMsg){
        Result<E> result = new Result<>();
        result.setSuccess(false);
        result.setErrMsg(errMsg);
        return result;
    }
}
