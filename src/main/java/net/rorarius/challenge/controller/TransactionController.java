package net.rorarius.challenge.controller;

import java.util.ArrayList;
import java.util.List;

import net.rorarius.challenge.database.DBRepository;
import net.rorarius.challenge.enums.StatusCode;
import net.rorarius.challenge.model.Transaction;
import net.rorarius.challenge.responses.StatusResponse;
import net.rorarius.challenge.responses.SumResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class TransactionController
{
    @Autowired
    DBRepository repository;

    /**
     * Adds a new Transaction via the PUT Command. Returns a Body, although PUT
     * normally does not need a return body.
     * @param transactionId the transactionId to be added
     * @param transaction the transaction from the Request-Body
     * @param response The HttpServletResponse for custom return codes
     * @return StatusResponse OK or ERROR, Returns HttpCode 200, 201 or 400
     */
    @RequestMapping(value="/transactionservice/transaction/{transactionId}",
                    method= RequestMethod.PUT,
                    produces= MediaType.APPLICATION_JSON_VALUE
    )
    public StatusResponse putTransaction(@PathVariable("transactionId") Long transactionId,
                               @RequestBody Transaction transaction,
                                 HttpServletResponse response) {

        try {
            transaction.setTransactionId(transactionId);

            if (!repository.transactionIsValid(transaction)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new StatusResponse(StatusCode.ERROR);
            }

            if (transaction.getParentId() != null &&
                    !repository.transactionExists(transaction.getParentId())) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return new StatusResponse(StatusCode.ERROR);
            }

            // Depending on if the Transaction is updated or created we return a
            // different Http-Code (200-OK for update, 201-Created for create)
            if (repository.transactionExists(transactionId)) {
                response.setStatus(HttpServletResponse.SC_OK);
            } else {
                response.setStatus(HttpServletResponse.SC_CREATED);
            }

            repository.addTransaction(transaction);

            return new StatusResponse(StatusCode.OK);
        } catch (Exception e) {
            handleException(e);
            return new StatusResponse(StatusCode.ERROR);
        }
    }

    /**
     * Returns a List of all transactionIDs for a certain type of Transaction
     * @param type the type of transaction to query
     * @param response The HttpServletResponse for custom return codes
     * @returnList transactionIds as Long, Returns HttpCodes 200 or 404
     */
    @RequestMapping(value="/transactionservice/types/{type}",
            method= RequestMethod.GET,
            produces= MediaType.APPLICATION_JSON_VALUE
    )
    public List<Long> getTransactionIdsByType(@PathVariable("type") String type,
                               HttpServletResponse response) {

        try {
            if (repository.transactionListContainsType(type))
            {
                List<Transaction> transactionListForType = repository.getTransactionListByType(type);
                List<Long> transactionIdList = new ArrayList<>();

                for (Transaction transaction : transactionListForType) {
                    transactionIdList.add(transaction.getTransactionId());
                }

                response.setStatus(HttpServletResponse.SC_OK);
                return transactionIdList;
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleException(e);
        }

        return new ArrayList<>();
    }

    /**
     * Returns a Sum of all Transactions and Sub-Transactions of
     * @param transactionId
     * @param response
     * @return Sum of Amounts of all Transactions and Sub-Transactions.
     *         Returns HttpCodes 200 or 404 and 0 if no Transaction was found
     */
    @RequestMapping(value="/transactionservice/sum/{transaction_id}",
            method= RequestMethod.GET,
            produces= MediaType.APPLICATION_JSON_VALUE
    )
    public SumResponse getTransactionSumById(@PathVariable("transaction_id") Long transactionId,
                                              HttpServletResponse response) {

        try {
            if (repository.transactionExists(transactionId)) {
                response.setStatus(HttpServletResponse.SC_OK);
                return new SumResponse(repository.getTransactionSumRecursive(transactionId));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            handleException(e);
        }

        return new SumResponse(0D);
    }

    /**
     * Handles Exceptions of all REST-Services by returning an Internal Server Error Http-Code
     * @param ex
     * @return
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleException(Exception ex) {
        return ex.getMessage();
    }
}
