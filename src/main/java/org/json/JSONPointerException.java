package org.json;

/*
Public Domain.
*/

/**
 * The JSONPointerException is thrown by {@link JSONPointer} if an error occurs
 * during evaluating a pointer.
 * 
 * @author JSON.org
 * @version 2016-05-13
 */
public class JSONPointerException extends JSONException {
    private static final long serialVersionUID = 8872944667561856751L;

    public JSONPointerException(String message) {
        super(message);
    }

    public JSONPointerException(String message, Throwable cause) {
        super(message, cause);
    }

}
