package za.co.tradelink.fiscal.mapper;

import org.springframework.stereotype.Service;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.model.Receipt;

@Service
public class FiscalReceiptMapper {

    public FiscalReceiptDto mapToFiscalReceiptDto(Receipt receipt) {

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

        return fiscalReceiptDto;
    }
}
