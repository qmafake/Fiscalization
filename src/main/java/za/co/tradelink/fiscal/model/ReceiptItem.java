package za.co.tradelink.fiscal.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
@Data
public class ReceiptItem implements Serializable {
    private String name;
    private int qty;
    private BigDecimal unitPrice;
    private BigDecimal vat;

    public ReceiptItem() {
    }

    public ReceiptItem(String name, int qty, BigDecimal unitPrice, BigDecimal vat) {
        this.name = name;
        this.qty = qty;
        this.unitPrice = unitPrice;
        this.vat = vat;

    }
}
