package com.taller360.app.quotations.domain;

import com.taller360.app.shared.exception.BusinessRuleException;
import com.taller360.app.shared.exception.QuotationAlreadyDecidedException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class QuotationTest {

    @Test
    void shouldOnlyAllowModifyingDraftQuotations() {
        Quotation quotation = quotationWithStatus(QuotationStatus.APPROVED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                quotation::ensureDraftEditable
        );

        assertEquals("Only DRAFT quotations can be modified", exception.getMessage());
    }

    @Test
    void shouldOnlyAllowModifyingDraftQuotationsWhenRejected() {
        Quotation quotation = quotationWithStatus(QuotationStatus.REJECTED);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                quotation::ensureDraftEditable
        );

        assertEquals("Only DRAFT quotations can be modified", exception.getMessage());
    }

    @Test
    void shouldNotSendQuotationWithoutItems() {
        Quotation quotation = quotationWithStatus(QuotationStatus.DRAFT);

        BusinessRuleException exception = assertThrows(
                BusinessRuleException.class,
                () -> quotation.send(new BigDecimal("0.15"))
        );

        assertEquals("A quotation without items cannot be sent", exception.getMessage());
    }

    @Test
    void shouldNotApproveQuotationTwice() {
        Quotation quotation = quotationWithStatus(QuotationStatus.APPROVED);

        QuotationAlreadyDecidedException exception = assertThrows(
                QuotationAlreadyDecidedException.class,
                quotation::approve
        );

        assertEquals("A quotation cannot be approved twice", exception.getMessage());
    }

    @Test
    void shouldNotRejectQuotationAfterApproval() {
        Quotation quotation = quotationWithStatus(QuotationStatus.APPROVED);

        QuotationAlreadyDecidedException exception = assertThrows(
                QuotationAlreadyDecidedException.class,
                quotation::reject
        );

        assertEquals("A quotation cannot be rejected after being approved", exception.getMessage());
    }

    @Test
    void shouldNotApproveQuotationAfterRejection() {
        Quotation quotation = quotationWithStatus(QuotationStatus.REJECTED);

        QuotationAlreadyDecidedException exception = assertThrows(
                QuotationAlreadyDecidedException.class,
                quotation::approve
        );

        assertEquals("A quotation cannot be approved after being rejected", exception.getMessage());
    }

    private Quotation quotationWithStatus(QuotationStatus status) {
        Quotation quotation = new Quotation();
        quotation.setStatus(status);
        quotation.setSubtotal(BigDecimal.ZERO);
        quotation.setTax(BigDecimal.ZERO);
        quotation.setTotal(BigDecimal.ZERO);
        return quotation;
    }
}
