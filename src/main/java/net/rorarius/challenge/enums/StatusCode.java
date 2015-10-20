package net.rorarius.challenge.enums;

/**
 * Created by mrorariu on 16.10.15.
 */
public enum StatusCode
{
    OK("OK"), ERROR("ERROR"), NOT_FOUND("NOT_FOUND");

    private String code;

    StatusCode(String code) {
        this.code = code;
    }

    public String getStatusCode() {
        return code;
    }
}
