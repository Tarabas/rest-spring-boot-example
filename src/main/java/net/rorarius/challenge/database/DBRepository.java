package net.rorarius.challenge.database;

import net.rorarius.challenge.exceptions.TransactionIdEmptyException;
import net.rorarius.challenge.exceptions.TransactionInvalidException;
import net.rorarius.challenge.model.Transaction;
import net.rorarius.challenge.validator.TransactionValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * In-Memory Data-Repository.
 * Please note that for simplicity sake I did NOT implement Transactional safety here.
 * For a real world use case, this would obviously be mandatory!
 */
@Repository
public class DBRepository
{
    @Autowired
    TransactionValidator validator;

    private static Hashtable<Long, Transaction> transactionListById = new Hashtable<>();
    private static Hashtable<String, List<Transaction>> transactionListByType = new Hashtable<>();
    private static Hashtable<Long, List<Transaction>> transactionWithChilds = new Hashtable<>();

    /**
     * Clears all "Tables" of the Repository
     */
    public void clearRepostory() {
        transactionListById = new Hashtable<>();
        transactionListByType = new Hashtable<>();
        transactionWithChilds = new Hashtable<>();
    }

    /**
     * Validates a Transaction trough the TransactionValidator and returns a List of Errors in
     * a BeanPropertyBindingResult
     * @param transaction the transaction to be validated
     * @return BeanPropertyBindingResult
     */
    public BeanPropertyBindingResult validateTransaction(Transaction transaction) {
        TransactionValidator transactionValidator = new TransactionValidator();
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(transaction, "transaction");
        ValidationUtils.invokeValidator(transactionValidator, transaction, result);

        return result;
    }

    /**
     * Returns a simple boolean if a Transaction is valid or not
     * @param transaction
     * @return true/false
     */
    public boolean transactionIsValid(Transaction transaction) {
        BeanPropertyBindingResult result = validateTransaction(transaction);

        if (result.hasErrors()) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Checks if a Transaction exists in the Repository
     * @param transactionId
     * @return true/false
     */
    public boolean transactionExists(Long transactionId) {
        return transactionListById.containsKey(transactionId);
    }

    /**
     * Checks if a Transaction is valid or not and throws an according Exception
     * @param transaction
     * @throws TransactionInvalidException
     */
    private void checkTransaction(Transaction transaction) throws TransactionInvalidException {
        if (!transactionIsValid(transaction)) {
            throw new TransactionInvalidException();
        }
    }

    /**
     * Add a Transaction to the memory
     * Assuming, that a valid transaction will always produce a correct result for simplicities sake.
     * This method is NOT Transaction-Safe!!
     *
     * @param transaction
     * @return
     * @throws TransactionInvalidException
     */
    public boolean addTransaction(Transaction transaction) throws TransactionInvalidException {
        // Check transaction mandatory fields
        checkTransaction(transaction);

        // if we have a parent-id, this parent must already be present of course.
        if (transaction.getParentId() != null && !transactionExists(transaction.getParentId())) {
            throw new TransactionInvalidException();
        }

        transactionListById.put(transaction.getTransactionId().longValue(), transaction);
        boolean transactionByTypeOk = addTransactionByType(transaction);

        if (transaction.getParentId() != null)
        {
            boolean transactionChild = addChildTransaction(transaction);
            return transactionByTypeOk && transactionChild;
        }

        return transactionByTypeOk;
    }

    /**
     * Returns true/false depending on if a transaction-type already exists in the repository
     * @param type
     * @return true/false
     */
    public boolean transactionListContainsType(String type) {
        return transactionListByType.containsKey(type);
    }

    /**
     * Returns the List of transactions for a specific transaction type
     * @param type
     * @return List of Transactions
     */
    public List<Transaction> getTransactionListByType(String type) {
        return transactionListByType.get(type);
    }

    /**
     * Gets a List of Child-Transactions for a transaction-ID
     * @param transactionId
     * @return
     */
    public List<Transaction> getChildTransactions(Long transactionId) {
        return transactionWithChilds.get(transactionId);
    }

    /**
     * Calculates the Sum of all amounts of a transaction and all its child transactions
     * @param transactionId
     * @return Double Sum of Amounts
     * @throws TransactionIdEmptyException
     */
    public Double getTransactionSumRecursive(Long transactionId) throws TransactionIdEmptyException {
        Double sum = 0D;

        if (transactionId != null)
        {
            Transaction transaction = transactionListById.get(transactionId);
            List<Transaction> childTransactions = transactionWithChilds.get(transactionId);

            sum += transaction.getAmount();

            if (childTransactions != null)
            {
                for (Transaction childTransaction : childTransactions)
                {
                    sum += getTransactionSumRecursive(childTransaction.getTransactionId());
                }
            }
        } else {
            throw new TransactionIdEmptyException();
        }

        return sum;
    }

    /**
     * Adds a Transaction as a child in the Datastore.
     * Returns true if a child was added, false if not.
     * @param transaction
     * @return
     */
    private boolean addChildTransaction(Transaction transaction) {
        if (transaction.getParentId() != null) {
            if (transactionWithChilds.containsKey(transaction.getParentId())) {
                List<Transaction> transactionList = getChildTransactions(transaction.getParentId());

                // Only add Transaction to Childs once
                if (!transactionList.contains(transaction)) {
                    transactionList.add(transaction);
                } else {
                    return false;
                }
            } else {
                List<Transaction> transactionChilds = new ArrayList<>();
                transactionChilds.add(transaction);
                transactionWithChilds.put(transaction.getParentId(), transactionChilds);
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Adds a transaction to the transactionListByType HashMap
     * @param transaction
     */
    private boolean addTransactionByType(Transaction transaction) {
        List<Transaction> transactionList;

        if (transactionListByType.containsKey(transaction.getType()))
        {
            transactionList = transactionListByType.get(transaction.getType());
        }
        else
        {
            transactionList = new ArrayList<>();
        }

        if (transactionList.contains(transaction))
        {
            transactionList.set(transactionList.indexOf(transaction), transaction);
        } else {
            transactionList.add(transaction);
        }
        transactionListByType.put(transaction.getType(), transactionList);

        return true;
    }
}
