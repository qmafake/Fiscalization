package za.co.tradelink.fiscal.service;

import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.exception.ReceiptNotFoundException;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptItem;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReceiptService {
    private final ReceiptRepository receiptRepository;
    
    @Transactional
    public Receipt createReceipt(PosReceiptDto posReceiptDto) {
        Receipt receipt = mapToReceipt(posReceiptDto);
        receipt.setStatus(ReceiptStatus.PENDING_FISCALIZATION);
        return receiptRepository.save(receipt);
    }
    
    public Receipt getReceiptById(Long id) {
        return receiptRepository.findById(id)
                .orElseThrow(() -> new ReceiptNotFoundException("Receipt not found with id: " + id));
    }
    
    public List<Receipt> getAllReceipts(ReceiptStatus status, LocalDateTime fromDate, LocalDateTime toDate) {
        return receiptRepository.findByStatusAndDateRange(status, fromDate, toDate);
    }
    
    private Receipt mapToReceipt(PosReceiptDto posReceiptDto) {
        Receipt receipt = new Receipt();
        receipt.setReceiptNumber(posReceiptDto.getReceiptNumber());
        receipt.setTimestamp(posReceiptDto.getTimestamp());
        receipt.setCashierId(posReceiptDto.getCashierId());
        receipt.setBranchCode(posReceiptDto.getBranchCode());
        
        if (posReceiptDto.getBuyer() != null) {
            receipt.setBuyerTin(posReceiptDto.getBuyer().getTin());
            receipt.setBuyerName(posReceiptDto.getBuyer().getName());
        }

        receipt.setItems(posReceiptDto.getItems().stream()
                .map(item -> new ReceiptItem(
                        item.getName(),
                        item.getQty(),
                        item.getUnitPrice(),
                        item.getVat()))
                .toList());

        receipt.setTotal(posReceiptDto.getTotal());
        receipt.setCurrency(posReceiptDto.getCurrency());
        return receipt;
    }
}