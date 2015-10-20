package net.rorarius.challenge.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import net.rorarius.challenge.enums.StatusCode;

public class StatusResponse
{
    @JsonProperty("status")
    private StatusCode status;

    public StatusResponse()
    {
    }

    public StatusResponse(StatusCode status)
    {
        this.status = status;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o)
        {
            return true;
        }
        if (o == null || getClass() != o.getClass())
        {
            return false;
        }

        StatusResponse that = (StatusResponse) o;

        if (status.getStatusCode() != that.status.getStatusCode())
        {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode()
    {
        return status != null ? status.hashCode() : 0;
    }
}
