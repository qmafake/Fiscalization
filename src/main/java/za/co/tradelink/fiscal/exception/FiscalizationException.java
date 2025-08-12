// FiscalizationException.java
package za.co.tradelink.fiscal.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class FiscalizationException extends RuntimeException {
    private final String receiptId;
    private final String errorCode;

    public FiscalizationException(String receiptId, String errorCode, String message) {
        super(message);
        this.receiptId = receiptId;
        this.errorCode = errorCode;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public String getErrorCode() {
        return errorCode;
    }
}