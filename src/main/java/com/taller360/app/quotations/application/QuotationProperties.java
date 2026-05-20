package com.taller360.app.quotations.application;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;

@Validated
@ConfigurationProperties(prefix = "app")
public record QuotationProperties(
        @NotNull
        @DecimalMin(value = "0.00")
        @DecimalMax(value = "1.00")
        BigDecimal taxRate
) {
}
