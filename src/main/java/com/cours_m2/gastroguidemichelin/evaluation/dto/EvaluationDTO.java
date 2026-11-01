package com.cours_m2.gastroguidemichelin.evaluation.dto;

import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

public class EvaluationDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateEvaluationRequest {
        @NotBlank
        @Size(max = 50)
        private String evaluatorName;

        @NotBlank
        @Size(max = 255)
        private String comment;

        @NotNull
        @Min(0)
        @Max(3)
        private Integer note;

        private List<String> images;

        public EvaluationEntity toEvaluationEntity() {
            return EvaluationEntity.builder()
                    .evaluatorName(evaluatorName)
                    .comment(comment)
                    .note(note)
                    .images(images)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateEvaluationRequest {
        @Size(max = 50)
        private String evaluatorName;

        @Size(max = 255)
        private String comment;

        @Min(0)
        @Max(3)
        private Integer note;

        private List<String> images;

        public EvaluationEntity toEvaluationEntity() {
            return EvaluationEntity.builder()
                    .evaluatorName(evaluatorName)
                    .comment(comment)
                    .note(note)
                    .images(images)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class EvaluationResponse {
        private Long id;
        private String evaluatorName;
        private String comment;
        private Integer note;
        private List<String> images;
        private Long restaurantId;

        public static EvaluationResponse fromEvaluationEntity(EvaluationEntity evaluation) {
            return EvaluationResponse.builder()
                    .id(evaluation.getId())
                    .evaluatorName(evaluation.getEvaluatorName())
                    .comment(evaluation.getComment())
                    .note(evaluation.getNote())
                    .images(evaluation.getImages())
                    .restaurantId(evaluation.getRestaurant().getId())
                    .build();
        }
    }
}
