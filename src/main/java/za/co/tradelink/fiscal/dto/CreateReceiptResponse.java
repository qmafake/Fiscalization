package za.co.tradelink.fiscal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import za.co.tradelink.fiscal.model.ReceiptStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CreateReceiptResponse {
    private Long id;
    private String receiptNumber;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    private String cashierId;
    private String branchCode;
    private String buyerTin;
    private String buyerName;
    private List<ReceiptItemResponse> items;
    private BigDecimal total;
    private String currency;
    private ReceiptStatus status;
    private String fiscalCode;
    
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime fiscalizedAt;

    @Data
    public static class ReceiptItemResponse {
        private String name;
        private int quantity;
        private BigDecimal unitPrice;
        private BigDecimal vat;
    }
}