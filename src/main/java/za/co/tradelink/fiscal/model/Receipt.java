// Receipt.java
package za.co.tradelink.fiscal.model;

import jakarta.persistence.*;
import lombok.Data;


import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
public class Receipt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String receiptNumber;
    private LocalDateTime timestamp;
    private String cashierId;
    private String branchCode;
    private String buyerTin;
    private String buyerName;
    
    @ElementCollection
    @Embedded
    private List<ReceiptItem> items;
    
    private BigDecimal total;
    private String currency;
    
    @Enumerated(EnumType.STRING)
    private ReceiptStatus status;
    
    private String fiscalCode;
    private LocalDateTime fiscalizedAt;

}

