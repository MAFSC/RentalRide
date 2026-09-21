package com.rentalride.api.controller;

import com.rentalride.api.dto.AddCarRequest;
import com.rentalride.api.dto.CarDataDto;
import com.rentalride.api.dto.FindBestCarRequest;
import com.rentalride.api.dto.FindBestCarResponse;
import com.rentalride.api.service.CarRankingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarRankingService carRankingService;

    /** POST /api/cars/find-best — поиск лучшей машины по 7 критериям */
    @PostMapping("/find-best")
    public ResponseEntity<FindBestCarResponse> findBestCar(
            @Valid @RequestBody FindBestCarRequest request) {
        try {
            FindBestCarResponse response = carRankingService.findBestCar(request);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("findBestCar failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /** POST /api/cars/add — добавить новую машину */
    @PostMapping("/add")
    public ResponseEntity<Long> addCar(@Valid @RequestBody AddCarRequest request) {
        try {
            long carId = carRankingService.addCar(request);
            return ResponseEntity.ok(carId);
        } catch (Exception e) {
            log.error("addCar failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /** GET /api/cars/list — список всех машин */
    @GetMapping("/list")
    public ResponseEntity<List<CarDataDto>> listAllCars() {
        try {
            List<CarDataDto> cars = carRankingService.listAllCars();
            return ResponseEntity.ok(cars);
        } catch (Exception e) {
            log.error("listAllCars failed", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /** GET /api/cars/{id} — данные одной машины */
    @GetMapping("/{id}")
    public ResponseEntity<CarDataDto> getCar(@PathVariable long id) {
        try {
            CarDataDto car = carRankingService.getCar(id);
            return ResponseEntity.ok(car);
        } catch (Exception e) {
            log.error("getCar failed", e);
            return ResponseEntity.notFound().build();
        }
    }

    /** GET /api/cars/health — проверка связи с блокчейном */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        try {
            var blockNumber = carRankingService.getWeb3j().ethBlockNumber().send().getBlockNumber();
            return ResponseEntity.ok("OK, block=" + blockNumber);
        } catch (Exception e) {
            return ResponseEntity.status(503).body("Blockchain unavailable: " + e.getMessage());
        }
    }
}
