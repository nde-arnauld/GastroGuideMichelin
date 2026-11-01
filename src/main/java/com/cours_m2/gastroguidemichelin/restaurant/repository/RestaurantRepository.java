package com.cours_m2.gastroguidemichelin.restaurant.repository;

import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantEntity, Long> {
}
