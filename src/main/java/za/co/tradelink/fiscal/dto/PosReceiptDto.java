// PosReceiptDto.java
package za.co.tradelink.fiscal.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class PosReceiptDto {

    @NotEmpty(message = "receiptNumber is required")
    private String receiptNumber;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'")
    private LocalDateTime timestamp;

    @NotEmpty(message = "CashierI Id is required")
    private String cashierId;

    private String branchCode;

    private BuyerDto buyer;

    @NotNull(message = "Receipt items cannot be null")
    private List<ItemDto> items;

    @NotNull(message = "Total is required")
    private BigDecimal total;

    //TODO: revisit default or remove comment
    private String currency = "ZAR";

    @Data
    @NotNull
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