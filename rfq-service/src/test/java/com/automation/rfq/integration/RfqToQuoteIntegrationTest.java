package com.automation.rfq.integration;

import com.automation.rfq.domain.Customer;
import com.automation.rfq.domain.PriceList;
import com.automation.rfq.domain.PriceListItem;
import com.automation.rfq.domain.Product;
import com.automation.rfq.dto.AddRfqItemRequest;
import com.automation.rfq.dto.CreateRfqRequest;
import com.automation.rfq.dto.QuoteResponse;
import com.automation.rfq.dto.RfqResponse;
import com.automation.rfq.repository.*;
import com.automation.rfq.service.QuoteService;
import com.automation.rfq.service.RfqService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class RfqToQuoteIntegrationTest {

    @Autowired
    private RfqService rfqService;

    @Autowired
    private QuoteService quoteService;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private PriceListRepository priceListRepository;

    @Autowired
    private PriceListItemRepository priceListItemRepository;

    private Customer customer;
    private Product sensorProduct;
    private Product controllerProduct;

    @BeforeEach
    void setUpData() {
        customer = customerRepository.save(Customer.builder()
                .name("Integration Test Mfg")
                .email("int-test@mfg.com")
                .companyCode("INT-CUST-99")
                .build());

        sensorProduct = productRepository.save(Product.builder()
                .sku("INT-SENS-100")
                .name("Int Sensor")
                .unit("EA")
                .category("Sensors")
                .build());

        controllerProduct = productRepository.save(Product.builder()
                .sku("INT-CTRL-200")
                .name("Int Controller")
                .unit("EA")
                .category("Controllers")
                .build());

        PriceList priceList = priceListRepository.save(PriceList.builder()
                .name("Integration Price List")
                .currency("USD")
                .effectiveFrom(LocalDate.now().minusDays(1))
                .build());

        priceListItemRepository.save(PriceListItem.builder()
                .priceList(priceList)
                .product(sensorProduct)
                .unitPrice(new BigDecimal("100.0000"))
                .build());

        priceListItemRepository.save(PriceListItem.builder()
                .priceList(priceList)
                .product(controllerProduct)
                .unitPrice(new BigDecimal("500.0000"))
                .build());
    }

    @Test
    @DisplayName("Complete Integration Flow: Create RFQ -> Add Items -> Generate Quote -> Verify Subtotal & Tax")
    void completeRfqToQuoteFlow() {
        // Step 1: Create RFQ
        CreateRfqRequest createReq = new CreateRfqRequest(customer.getId(), "USD");
        RfqResponse rfqRes = rfqService.createRfq(createReq);
        assertThat(rfqRes).isNotNull();
        assertThat(rfqRes.rfqNumber()).startsWith("RFQ-");

        // Step 2: Add RFQ Items
        // 10 Sensors @ $100 = $1,000
        // 2 Controllers @ $500 = $1,000
        rfqService.addRfqItem(rfqRes.id(), new AddRfqItemRequest(sensorProduct.getId(), 10, "High temp rating"));
        RfqResponse updatedRfq = rfqService.addRfqItem(rfqRes.id(), new AddRfqItemRequest(controllerProduct.getId(), 2, "24V DC"));
        assertThat(updatedRfq.items()).hasSize(2);

        // Step 3: Generate Quote (Tax 18%)
        com.automation.rfq.dto.GenerateQuoteRequest quoteReq = new com.automation.rfq.dto.GenerateQuoteRequest(rfqRes.id(), new BigDecimal("0.1800"));
        QuoteResponse quoteRes = quoteService.generateQuote(quoteReq);

        // Step 4: Verify Totals
        // Subtotal = $2,000.00
        // Tax 18% = $360.00
        // Total = $2,360.00
        assertThat(quoteRes).isNotNull();
        assertThat(quoteRes.quoteNumber()).startsWith("QT-");
        assertThat(quoteRes.subtotal()).isEqualByComparingTo(new BigDecimal("2000.0000"));
        assertThat(quoteRes.taxAmount()).isEqualByComparingTo(new BigDecimal("360.0000"));
        assertThat(quoteRes.totalAmount()).isEqualByComparingTo(new BigDecimal("2360.0000"));
        assertThat(quoteRes.items()).hasSize(2);

        // Step 5: Verify Listing RFQs and Quotes
        var allRfqs = rfqService.getAllRfqs(null);
        assertThat(allRfqs).isNotEmpty();

        var quotesForRfq = quoteService.getQuotesByRfqId(rfqRes.id());
        assertThat(quotesForRfq).hasSize(1);
        assertThat(quotesForRfq.get(0).id()).isEqualTo(quoteRes.id());

        // Step 6: Accept Quote
        QuoteResponse acceptedQuote = quoteService.acceptQuote(quoteRes.id());
        assertThat(acceptedQuote.status()).isEqualTo(com.automation.rfq.domain.QuoteStatus.ACCEPTED);
    }
}
