package za.co.tradelink.fiscal.mapper;

import org.springframework.stereotype.Service;
import za.co.tradelink.fiscal.dto.CreateReceiptResponse;
import za.co.tradelink.fiscal.model.Receipt;

@Service
public class ReceiptMapper {

    public CreateReceiptResponse mapToCreateReceiptResponse(Receipt receipt) {
        CreateReceiptResponse response = new CreateReceiptResponse();
        response.setId(receipt.getId());
        response.setReceiptNumber(receipt.getReceiptNumber());
        response.setTimestamp(receipt.getTimestamp());
        response.setCashierId(receipt.getCashierId());
        response.setBranchCode(receipt.getBranchCode());
        response.setBuyerTin(receipt.getBuyerTin());
        response.setBuyerName(receipt.getBuyerName());

        response.setItems(receipt.getItems().stream()
                .map(item -> {
                    CreateReceiptResponse.ReceiptItemResponse itemResponse = new CreateReceiptResponse.ReceiptItemResponse();
                    itemResponse.setName(item.getName());
                    itemResponse.setQuantity(item.getQty());
                    itemResponse.setUnitPrice(item.getUnitPrice());
                    itemResponse.setVat(item.getVat());
                    return itemResponse;
                })
                .toList());

        response.setTotal(receipt.getTotal());
        response.setCurrency(receipt.getCurrency());
        response.setStatus(receipt.getStatus());
        response.setFiscalCode(receipt.getFiscalCode());
        response.setFiscalizedAt(receipt.getFiscalizedAt());

        return response;
    }

}
