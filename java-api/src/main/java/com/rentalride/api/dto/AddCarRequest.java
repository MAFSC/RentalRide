package com.rentalride.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCarRequest {

    @NotBlank private String brand;
    @NotNull private Long mileage;
    @NotNull private Long enginePower;
    @NotBlank private String fuelType;
    @NotNull private Long year;
    @NotNull private Long interiorScore;
    @NotNull private Long rating;
    @NotNull private Long pricePerDay;
}
