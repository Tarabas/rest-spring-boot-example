package net.rorarius.challenge.validator;

import net.rorarius.challenge.model.Transaction;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class TransactionValidator implements Validator
{
    @Override
    public boolean supports(Class<?> aClass)
    {
        return Transaction.class.equals(aClass);
    }

    /**
     * Validates a transaction
     * @param o
     * @param errors
     */
    @Override
    public void validate(Object o, Errors errors)
    {
        Transaction transaction = (Transaction) o;

        if (transaction.getTransactionId() == null) {
            errors.reject("transactionId", "transactionId is mandatory");
        }
        
        if (transaction.getAmount() == null) {
            errors.reject("amount", "amount is mandatory");
        }

        if (transaction.getType() == null) {
            errors.reject("type", "type is mandatory");
        }

        if (transaction.getParentId() != null &&
                transaction.getParentId() == transaction.getTransactionId()) {
            errors.reject("transactionId", "transactionId and parentId must differ");
        }
    }
}
