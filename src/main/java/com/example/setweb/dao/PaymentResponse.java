package com.example.setweb.dao;

/**
 * @author Yoruko
 */
public class PaymentResponse {
    Integer code;
    String status;

    public PaymentResponse(Integer code, String status) {
        this.code = code;
        this.status = status;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PaymentResponse{" +
                "code=" + code +
                ", status='" + status + '\'' +
                '}';
    }
}
