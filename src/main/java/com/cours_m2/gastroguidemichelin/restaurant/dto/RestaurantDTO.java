package com.cours_m2.gastroguidemichelin.restaurant.dto;

import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

public class RestaurantDTO {
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CreateRestaurantRequest {
        @Schema(description = "Nom du restaurant", example = "Resto U' Vélodrome", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 1, max = 90)
        private String name;

        @Schema(description = "Adresse du restaurant", example = "1 Bd des Aiguillettes, 54500 Vandœuvre-lès-Nancy", requiredMode = Schema.RequiredMode.REQUIRED)
        @Size(min = 1, max = 255)
        private String address;

        public RestaurantEntity toRestaurantEntity() {
            return RestaurantEntity.builder()
                    .name(name)
                    .address(address)
                    .build();
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class UpdateRestaurantRequest {
        @Schema(description = "Nom du restaurant", example = "Resto U' Vélodrome")
        private String name;

        @Schema(description = "Adresse du restaurant", example = "1 Bd des Aiguillettes, 54500 Vandœuvre-lès-Nancy")
        private String address;

        @Schema(description = "URL de l'image du restaurant", example = "https://example.com/image.jpg")
        @Nullable
        private String image;

        public RestaurantEntity toRestaurantEntity() {
            RestaurantEntity restaurant = new RestaurantEntity();
            return RestaurantEntity.builder()
                    .name(name)
                    .address(address)
                    .image(image)
                    .build();
        }
    }

    /**
     * Nous avons décidé d'utiliser une seule classe de réponse pour car il n'y a pas
     * une grande différence entre les éléments retournés par les différentes requêtes.
     * (Lors de la création, la moyenne n'est pas forcément nécessaire, mais pour éviter
     * une duplication, nous conservons un seul DTO pour les réponses.)
     */
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @Getter
    @Setter
    public static class RestaurantResponse {
        @Schema(description = "Identifiant unique du restaurant", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        @Min(value = 1, message = "L'identifiant est un nombre entier positif.")
        private Long id;

        @Schema(description = "Nom du restaurant", example = "Resto U' Vélodrome", requiredMode = Schema.RequiredMode.REQUIRED)
        private String name;

        @Schema(description = "Adresse du restaurant", example = "1 Bd des Aiguillettes, 54500 Vandœuvre-lès-Nancy", requiredMode = Schema.RequiredMode.REQUIRED)
        private String address;

        @Schema(description = "URL de l'image du restaurant", example = "https://example.com/image.jpg")
        private String image;

        @Schema(description = "Moyenne des notes du restaurant", example = "2.5")
        private Double moyenne;

        public static RestaurantResponse fromRestaurantEntity(RestaurantEntity restaurant, Double moyenne) {
            return RestaurantResponse.builder()
                    .id(restaurant.getId())
                    .name(restaurant.getName())
                    .address(restaurant.getAddress())
                    .image(restaurant.getImage())
                    .moyenne(moyenne)
                    .build();
        }
    }
}
