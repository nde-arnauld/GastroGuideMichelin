package com.cours_m2.gastroguidemichelin.evaluation.repository;

import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<EvaluationEntity, Long> {
}
