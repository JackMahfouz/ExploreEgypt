package com.exploreegypt.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;
import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SiteRequest extends BaseServiceRequest {
    private String description;
    private BigDecimal rate;
}
