// FiscalService.java
package za.co.tradelink.fiscal.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.exception.FiscalizationException;
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
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Transactional
    public Receipt fiscalizeReceipt(Long receiptId) {

        Receipt receipt = receiptRepository.findById(receiptId)
                .orElseThrow(() -> new ReceiptNotFoundException("Receipt not found with id: " + receiptId));

        // Simulate fiscalization - in a real system this would call an external service
        boolean fiscalizationSuccess = simulateFiscalization();

        if (fiscalizationSuccess) {
            receipt.setStatus(ReceiptStatus.FISCALIZED);
            receipt.setFiscalCode(UUID.randomUUID().toString());
            receipt.setFiscalizedAt(LocalDateTime.now());

            logger.info("Fiscalized receipt number {} ", receipt.getReceiptNumber());

        } else {

            logger.error("Failed fiscalization for receipt number {} ", receipt.getReceiptNumber());

            receipt.setStatus(ReceiptStatus.FISCALIZATION_FAILED);

            throw new FiscalizationException(
                    receipt.getId().toString(),
                    "FISCAL_500",
                    "Fiscalization service internal error occurred"
            );

        }

        return receiptRepository.save(receipt);
    }

    public boolean simulateFiscalization() {
        // Simulate random success/failure (80% success rate for demo purposes)
        return Math.random() > 0.2; //TODO: move success rate to properties file for easy demo adjusting
    }
}