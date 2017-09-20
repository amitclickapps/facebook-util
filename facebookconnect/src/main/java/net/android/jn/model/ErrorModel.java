package net.android.jn.model;

/**
 * Created by clickapps on 19/6/15.
 */
public class ErrorModel {
    private String ErrorType, ErrorMessage;
    private int ErrorCode;

    public ErrorModel(int ErrorCode, String ErrorType, String ErrorMessage) {
        this.ErrorCode = ErrorCode;
        this.ErrorType = ErrorType;
        this.ErrorMessage = ErrorMessage;
    }

    public String getErrorType() {
        return ErrorType;
    }

    public String getErrorMessage() {
        return ErrorMessage;
    }

    public int getErrorCode() {
        return ErrorCode;
    }
}
