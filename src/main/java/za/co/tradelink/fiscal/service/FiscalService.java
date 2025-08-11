// FiscalService.java
package za.co.tradelink.fiscal.service;

import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.exception.ReceiptNotFoundException;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpServerErrorException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FiscalService {
    private final ReceiptRepository receiptRepository;
    
    @Transactional
    public Receipt fiscalizeReceipt(Long receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ReceiptNotFoundException("Receipt not found with id: " + receiptId));

        //TODO: See what to do here to determine successfull fiscalization, also check firefox
        // Simulate fiscalization - in a real system this would call an external service
        boolean fiscalizationSuccess = simulateFiscalization();
        
        if (fiscalizationSuccess) {
            receipt.setStatus(ReceiptStatus.FISCALIZED);
            receipt.setFiscalCode(UUID.randomUUID().toString());
            receipt.setFiscalizedAt(LocalDateTime.now());
        } else {
            receipt.setStatus(ReceiptStatus.FISCALIZATION_FAILED);
            throw new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR, "Fiscalization failed");
        }
        
        return receiptRepository.save(receipt);
    }
    
    public FiscalReceiptDto mapToFiscalReceiptDto(Receipt receipt) {

        FiscalReceiptDto dto = new FiscalReceiptDto();
        dto.setFiscalCode(receipt.getFiscalCode());
        dto.setReceiptNumber(receipt.getReceiptNumber());
        dto.setIssueDateTime(receipt.getTimestamp());
        dto.setBranchTIN(receipt.getBranchCode());
        dto.setBuyerTIN(receipt.getBuyerTin());

        dto.setLineItems(receipt.getItems().stream()
                .map(item -> new FiscalReceiptDto.LineItemDto(
                        item.getName(),
                        item.getQty(),
                        item.getUnitPrice(),
                        item.getVat()))
                .toList());
        
        dto.setTotalAmount(receipt.getTotal());
        dto.setCurrency(receipt.getCurrency());
        return dto;
    }
    
    private boolean simulateFiscalization() {
        // Simulate random success/failure (80% success rate for demo purposes)
        return Math.random() > 0.2;
    }
}