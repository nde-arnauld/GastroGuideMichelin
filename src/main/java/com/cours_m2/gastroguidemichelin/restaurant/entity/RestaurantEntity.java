package com.cours_m2.gastroguidemichelin.restaurant.entity;

import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "restaurants")
public class RestaurantEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 90)
    private String name;

    private String address;

    private String image;

    @OneToMany(
            mappedBy = "restaurant",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<EvaluationEntity> evaluations = new ArrayList<>();
}
