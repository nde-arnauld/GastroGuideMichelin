package com.cours_m2.gastroguidemichelin.evaluation.entity;

import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Entity
@Table(name = "evaluations")
public class EvaluationEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "evaluator_name", length = 50)
    private String evaluatorName;

    @Column(length = 255)
    private String comment;

    @Min(0)
    @Max(3)
    private Integer note;

    @ElementCollection
    @CollectionTable(
            name = "evaluation_images",
    joinColumns = @JoinColumn(name = "evaluation_id"))
    @Column(name = "image_url", nullable = false)
    private List<String> images;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id", nullable = false)
    private RestaurantEntity restaurant;
}
