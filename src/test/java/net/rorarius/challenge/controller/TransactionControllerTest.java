package net.rorarius.challenge.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.rorarius.challenge.Application;
import net.rorarius.challenge.database.DBRepository;
import net.rorarius.challenge.enums.StatusCode;
import net.rorarius.challenge.model.Transaction;
import net.rorarius.challenge.responses.StatusResponse;
import net.rorarius.challenge.responses.SumResponse;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebIntegrationTest({"server.port=7777", "management.port=0"})
public class TransactionControllerTest
{
    private final static String PUT_URL="http://localhost:7777/transactionservice/transaction/{id}";
    private final static String GET_BY_TYPE_URL="http://localhost:7777/transactionservice/types/{type}";
    private final static String GET_SUM_URL="http://localhost:7777/transactionservice/sum/{id}";

    private final static StatusResponse OK_RESPONSE = new StatusResponse(StatusCode.OK);
    private final static StatusResponse ERROR_RESPONSE = new StatusResponse(StatusCode.ERROR);
    private final static StatusResponse NOT_FOUND_RESPONSE = new StatusResponse(StatusCode.NOT_FOUND);

    @Autowired
    DBRepository repository;

    public static final ObjectMapper objMapper = new ObjectMapper();

    RestTemplate restTemplate = new TestRestTemplate();

    @Before
    public void setUp() {
        repository.clearRepostory();
    }

    private StatusResponse addTransaction(Transaction trx) throws JsonProcessingException, IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> httpEntity = new HttpEntity<String>(objMapper.writeValueAsString(trx), requestHeaders);
        ResponseEntity<String> responseEntity = restTemplate.exchange(PUT_URL, HttpMethod.PUT, httpEntity, String.class, trx.getTransactionId());

        if (responseEntity.getBody() != null) {
            return objMapper.readValue(responseEntity.getBody(), StatusResponse.class);
        } else {
            return null;
        }
    }

    private List<Long> getTransactionListByType(String type) throws JsonProcessingException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<List> transaction = restTemplate.getForEntity(GET_BY_TYPE_URL, List.class, type);

        return transaction.getBody();
    }


    private Double getTransactionSum(Long transactionId) throws JsonProcessingException, IOException {
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);

        ResponseEntity<String> responseEntity = restTemplate.getForEntity(GET_SUM_URL, String.class, transactionId);

        if (responseEntity.getBody() != null) {
            SumResponse sumResponse = objMapper.readValue(responseEntity.getBody(), SumResponse.class);
            return sumResponse.getSum();
        } else {
            return null;
        }
    }

    @Test
    public void testAddTransaction() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));
    }

    @Test
    public void testAddTransactionNonExistentParent() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", 2L));
        assertThat(response, equalTo(ERROR_RESPONSE));
    }

    @Test
    public void testAddTransactionTwice() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response2, equalTo(OK_RESPONSE));
    }

    @Test
    public void testAddTransactionAndTypeRequest() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        List<Long> transactionList = getTransactionListByType("cars");
        assertThat(transactionList, notNullValue());
        assertThat(transactionList.size(), equalTo(1));
        assertThat(transactionList.get(0), equalTo(10));
    }

    @Test
    public void testAddTransactionAndTypeRequestSingle() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        List<Long> transactionList = getTransactionListByType("cars");
        assertThat(transactionList, notNullValue());
        assertThat(transactionList.size(), equalTo(1));
        assertThat(transactionList.get(0), equalTo(10));
    }

    @Test
    public void testAddTransactionAndTypeRequestWithChilds() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(20L, 10D, "cars", 10L));
        assertThat(response2, equalTo(OK_RESPONSE));

        StatusResponse response3 = addTransaction(new Transaction(30L, 10D, "cars", 10L));
        assertThat(response3, equalTo(OK_RESPONSE));

        List<Long> transactionList = getTransactionListByType("cars");
        assertThat(transactionList, notNullValue());
        assertThat(transactionList.size(), equalTo(3));
        assertThat(transactionList.get(0), equalTo(10));
        assertThat(transactionList.get(1), equalTo(20));
        assertThat(transactionList.get(2), equalTo(30));
    }

    @Test
    public void testAddTransactionAndTypeRequestWithRecurringChilds() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(20L, 10D, "cars", 10L));
        assertThat(response2, equalTo(OK_RESPONSE));

        StatusResponse response3 = addTransaction(new Transaction(30L, 10D, "cars", 20L));
        assertThat(response3, equalTo(OK_RESPONSE));

        List<Long> transactionList = getTransactionListByType("cars");
        assertThat(transactionList, notNullValue());
        assertThat(transactionList.size(), equalTo(3));
        assertThat(transactionList.get(0), equalTo(10));
        assertThat(transactionList.get(1), equalTo(20));
        assertThat(transactionList.get(2), equalTo(30));
    }

    @Test
    public void testAddTransactionAndSumRequestSingle() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        Double transactionSum = getTransactionSum(10L);
        assertThat(transactionSum, notNullValue());
        assertThat(transactionSum, equalTo(10D));
    }

    @Test
    public void testAddTransactionAndSumRequestWithChilds() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(20L, 10D, "cars", 10L));
        assertThat(response2, equalTo(OK_RESPONSE));

        StatusResponse response3 = addTransaction(new Transaction(30L, 10D, "cars", 10L));
        assertThat(response3, equalTo(OK_RESPONSE));

        Double transactionSum = getTransactionSum(10L);
        assertThat(transactionSum, notNullValue());
        assertThat(transactionSum, equalTo(30D));
    }

    @Test
    public void testAddTransactionAndSumRequestWithRecurringChilds() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(20L, 10D, "cars", 10L));
        assertThat(response2, equalTo(OK_RESPONSE));

        StatusResponse response3 = addTransaction(new Transaction(30L, 10D, "cars", 20L));
        assertThat(response3, equalTo(OK_RESPONSE));

        Double transactionSum = getTransactionSum(10L);
        assertThat(transactionSum, notNullValue());
        assertThat(transactionSum, equalTo(30D));
    }

    @Test
    public void testAddTransactionAndSumRequestWithRecurringChildsSubItem() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(20L, 10D, "cars", 10L));
        assertThat(response2, equalTo(OK_RESPONSE));

        StatusResponse response3 = addTransaction(new Transaction(30L, 10D, "cars", 20L));
        assertThat(response3, equalTo(OK_RESPONSE));

        Double transactionSum = getTransactionSum(20L);
        assertThat(transactionSum, notNullValue());
        assertThat(transactionSum, equalTo(20D));
    }

    @Test
    public void testSumRequestWithNotExistingTransaction() throws Exception {
        Double transactionSum = getTransactionSum(20L);
        assertThat(transactionSum, equalTo(0D));
    }

    @Test
    public void testTypeRequestWithNotExistingTransaction() throws Exception {
        List<Long> transactionList = getTransactionListByType("cars");
        assertThat(transactionList, notNullValue());
        assertThat(transactionList.size(), equalTo(0));
    }

    @Test
    public void testAddInvalidTransaction() throws Exception {
        StatusResponse response = addTransaction(new Transaction(1L, 10D, null, null));
        assertThat(response, equalTo(ERROR_RESPONSE));
    }

    @Test
    public void testAddTransactionWithEqualParentAndTransactionId() throws Exception {
        StatusResponse response = addTransaction(new Transaction(10L, 10D, "cars", null));
        assertThat(response, equalTo(OK_RESPONSE));

        StatusResponse response2 = addTransaction(new Transaction(10L, 10D, "cars", 10L));
        assertThat(response2, equalTo(ERROR_RESPONSE));
    }
}
