package net.rorarius.challenge.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Transaction
{
    private static final long serialVersionUID = 1L;

    @JsonProperty("transaction_id")
    private Long transactionId;

    @JsonProperty("amount")
    private Double amount;

    @JsonProperty("type")
    private String type;

    @JsonProperty("parent_id")
    private Long parentId;

    public Transaction()
    {
    }

    public Transaction(Long transactionId, Double amount, String type, Long parentId)
    {
        this.transactionId = transactionId;
        this.amount = amount;
        this.type = type;
        this.parentId = parentId;
    }

    public Long getTransactionId()
    {
        return transactionId;
    }

    public void setTransactionId(Long transactionId)
    {
        this.transactionId = transactionId;
    }

    public Double getAmount()
    {
        return amount;
    }

    public void setAmount(Double amount)
    {
        this.amount = amount;
    }

    public String getType()
    {
        return type;
    }

    public void setType(String type)
    {
        this.type = type;
    }

    public Long getParentId()
    {
        return parentId;
    }

    public void setParentId(Long parentId)
    {
        this.parentId = parentId;
    }

    @Override
    public int hashCode()
    {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (((Transaction)obj).getTransactionId() == this.getTransactionId()) {
            return true;
        }

        return false;
    }
}