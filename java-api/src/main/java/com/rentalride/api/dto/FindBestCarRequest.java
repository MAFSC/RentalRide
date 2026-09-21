package com.rentalride.api.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class FindBestCarRequest {

    @NotEmpty(message = "carIds не может быть пустым")
    private List<Long> carIds;

    @NotNull private Long weightPrice;
    @NotNull private Long weightMileage;
    @NotNull private Long weightPower;
    @NotNull private Long weightRating;
    @NotNull private Long weightYear;
    @NotNull private Long weightInterior;
    @NotNull private Long weightFuel;
}
