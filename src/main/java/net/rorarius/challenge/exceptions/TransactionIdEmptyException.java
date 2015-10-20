package net.rorarius.challenge.exceptions;

public class TransactionIdEmptyException extends Exception
{

    public TransactionIdEmptyException()
    {
    }

    public TransactionIdEmptyException(String message)
    {
        super(message);
    }
}
