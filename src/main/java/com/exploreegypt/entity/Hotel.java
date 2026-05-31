package com.exploreegypt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "hotels")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Hotel extends BaseService {

    @Column(columnDefinition = "integer check (stars >= 1 and stars <= 5)")
    private Integer stars;
}
