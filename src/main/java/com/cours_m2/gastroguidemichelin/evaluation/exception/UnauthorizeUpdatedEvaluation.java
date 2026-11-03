package com.cours_m2.gastroguidemichelin.evaluation.exception;

public class UnauthorizeUpdatedEvaluation extends RuntimeException {
    public UnauthorizeUpdatedEvaluation() {
        super("Vous n'êtes pas autorisé à modifier cette évaluation.");
    }
}
