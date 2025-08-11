// PosReceiptDto.java
package za.co.tradelink.fiscal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PosReceiptDto {
    private String receiptNumber;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;
    
    private String cashierId;
    private String branchCode;
    
    private BuyerDto buyer;
    private List<ItemDto> items;
    
    private BigDecimal total;
    private String currency;
    
    @Data
    public static class BuyerDto {
        private String tin;
        private String name;
    }
    
    @Data
    public static class ItemDto {
        private String name;
        private int qty;
        private BigDecimal unitPrice;
        private BigDecimal vat;
    }
}