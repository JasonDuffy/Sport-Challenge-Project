package de.hsesslingen.scpprojekt.scp.Exceptions;

/**
 * Exception for when the activities are not part of the same challenge
 *
 * @author Jason Patrick Duffy
 */
public class InvalidActivitiesException extends Exception{
    public InvalidActivitiesException(String error){
        super(error);
    }
}
