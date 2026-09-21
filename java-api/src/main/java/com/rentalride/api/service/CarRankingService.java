package com.rentalride.api.service;

import com.rentalride.api.contract.CarBridge;
import com.rentalride.api.dto.AddCarRequest;
import com.rentalride.api.dto.CarDataDto;
import com.rentalride.api.dto.FindBestCarRequest;
import com.rentalride.api.dto.FindBestCarResponse;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tuples.generated.Tuple2;
import org.web3j.tuples.generated.Tuple8;
import org.web3j.tx.gas.DefaultGasProvider;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class CarRankingService {

    private final Web3j web3j;
    private final Credentials credentials;
    private final String carBridgeAddress;

    private volatile List<CarDataDto> cachedCars = null;
    private volatile long cacheTimestamp = 0;
    private static final long CACHE_TTL_MS = 3_600_000L;
    private final Object cacheLock = new Object();

    public CarRankingService(
            @Value("${blockchain.rpc-url}") String rpcUrl,
            @Value("${blockchain.private-key}") String privateKey,
            @Value("${blockchain.car-bridge-address}") String carBridgeAddress) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);
        this.carBridgeAddress = carBridgeAddress;
        log.info("CarRankingService initialized. RPC={}, CarBridge={}", rpcUrl, carBridgeAddress);
    }

    @PostConstruct
    public void preloadCache() {
        Thread t = new Thread(() -> {
            try {
                log.info("Preloading car cache in background...");
                long start = System.currentTimeMillis();
                listAllCars();
                log.info("Cache preloaded in {} ms", System.currentTimeMillis() - start);
            } catch (Exception e) {
                log.error("Failed to preload cache", e);
            }
        }, "cache-preloader");
        t.setDaemon(true);
        t.start();
    }

    @Scheduled(fixedDelay = 1_800_000L)
    public void refreshCache() {
        try {
            log.info("Scheduled cache refresh...");
            cachedCars = null;
            cacheTimestamp = 0;
            listAllCars();
            log.info("Scheduled cache refresh completed");
        } catch (Exception e) {
            log.error("Scheduled cache refresh failed", e);
        }
    }

    public Web3j getWeb3j() {
        return web3j;
    }

    private CarBridge loadContract() {
        return CarBridge.load(carBridgeAddress, web3j, credentials, new DefaultGasProvider());
    }

    public FindBestCarResponse findBestCar(FindBestCarRequest request) throws Exception {
        CarBridge carBridge = loadContract();
        List<BigInteger> carIds = request.getCarIds().stream()
                .map(BigInteger::valueOf)
                .collect(Collectors.toList());

        Tuple2<BigInteger, BigInteger> result = carBridge.findBestCar(
                carIds,
                BigInteger.valueOf(request.getWeightPrice()),
                BigInteger.valueOf(request.getWeightMileage()),
                BigInteger.valueOf(request.getWeightPower()),
                BigInteger.valueOf(request.getWeightRating()),
                BigInteger.valueOf(request.getWeightYear()),
                BigInteger.valueOf(request.getWeightInterior()),
                BigInteger.valueOf(request.getWeightFuel())
        ).send();

        log.info("findBestCar: bestCarId={}, score={}", result.component1(), result.component2());
        return new FindBestCarResponse(result.component1().longValue(), result.component2().toString());
    }

    public long addCar(AddCarRequest request) throws Exception {
        CarBridge carBridge = loadContract();
        carBridge.addCar(
                request.getBrand(),
                BigInteger.valueOf(request.getMileage()),
                BigInteger.valueOf(request.getEnginePower()),
                request.getFuelType(),
                BigInteger.valueOf(request.getYear()),
                BigInteger.valueOf(request.getInteriorScore()),
                BigInteger.valueOf(request.getRating()),
                BigInteger.valueOf(request.getPricePerDay())
        ).send();

        BigInteger count = carBridge.carCount().send();
        cachedCars = null;
        cacheTimestamp = 0;
        return count.longValue() - 1;
    }

    public CarDataDto getCar(long carId) throws Exception {
        CarBridge carBridge = loadContract();
        Tuple8<String, BigInteger, BigInteger, String, BigInteger, BigInteger, BigInteger, BigInteger> tuple =
                carBridge.cars(BigInteger.valueOf(carId)).send();

        return new CarDataDto(
                carId,
                tuple.component1(),
                tuple.component2().longValue(),
                tuple.component3().longValue(),
                tuple.component4(),
                tuple.component5().longValue(),
                tuple.component6().longValue(),
                tuple.component7().longValue(),
                tuple.component8().longValue()
        );
    }

    public List<CarDataDto> listAllCars() throws Exception {
        long now = System.currentTimeMillis();
        List<CarDataDto> cached = cachedCars;
        if (cached != null && (now - cacheTimestamp) < CACHE_TTL_MS) {
            return cached;
        }

        synchronized (cacheLock) {
            now = System.currentTimeMillis();
            cached = cachedCars;
            if (cached != null && (now - cacheTimestamp) < CACHE_TTL_MS) {
                return cached;
            }

            log.info("listAllCars: cache miss, fetching from blockchain...");
            CarBridge carBridge = loadContract();
            BigInteger count = carBridge.carCount().send();
            long total = count.longValue();

            List<CarDataDto> result = new ArrayList<>();
            for (long i = 0; i < total; i++) {
                try {
                    Boolean exists = carBridge.carExists(BigInteger.valueOf(i)).send();
                    if (Boolean.TRUE.equals(exists)) {
                        result.add(getCar(i));
                    }
                } catch (Exception e) {
                    log.warn("Failed to fetch car {}: {}", i, e.getMessage());
                }
            }

            cachedCars = result;
            cacheTimestamp = System.currentTimeMillis();
            log.info("listAllCars: fetched and cached {} cars", result.size());
            return result;
        }
    }
}
