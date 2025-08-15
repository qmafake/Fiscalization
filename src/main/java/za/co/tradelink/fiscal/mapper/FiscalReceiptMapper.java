package za.co.tradelink.fiscal.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.model.Receipt;

@Service
public class FiscalReceiptMapper {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public FiscalReceiptDto mapToFiscalReceiptDto(Receipt receipt) {

        logger.info("Receipt to transform to fiscal: {}", receipt);

        FiscalReceiptDto fiscalReceiptDto = new FiscalReceiptDto();
        fiscalReceiptDto.setFiscalCode(receipt.getFiscalCode());
        fiscalReceiptDto.setReceiptNumber(receipt.getReceiptNumber());
        fiscalReceiptDto.setIssueDateTime(receipt.getTimestamp());
        fiscalReceiptDto.setBranchTIN(receipt.getBranchCode());
        fiscalReceiptDto.setBuyerTIN(receipt.getBuyerTin());

        fiscalReceiptDto.setLineItems(receipt.getItems().stream()
                .map(item -> new FiscalReceiptDto.LineItemDto(
                        item.getName(),
                        item.getQty(),
                        item.getUnitPrice(),
                        item.getVat()))
                .toList());

        fiscalReceiptDto.setTotalAmount(receipt.getTotal());
        fiscalReceiptDto.setCurrency(receipt.getCurrency());

        logger.info("Transformed fiscal receipt: {}", fiscalReceiptDto);

        return fiscalReceiptDto;
    }
}
