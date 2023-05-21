package de.hsesslingen.scpprojekt.scp.Exceptions;

/**
 * Exception for when a DB entry already exists
 *
 * @author Jason Patrick Duffy
 */
public class AlreadyExistsException extends Exception{
    public AlreadyExistsException(String error) { super(error); }
}
