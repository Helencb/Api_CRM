package com.helen.api_crm.sale.dto;


import com.helen.api_crm.sale.model.PaymentMethod;
import com.helen.api_crm.sale.model.SaleStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    private Long id;
    private BigDecimal subtotal;
    private BigDecimal discount;
    private BigDecimal totalValue;
    private String description;
    private SaleStatus status;
    private PaymentMethod paymentMethod;
    private String failureReason;
    private Long clientId;
    private String clientName;
    private Long sellerId;
    private String sellerName;
    private LocalDateTime createdAt;

    private List<SaleItemResponse> items;

    @Data
    @AllArgsConstructor
    public static class SaleItemResponse {
        private String productName;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;
    }
}
