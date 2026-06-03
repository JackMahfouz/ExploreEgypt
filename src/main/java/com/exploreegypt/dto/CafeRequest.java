package com.exploreegypt.dto;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CafeRequest extends BaseServiceRequest {
    private Boolean hasWifi;
}
