package com.cours_m2.gastroguidemichelin.evaluation.controller;

import com.cours_m2.gastroguidemichelin.evaluation.dto.EvaluationDTO;
import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import com.cours_m2.gastroguidemichelin.evaluation.exception.UnauthorizeUpdatedEvaluation;
import com.cours_m2.gastroguidemichelin.evaluation.service.EvaluationService;
import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import com.cours_m2.gastroguidemichelin.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.logging.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
public class EvaluationController {
    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);
    private final RestaurantService restaurantService;
    private final EvaluationService evaluationService;

    @PostMapping(
            value = "/restaurants/{idRes}/evaluations",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluationDTO.EvaluationResponse createEvaluation(
            @Valid
            @RequestPart(value = "evaluation") EvaluationDTO.CreateEvaluationRequest request,
            @RequestPart(value = "images", required = false) List<MultipartFile> images,
            @PathVariable Long idRes) throws IOException {

        RestaurantEntity restaurant = this.restaurantService.getRestaurantById(idRes);
        EvaluationEntity evaluation = request.toEvaluationEntity();
        evaluation.setRestaurant(restaurant);
        evaluation.getRestaurant().setId(idRes);
        EvaluationEntity savedEvaluation = this.evaluationService.createEvaluation(evaluation, images);
        return EvaluationDTO.EvaluationResponse
                .fromEvaluationEntity(savedEvaluation);
    }

    @DeleteMapping("/evaluations/{idEval}")
    @ResponseStatus(HttpStatus.OK)
    public EvaluationDTO.EvaluationResponse deleteEvaluation(@PathVariable Long idEval) {
        return EvaluationDTO.EvaluationResponse
                .fromEvaluationEntity(this.evaluationService.deleteEvaluation(idEval));
    }

    @GetMapping("/evaluations")
    @ResponseStatus(HttpStatus.OK)
    public List<EvaluationDTO.EvaluationResponse> getAllEvaluations() {
        log.info("Fetching all evaluations");
        return this.evaluationService.getAllEvaluations().stream()
                .map(EvaluationDTO.EvaluationResponse::fromEvaluationEntity)
                .toList();
    }

    @GetMapping(value = "/evaluations", params = "keywords")
    @ResponseStatus(HttpStatus.OK)
    public List<EvaluationDTO.EvaluationResponse> getEvaluationsByKeywords(@RequestParam List<String> keywords) {
        log.info("Fetching evaluations by keywords: {}", keywords);
        return this.evaluationService
                .getEvaluationsByKeywords(keywords).stream()
                .map(EvaluationDTO.EvaluationResponse::fromEvaluationEntity)
                .toList();
    }

    @GetMapping(value = "/evaluations-lucene", params = "keywords")
    @ResponseStatus(HttpStatus.OK)
    public List<EvaluationDTO.EvaluationResponse> getEvaluationsByKeywordsLucene(@RequestParam List<String> keywords) {
        log.info("Fetching evaluations by keywords lucene: {}", keywords);
        return this.evaluationService
                .getEvaluationsByKeywords(keywords).stream()
                .map(EvaluationDTO.EvaluationResponse::fromEvaluationEntity)
                .toList();
    }

    @GetMapping("/evaluations/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<EvaluationDTO.EvaluationResponse> getEvaluationsByEvaluatorName(@RequestBody String evaluatorName) {
        List<EvaluationEntity> evaluations = this.evaluationService.getEvaluationsByEvaluatorName(evaluatorName);
        return evaluations.stream()
                .map(EvaluationDTO.EvaluationResponse::fromEvaluationEntity)
                .toList();
    }

    @PreAuthorize("isAuthenticated()")
    @PatchMapping("/evaluations/{idEval}")
    @ResponseStatus(HttpStatus.OK)
    public EvaluationDTO.EvaluationResponse updateEvaluation(@Valid @RequestBody EvaluationDTO.UpdateEvaluationRequest request,
                                                             @PathVariable Long idEval,
                                                             Authentication principal) {
        EvaluationEntity evaluation = request.toEvaluationEntity();
        evaluation.setId(idEval);

        log.info("Authenticated user: {}", principal.getName());

        if (!principal.getName().trim().equals(evaluation.getEvaluatorName().trim())) {
            throw new UnauthorizeUpdatedEvaluation();
        }

        return EvaluationDTO.EvaluationResponse
                .fromEvaluationEntity(this.evaluationService.updateEvaluation(evaluation));
    }
}
