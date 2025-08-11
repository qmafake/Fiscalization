package za.co.tradelink.fiscal.exception;

public class ReceiptNotFoundException extends RuntimeException {
    public ReceiptNotFoundException(String message) {
        super(message);
    }
}