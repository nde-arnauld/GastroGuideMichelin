package com.cours_m2.gastroguidemichelin.service;

import com.cours_m2.gastroguidemichelin.restaurant.entity.RestaurantEntity;
import com.cours_m2.gastroguidemichelin.restaurant.repository.RestaurantRepository;
import com.cours_m2.gastroguidemichelin.restaurant.service.RestaurantService;
import com.cours_m2.gastroguidemichelin.storage.service.S3Service;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RestaurantServiceTest {

    @InjectMocks
    private RestaurantService restaurantService;

    @Mock
    private RestaurantRepository restaurantRepository;

    @Mock
    private S3Service s3Service;

    @Mock
    private MultipartFile image;

    @Test
    void shouldCreateRestaurantWithImage() throws IOException {
        RestaurantEntity restaurant = new RestaurantEntity();

        when(image.isEmpty()).thenReturn(false);
        when(s3Service.uploadFile("restaurant", image)).thenReturn("restaurant/image.ext");
        when(restaurantRepository.save(restaurant)).thenAnswer(invocation -> invocation.getArgument(0));

        RestaurantEntity result = restaurantService.createRestaurant(restaurant, image);

        assertEquals("restaurant/image.ext", result.getImage());

        verify(s3Service).uploadFile("restaurant", image);
        verify(restaurantRepository).save(restaurant);
    }
}
