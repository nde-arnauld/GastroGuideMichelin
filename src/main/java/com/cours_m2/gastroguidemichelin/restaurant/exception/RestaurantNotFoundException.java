package com.cours_m2.gastroguidemichelin.restaurant.exception;

public class RestaurantNotFoundException extends RestaurantException{
    public RestaurantNotFoundException(Long id) {
        super("Le restaurant avec l'id : "+ id + " n'apparaît pas.");
    }
}
