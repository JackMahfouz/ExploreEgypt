package com.exploreegypt.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "cafes")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Cafe extends BaseService {

    @Column(name = "has_wifi")
    @Builder.Default
    private Boolean hasWifi = true;
}
