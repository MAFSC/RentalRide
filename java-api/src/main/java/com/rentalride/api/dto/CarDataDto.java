package com.rentalride.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CarDataDto {
    private Long carId;
    private String brand;
    private Long mileage;
    private Long enginePower;
    private String fuelType;
    private Long year;
    private Long interiorScore;
    private Long rating;
    private Long pricePerDay;
}
