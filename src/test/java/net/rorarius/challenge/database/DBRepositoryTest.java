package net.rorarius.challenge.database;

import net.rorarius.challenge.Application;
import net.rorarius.challenge.database.DBRepository;
import net.rorarius.challenge.exceptions.TransactionIdEmptyException;
import net.rorarius.challenge.exceptions.TransactionInvalidException;
import net.rorarius.challenge.model.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class DBRepositoryTest
{
    @Autowired
    DBRepository repository;

    @Before
    public void startup() {
        repository.clearRepostory();
    }

    @Test
    public void testAddTransactionAndExists() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        assertThat(success, equalTo(true));
        assertThat(repository.transactionExists(trx.getTransactionId()), equalTo(true));
    }

    @Test
    public void testAddTransactionAndGetByType() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        assertThat(success, equalTo(true));
        assertThat(repository.getTransactionListByType("test"), contains(trx));
    }

    @Test
    public void testAddTransactionAndGetByTypeMultiple() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", null);
        boolean success2 = repository.addTransaction(trx2);

        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.getTransactionListByType("test"), contains(trx, trx2));
    }

    @Test
    public void testGetSumZeroEntries() throws TransactionIdEmptyException, TransactionInvalidException {
        assertThat(repository.getTransactionListByType("test"), nullValue());
    }

    @Test
    public void testAddTransactionGetSumOneEntry() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        assertThat(success, equalTo(true));
        assertThat(repository.getTransactionSumRecursive(1L), equalTo(10D));
    }

    @Test
    public void testAddTransactionGetSumMultipleEntriesNoChild() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", null);
        boolean success2 = repository.addTransaction(trx2);

        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.getTransactionSumRecursive(1L), equalTo(10D));
    }

    @Test
    public void testAddTransactionGetSumMultipleEntriesOneChild() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", 1L);
        boolean success2 = repository.addTransaction(trx2);

        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.getTransactionSumRecursive(1L), equalTo(20D));
    }

    @Test
    public void testAddTransactionGetSumMultipleEntriesMultipleChilds() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", 1L);
        boolean success2 = repository.addTransaction(trx2);

        Transaction trx3 = new Transaction(3L, 10D, "test", 1L);
        boolean success3 = repository.addTransaction(trx3);

        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(success3, equalTo(true));
        assertThat(repository.getTransactionSumRecursive(1L), equalTo(30D));
    }

    @Test
    public void testAddTransactionGetSumMultipleEntriesReursiveChilds() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", 1L);
        boolean success2 = repository.addTransaction(trx2);

        Transaction trx3 = new Transaction(3L, 10D, "test", 2L);
        boolean success3 = repository.addTransaction(trx3);

        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(success3, equalTo(true));
        assertThat(repository.getTransactionSumRecursive(1L), equalTo(30D));
    }

    @Test
    public void testTransactionExists() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        // All these asserts can also be split up into own tests but I like it better to test one case in whole in some cases
        assertThat(success, equalTo(true));
        assertThat(repository.transactionExists(trx.getTransactionId()), equalTo(true));
    }

    @Test
    public void testTransactionExistsMultipleEntries() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", null);
        boolean success2 = repository.addTransaction(trx);

        // All these asserts can also be split up into own tests but I like it better to test one case in whole in some cases
        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.transactionExists(trx.getTransactionId()), equalTo(true));
    }

    @Test
    public void testTransactionNotExists() throws TransactionIdEmptyException, TransactionInvalidException {
        assertThat(repository.transactionExists(1L), equalTo(false));
    }

    @Test(expected=TransactionInvalidException.class)
    public void testAddEmptyTransaction() throws TransactionInvalidException {
        Transaction trx = new Transaction();
        repository.addTransaction(trx);
    }

    @Test
    public void testAddSameTransactionTwice() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(1L, 10D, "test", null);
        boolean success2 = repository.addTransaction(trx2);

        // All these asserts can also be split up into own tests but I like it better to test one case in whole in some cases
        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.transactionExists(trx.getTransactionId()), equalTo(true));
        assertThat(repository.getTransactionListByType("test").size(), equalTo(1));
        assertThat(repository.getTransactionListByType("test"), contains(trx2));
        assertThat(repository.getTransactionSumRecursive(trx.getTransactionId()), equalTo(10D));
        assertThat(repository.getChildTransactions(trx.getTransactionId()), nullValue());
    }

    @Test
    public void testAddTransactionAndChildSameType() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10D, "test", 1L);
        boolean success2 = repository.addTransaction(trx2);

        // All these asserts can also be split up into own tests but I like it better to test one case in whole in some cases
        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.transactionExists(trx.getTransactionId()), equalTo(true));
        assertThat(repository.getTransactionListByType("test").size(), equalTo(2));
        assertThat(repository.getTransactionListByType("test"), contains(trx, trx2));
        assertThat(repository.getTransactionSumRecursive(trx.getTransactionId()), equalTo(20D));
        assertThat(repository.getChildTransactions(trx.getTransactionId()).size(), equalTo(1));
        assertThat(repository.getChildTransactions(trx.getTransactionId()).get(0), equalTo(trx2));
    }

    @Test
    public void testAddTransactionAndParentDifferentType() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        boolean success = repository.addTransaction(trx);

        Transaction trx2 = new Transaction(2L, 10.3D, "auto", 1L);
        boolean success2 = repository.addTransaction(trx2);

        // All these asserts can also be split up into own tests but I like it better to test one case in whole in some cases
        assertThat(success, equalTo(true));
        assertThat(success2, equalTo(true));
        assertThat(repository.transactionExists(trx.getTransactionId()), equalTo(true));
        assertThat(repository.getTransactionListByType("test").size(), equalTo(1));
        assertThat(repository.getTransactionListByType("test"), contains(trx));
        assertThat(repository.getTransactionListByType("auto").size(), equalTo(1));
        assertThat(repository.getTransactionListByType("auto"), contains(trx2));
        assertThat(repository.getChildTransactions(trx.getTransactionId()).size(), equalTo(1));
        assertThat(repository.getChildTransactions(trx.getTransactionId()).get(0), equalTo(trx2));
        assertThat(repository.getTransactionSumRecursive(trx.getTransactionId()), equalTo(20.3D));
    }

    @Test(expected=TransactionInvalidException.class)
    public void testAddTransactionWithInvalidParent() throws TransactionIdEmptyException, TransactionInvalidException {
        Transaction trx = new Transaction(1L, 10D, "test", 2L);
        repository.addTransaction(trx);
    }
}
