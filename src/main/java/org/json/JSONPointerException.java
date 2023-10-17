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

    /**
     * Constructs a new JSONPointerException with the specified detail message.
     *
     * @param message the detail message.
     */
    public JSONPointerException(String message) {
        super(message);
    }

    /**
     * Constructs a new JSONPointerException with the specified detail message and cause.
     *
     * @param message the detail message (which is saved for later retrieval by the getMessage() method).
     * @param cause the cause (which is saved for later retrieval by the getCause() method).
     */
    public JSONPointerException(String message, Throwable cause) {
        super(message, cause);
    }

}
