package net.rorarius.challenge.responses;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SumResponse
{
    @JsonProperty
    Double sum;

    public SumResponse()
    {
    }

    public SumResponse(Double sum)
    {
        this.sum = sum;
    }

    public Double getSum()
    {
        return sum;
    }

    public void setSum(Double sum)
    {
        this.sum = sum;
    }
}
