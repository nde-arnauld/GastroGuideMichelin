package com.cours_m2.gastroguidemichelin.evaluation.service;

import com.cours_m2.gastroguidemichelin.evaluation.dto.EvaluationDTO;
import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import com.cours_m2.gastroguidemichelin.evaluation.repository.EvaluationRepository;
import com.cours_m2.gastroguidemichelin.storage.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class EvaluationService {
    private static final Logger log = LoggerFactory.getLogger(EvaluationService.class);
    private final EvaluationRepository evaluationRepository;
    private final S3Service s3Service;

    public EvaluationEntity createEvaluation(EvaluationEntity evaluation, List<MultipartFile> images) throws IOException {
        List<String> imageKeys = new ArrayList<>();
        if (images != null && !images.isEmpty()) {
            for (MultipartFile image : images) {
                imageKeys.add(this.s3Service.uploadFile("evaluation", image));
            }
            log.info("Images uploaded to S3: {}", imageKeys);
        }
        evaluation.setImages(imageKeys);
        return this.evaluationRepository.save(evaluation);
    }

    public List<EvaluationEntity> getAllEvaluations() {
        return this.evaluationRepository.findAll();
    }

    public EvaluationEntity getEvaluationById(Long id) {
        return this.evaluationRepository.getReferenceById(id);
    }

    public EvaluationEntity updateEvaluation(EvaluationEntity evaluation) {
        EvaluationEntity existingEvaluation = this.getEvaluationById(evaluation.getId());

        if (evaluation.getEvaluatorName() != null) existingEvaluation.setEvaluatorName(evaluation.getEvaluatorName());
        if (evaluation.getComment() != null) existingEvaluation.setComment(evaluation.getComment());
        if (evaluation.getNote() != null) existingEvaluation.setNote(evaluation.getNote());
        if (evaluation.getImages() != null) existingEvaluation.setImages(evaluation.getImages());

        return this.evaluationRepository.save(existingEvaluation);
    }

    public EvaluationEntity deleteEvaluation(Long id) {
        EvaluationEntity existingEvaluation = this.getEvaluationById(id);
        this.evaluationRepository.deleteById(id);
        return existingEvaluation;
    }

    public List<EvaluationEntity> getEvaluationsByRestaurantId(Long restaurantId) {
        return this.evaluationRepository.findAll().stream()
                .filter(evaluation -> evaluation.getRestaurant().getId().equals(restaurantId))
                .toList();
    }

    public List<EvaluationEntity> getEvaluationsByKeywords(List<String> keywords) {
        return this.evaluationRepository.findAll().stream()
                .filter(evaluation -> keywords.stream()
                        .anyMatch(keyword ->
                                evaluation.getComment()
                                        .toLowerCase()
                                        .contains(keyword.toLowerCase())))
                .toList();
    }

    public List<EvaluationEntity> getEvaluationsByEvaluatorName(String evaluatorName) {
        List<EvaluationEntity> evaluations = this.getAllEvaluations();
        return evaluations.stream()
                .filter(evaluation -> {
                    String evaluatorNameFromEntity = evaluation.getEvaluatorName();
                            return evaluatorNameFromEntity
                                    .trim()
                                    .equalsIgnoreCase(evaluatorName);
                        }
                )
                .toList();
    }
}
