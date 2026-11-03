package com.cours_m2.gastroguidemichelin.restaurant.controller;

import com.cours_m2.gastroguidemichelin.restaurant.dto.RestaurantDTO;
import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import com.cours_m2.gastroguidemichelin.restaurant.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Tag(name = "Restaurant", description = "API pour la gestion des restaurants")
@RestController
@RequiredArgsConstructor
@RequestMapping("/restaurants")
public class RestaurantController {
    private final RestaurantService restaurantService;

    /*
     * Une amélioration serait de créer un champ pour la moyenne afin de ne pas avoir
     * à recalculer la moyenne à chaque requête.
     */

    /*
     * Cette méthode permet de créer plusieurs restaurants à la fois.
     * À cause des problèmes de connexion avec la DB, j'y suis contraint
     * pour ne pas avoir à changer des valeurs en utilisant [/POST /api/v1/restaurants] plusieurs fois.
     */
    @Operation(
            summary = "Créer plusieurs restaurants",
            description = "Cette méthode permet de créer plusieurs restaurants à la fois afin de faciliter le test de l'application."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "Restaurants créés avec succès"
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Erreur interne du serveur"
            )
    })
    @PostMapping("/seed")
    @ResponseStatus(HttpStatus.CREATED)
    public String seedRestaurants(
            @Parameter(description = "Liste des restaurants à créer", required = true)
            @Valid
            @RequestBody List<RestaurantDTO.CreateRestaurantRequest> createRequests) {
        for (RestaurantDTO.CreateRestaurantRequest createRequest : createRequests) {
            RestaurantEntity restaurant = createRequest.toRestaurantEntity();
            this.restaurantService.createRestaurant(restaurant);
        }
        return "Restaurants peuplés avec succès!";
    }

    @Operation(summary = "Créer un restaurant", description = "Cette méthode permet de créer un restaurant.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Restaurant créé avec succès",
                    content = {@Content(mediaType = "application/json", schema = @Schema(implementation = RestaurantDTO.RestaurantResponse.class))}),
            @ApiResponse(responseCode = "400", description = "Requête invalide", content = @Content),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur", content = @Content)
    })
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public RestaurantDTO.RestaurantResponse createRestaurant(
            @Valid
            @RequestPart(value = "restaurant") RestaurantDTO.CreateRestaurantRequest createRequest,
            @RequestPart(value = "image", required = false) MultipartFile image
            ) throws IOException {
        RestaurantEntity restaurant = createRequest.toRestaurantEntity();
        RestaurantEntity createdRestaurant = this.restaurantService.createRestaurant(restaurant, image);
        return RestaurantDTO.RestaurantResponse.fromRestaurantEntity(createdRestaurant, -1.0);
    }

    @Operation(summary = "Récupérer tous les restaurants", description = "Cette méthode permet de récupérer tous les restaurants.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurants récupérés avec succès"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<RestaurantDTO.RestaurantResponse> getAllRestaurants() {
        List<RestaurantEntity> existingRestaurants = this.restaurantService.getAllRestaurants();
        return existingRestaurants.stream()
                .map(entity -> {
                        Double starts = this.restaurantService.getRestaurantStarts(entity.getId());
                    return RestaurantDTO.RestaurantResponse.fromRestaurantEntity(entity, starts);
                })
                .toList();
    }

    @Operation(summary = "Récupérer un restaurant par son ID", description = "Cette méthode permet de récupérer un restaurant par son ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant récupéré avec succès"),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDTO.RestaurantResponse getRestaurantBuyId(
            @Parameter(description = "Nombre entier positif du restaurant à récupérer", example = "1", required = true)
            @PathVariable Long id) {
        RestaurantEntity existingRestaurant = this.restaurantService.getRestaurantById(id);
        Double starts = this.restaurantService.getRestaurantStarts(id);
        return RestaurantDTO.RestaurantResponse.fromRestaurantEntity(existingRestaurant, starts);
    }

    @Operation(summary = "Mettre à jour un restaurant", description = "Cette méthode permet de mettre à jour un restaurant par son ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant mis à jour avec succès"),
            @ApiResponse(responseCode = "400", description = "Requête invalide"),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDTO.RestaurantResponse updateRestaurant(
            @Parameter(description = "Nombre entier positif du restaurant à mettre à jour", example = "1", required = true)
            @PathVariable Long id,
            @Valid @RequestBody RestaurantDTO.UpdateRestaurantRequest updateRequest) {
        RestaurantEntity restaurant = updateRequest.toRestaurantEntity();
        restaurant.setId(id);
        RestaurantEntity updatedRestaurant = this.restaurantService.updateRestaurant(restaurant);
        Double starts = this.restaurantService.getRestaurantStarts(id);
        return RestaurantDTO.RestaurantResponse.fromRestaurantEntity(updatedRestaurant, starts);
    }

    @Operation(summary = "Supprimer un restaurant", description = "Cette méthode permet de supprimer un restaurant par son ID.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Restaurant supprimé avec succès"),
            @ApiResponse(responseCode = "404", description = "Restaurant non trouvé"),
            @ApiResponse(responseCode = "500", description = "Erreur interne du serveur")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public RestaurantDTO.RestaurantResponse deleteRestaurant(
            @Parameter(description = "Nombre entier positif du restaurant à supprimer", example = "1", required = true)
            @PathVariable Long id) {
        Double starts = this.restaurantService.getRestaurantStarts(id);
        return RestaurantDTO.RestaurantResponse.fromRestaurantEntity(this.restaurantService.deleteRestaurant(id), starts);
    }
}
