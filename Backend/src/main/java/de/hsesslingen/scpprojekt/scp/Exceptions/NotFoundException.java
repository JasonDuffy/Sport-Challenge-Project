package de.hsesslingen.scpprojekt.scp.Exceptions;

/**
 * Exception for when an entry can not be found in the entity
 *
 * @author Jason Patrick Duffy
 */
public class NotFoundException extends Exception{
    public NotFoundException(String error){
        super(error);
    }
}
