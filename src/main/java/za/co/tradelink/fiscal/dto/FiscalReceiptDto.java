package za.co.tradelink.fiscal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class FiscalReceiptDto {
    private String fiscalCode;
    private String receiptNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime issueDateTime;
    
    private String branchTIN;
    private String buyerTIN;
    private List<LineItemDto> lineItems;
    private BigDecimal totalAmount;
    private String currency;
    
    @Data
    public static class LineItemDto {
        private String description;
        private int quantity;
        private BigDecimal price;
        private BigDecimal vatRate;

        public LineItemDto(String description, int quantity, BigDecimal price, BigDecimal vatRate) {
            this.description = description;
            this.quantity = quantity;
            this.price = price;
            this.vatRate = vatRate;
        }
    }
}