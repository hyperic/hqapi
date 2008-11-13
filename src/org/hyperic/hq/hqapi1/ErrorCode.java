package org.hyperic.hq.hqapi1;

public enum ErrorCode {
    /**
     * The given username and password could not be validated.
     */
    LOGIN_FAILURE("LoginFailure",
                  "The given username and password could not be validated"),
    /**
     * The requested object could not be found.
     */
    OBJECT_NOT_FOUND("ObjectNotFound",
                     "The requested object could not be found"),
    /**
     * The given object already exists.
     */
    OBJECT_EXISTS("ObjectExists",
                  "The given object already exists"),
    /**
     * The given parameters are incorrect. 
     */
    INVALID_PARAMETERS("InvalidParameters",
                       "The given parameters are incorrect"),
    /**
     * An unexpected error occured.
     */
    UNEXPECTED_ERROR("UnexpectedError",
                     "An unexpected error occured"),
    /**
     * The current user does not have permission for this operation.
     */
    PERMISSION_DENIED("PermissionDenied",
                      "The current user does not have permission for this operation"),

    /**
     * The requested API is not implemented.
     */
    NOT_IMPLEMENTED("NotImplemented",
                    "The requested API is not available");

    private final String _errorCode;
    private final String _reasonText;
    
    ErrorCode(String errorCode, String reasonText) {
        _errorCode = errorCode;
        _reasonText = reasonText;
    }

    public String getErrorCode() {
        return _errorCode;
    }

    public String getReasonText() {
        return _reasonText;
    }
}
