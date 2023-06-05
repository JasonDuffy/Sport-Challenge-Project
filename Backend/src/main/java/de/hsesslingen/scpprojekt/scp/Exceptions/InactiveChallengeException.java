package de.hsesslingen.scpprojekt.scp.Exceptions;
/**
 * Exception for when to add an activity to an inactive challenge
 *
 * @author Tom Nguyen Dinh
 */
public class InactiveChallengeException extends Exception{
    public InactiveChallengeException(String error) { super(error); }
}
