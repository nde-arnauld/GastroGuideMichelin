package com.cours_m2.gastroguidemichelin.evaluation.controller;

import com.cours_m2.gastroguidemichelin.evaluation.dto.EvaluationDTO;
import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import com.cours_m2.gastroguidemichelin.evaluation.service.EvaluationService;
import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import com.cours_m2.gastroguidemichelin.restaurant.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class EvaluationController {
    private static final Logger log = LoggerFactory.getLogger(EvaluationController.class);
    private final RestaurantService restaurantService;
    private final EvaluationService evaluationService;

    @PostMapping("/restaurants/{idRes}/evaluations")
    @ResponseStatus(HttpStatus.CREATED)
    public EvaluationDTO.EvaluationResponse createEvaluation(@Valid @RequestBody EvaluationDTO.CreateEvaluationRequest request,
                                                             @PathVariable Long idRes) {
        RestaurantEntity restaurant = this.restaurantService.getRestaurantById(idRes);
        EvaluationEntity evaluation = request.toEvaluationEntity();
        evaluation.setRestaurant(restaurant);
        return EvaluationDTO.EvaluationResponse
                .fromEvaluationEntity(this.evaluationService.createEvaluation(evaluation, idRes));
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

    @GetMapping("/evaluations/mine")
    @ResponseStatus(HttpStatus.OK)
    public List<EvaluationDTO.EvaluationResponse> getEvaluationsByEvaluatorName(@RequestBody String evaluatorName) {
        List<EvaluationEntity> evaluations = this.evaluationService.getEvaluationsByEvaluatorName(evaluatorName);
        return evaluations.stream()
                .map(EvaluationDTO.EvaluationResponse::fromEvaluationEntity)
                .toList();
    }

    @PatchMapping("/evaluations/{idEval}")
    @ResponseStatus(HttpStatus.OK)
    public EvaluationDTO.EvaluationResponse updateEvaluation(@Valid @RequestBody EvaluationDTO.UpdateEvaluationRequest request,
                                                             @PathVariable Long idEval) {
        EvaluationEntity evaluation = request.toEvaluationEntity();
        evaluation.setId(idEval);

        return EvaluationDTO.EvaluationResponse
                .fromEvaluationEntity(this.evaluationService.updateEvaluation(evaluation));
    }
}
