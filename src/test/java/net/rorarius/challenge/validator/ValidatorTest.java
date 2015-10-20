package net.rorarius.challenge.validator;

import net.rorarius.challenge.Application;
import net.rorarius.challenge.model.Transaction;
import net.rorarius.challenge.validator.TransactionValidator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.ValidationUtils;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
public class ValidatorTest
{
    @Autowired
    TransactionValidator validator;

    @Test
    public void testValidTransaction() {
        Transaction trx = new Transaction(1L, 10D, "test", null);
        BeanPropertyBindingResult result = new BeanPropertyBindingResult(trx, "transaction");
        ValidationUtils.invokeValidator(validator, trx, result);

        assertThat(result.hasErrors(), equalTo(false));
    }

    @Test
    public void testTransactionAmountInvalid() {
        Transaction trx = new Transaction(1L, null, "test", null);

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(trx, "transaction");
        ValidationUtils.invokeValidator(validator, trx, result);

        assertThat(result.hasErrors(), equalTo(true));
        assertThat(result.getErrorCount(), equalTo(1));
    }

    @Test
    public void testTransactionTypeInvalid() {
        Transaction trx = new Transaction(1L, 10D, null, null);

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(trx, "transaction");
        ValidationUtils.invokeValidator(validator, trx, result);

        assertThat(result.hasErrors(), equalTo(true));
        assertThat(result.getErrorCount(), equalTo(1));
    }

    @Test
    public void testTransactionIdInvalid() {
        Transaction trx = new Transaction(null, 10D, "test", null);

        BeanPropertyBindingResult result = new BeanPropertyBindingResult(trx, "transaction");
        ValidationUtils.invokeValidator(validator, trx, result);

        assertThat(result.hasErrors(), equalTo(true));
        assertThat(result.getErrorCount(), equalTo(1));
    }
}
