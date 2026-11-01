package com.cours_m2.gastroguidemichelin.restaurant.service;

import com.cours_m2.gastroguidemichelin.evaluation.entity.EvaluationEntity;
import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import com.cours_m2.gastroguidemichelin.restaurant.repository.RestaurantRepository;
import com.cours_m2.gastroguidemichelin.storage.service.S3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@Service
public class RestaurantService {
    private final RestaurantRepository restaurantRepository;
    private final S3Service s3Service;

    public void createRestaurant(RestaurantEntity restaurant) {
        this.restaurantRepository.save(restaurant);
    }

    public RestaurantEntity createRestaurant(RestaurantEntity restaurant, MultipartFile image) throws IOException {
        if (image != null && !image.isEmpty()) {
            String imageKey = this.s3Service.uploadFile("restaurant", image);
            restaurant.setImage(imageKey);
        }

        return this.restaurantRepository.save(restaurant);
    }

    public List<RestaurantEntity> getAllRestaurants() {
        return this.restaurantRepository.findAll();
    }

    public RestaurantEntity getRestaurantById(Long id) {
        return this.restaurantRepository.getReferenceById(id);
    }

    public RestaurantEntity updateRestaurant(RestaurantEntity restaurant) {
        RestaurantEntity existingRestaurant = this.getRestaurantById(restaurant.getId()); // Cette ligne retournera une RestaurantNotFoundException si le restaurant n'est pas trouvé.
        if (restaurant.getName() != null) existingRestaurant.setName(restaurant.getName());
        if (restaurant.getAddress() != null) existingRestaurant.setAddress(restaurant.getAddress());
        if (restaurant.getImage() != null) existingRestaurant.setImage(restaurant.getImage());
        return this.restaurantRepository.save(existingRestaurant);
    }

    public RestaurantEntity deleteRestaurant(Long id) {
        RestaurantEntity existingRestaurant = this.getRestaurantById(id);
        this.restaurantRepository.deleteById(id);
        return existingRestaurant;
    }

    public Double getRestaurantStarts(Long id) {
        RestaurantEntity restaurant = this.getRestaurantById(id);
        List<Integer> notes = restaurant.getEvaluations().stream()
                .map(EvaluationEntity::getNote)
                .toList();
        if (notes.isEmpty()) {
            return -1.0;
        }
        return notes.stream()
                .mapToDouble(Integer::doubleValue)
                .average()
                .orElse(-1.0);
    }
}
