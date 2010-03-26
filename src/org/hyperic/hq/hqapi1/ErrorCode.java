/*
 * 
 * NOTE: This copyright does *not* cover user programs that use HQ
 * program services by normal system calls through the application
 * program interfaces provided as part of the Hyperic Plug-in Development
 * Kit or the Hyperic Client Development Kit - this is merely considered
 * normal use of the program, and does *not* fall under the heading of
 * "derived work".
 * 
 * Copyright (C) [2008-2010], Hyperic, Inc.
 * This file is part of HQ.
 * 
 * HQ is free software; you can redistribute it and/or modify
 * it under the terms version 2 of the GNU General Public License as
 * published by the Free Software Foundation. This program is distributed
 * in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A
 * PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
 * USA.
 * 
 */

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
     * An unexpected error occurred.
     */
    UNEXPECTED_ERROR("UnexpectedError",
                     "An unexpected error occurred"),
    /**
     * The current user does not have permission for this operation.
     */
    PERMISSION_DENIED("PermissionDenied",
                      "The current user does not have permission for this operation"),

    /**
     * The requested operation was denied.
     */
    OPERATION_DENIED("OperationDenied",
                     "The request operation was denied"),
    /**
     * The requested API is not implemented.
     */
    NOT_IMPLEMENTED("NotImplemented",
                    "The requested API is not available"),

    /**
     * Operation not supported.
     */
    NOT_SUPPORTED("NotSupported",
                  "The requested operation is not supported");

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
