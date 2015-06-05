package org.json;

/**
 * The JSONException is thrown by the JSON.org classes when things are amiss.
 *
 * @author JSON.org
 * @author Haringat
 * @version 2014-06-04
 */
public class JSONException extends RuntimeException {
    private static final long serialVersionUID = 0;
    private Throwable cause;

    /**
     * Constructs a JSONException with an explanatory message.
     *
     * @param message
     *            Detail about the reason for the exception.
     */
    public JSONException(String message) {
        super(message);
    }
    
    
    public JSONException(String message, Throwable cause){
    	super(message, cause);
    	this.cause = cause;
    }

    /**
     * Constructs a new JSONException with the specified cause.
     * @param cause The cause.
     */
    public JSONException(Throwable cause) {
        super(cause.getMessage());
        this.cause = cause;
    }

    /**
     * Returns the cause of this exception or null if the cause is nonexistent
     * or unknown.
     *
     * @return the cause of this exception or null if the cause is nonexistent
     *          or unknown.
     */
    @Override
    public Throwable getCause() {
        return this.cause;
    }
}
