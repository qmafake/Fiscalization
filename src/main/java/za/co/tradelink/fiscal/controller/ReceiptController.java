package za.co.tradelink.fiscal.controller;

import za.co.tradelink.fiscal.dto.FiscalReceiptDto;
import za.co.tradelink.fiscal.dto.PosReceiptDto;
import za.co.tradelink.fiscal.model.Receipt;
import za.co.tradelink.fiscal.model.ReceiptStatus;
import za.co.tradelink.fiscal.service.FiscalService;
import za.co.tradelink.fiscal.service.ReceiptService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/receipts")
@RequiredArgsConstructor
public class ReceiptController {
    private final ReceiptService receiptService;
    private final FiscalService fiscalService;
    
    @PostMapping
    public ResponseEntity<Receipt> createReceipt(@RequestBody PosReceiptDto posReceiptDto) {
        Receipt receipt = receiptService.createReceipt(posReceiptDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(receipt);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Receipt> getReceiptById(@PathVariable Long id) {
        Receipt receipt = receiptService.getReceiptById(id);
        return ResponseEntity.ok(receipt);
    }
    
    @GetMapping
    public ResponseEntity<List<Receipt>> getAllReceipts(
            @RequestParam(required = false) ReceiptStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime toDate) {
        
        List<Receipt> receipts = receiptService.getAllReceipts(status, fromDate, toDate);
        return ResponseEntity.ok(receipts);
    }
    
    @PostMapping("/{id}/fiscalize")
    public ResponseEntity<FiscalReceiptDto> fiscalizeReceipt(@PathVariable Long id) {

        Receipt receipt = fiscalService.fiscalizeReceipt(id);
        FiscalReceiptDto fiscalReceiptDto = fiscalService.mapToFiscalReceiptDto(receipt);
        return ResponseEntity.ok(fiscalReceiptDto);
    }
}