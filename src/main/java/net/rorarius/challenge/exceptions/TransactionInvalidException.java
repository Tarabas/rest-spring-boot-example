package net.rorarius.challenge.exceptions;

public class TransactionInvalidException extends Exception
{

    public TransactionInvalidException()
    {
    }

    public TransactionInvalidException(String message)
    {
        super(message);
    }
}
