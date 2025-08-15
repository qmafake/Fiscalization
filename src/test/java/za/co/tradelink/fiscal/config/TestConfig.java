package za.co.tradelink.fiscal.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import za.co.tradelink.fiscal.service.FiscalService;
import za.co.tradelink.fiscal.service.ReceiptService;

import static org.mockito.Mockito.mock;

@TestConfiguration
public class TestConfig {

    @Bean
    public ReceiptService receiptService() {
        return mock(ReceiptService.class);
    }

    @Bean
    public FiscalService fiscalService() {
        return mock(FiscalService.class);
    }
}