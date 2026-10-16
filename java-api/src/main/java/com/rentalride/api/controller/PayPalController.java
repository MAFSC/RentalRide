package com.rentalride.api.controller;

import com.paypal.core.PayPalHttpClient;
import com.paypal.http.HttpResponse;
import com.paypal.orders.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/paypal")
@CrossOrigin(origins = "*")
public class PayPalController {

    @Autowired
    private PayPalHttpClient client;

    @PostMapping("/create-order")
    public ResponseEntity<?> createOrder(@RequestBody Map<String, Object> body) {
        try {
            String rentalId = String.valueOf(body.get("rentalId"));
            String amount = String.valueOf(body.get("amount"));
            String currency = (String) body.getOrDefault("currency", "USD");

            OrderRequest request = new OrderRequest();
            request.checkoutPaymentIntent("CAPTURE");

            AmountWithBreakdown amountBreakdown = new AmountWithBreakdown()
                    .currencyCode(currency)
                    .value(amount);

            PurchaseUnitRequest unit = new PurchaseUnitRequest()
                    .amountWithBreakdown(amountBreakdown)
                    .description("RentalRide booking #" + rentalId)
                    .customId(rentalId);

            request.purchaseUnits(List.of(unit));

            ApplicationContext appContext = new ApplicationContext()
                    .brandName("RentalRide")
                    .landingPage("LOGIN")
                    .userAction("PAY_NOW")
                    .returnUrl("http://localhost:3000/checkout/success")
                    .cancelUrl("http://localhost:3000/checkout/cancel");
            request.applicationContext(appContext);

            OrdersCreateRequest createRequest = new OrdersCreateRequest().requestBody(request);
            HttpResponse<Order> response = client.execute(createRequest);

            return ResponseEntity.ok(Map.of("id", response.result().id()));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/capture-order")
    public ResponseEntity<?> captureOrder(@RequestBody Map<String, String> body) {
        try {
            String orderId = body.get("orderId");
            String rentalId = body.get("rentalId");

            if (orderId == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "orderId required"));
            }

            OrdersCaptureRequest captureRequest = new OrdersCaptureRequest(orderId);
            captureRequest.requestBody(new OrderRequest());

            HttpResponse<Order> response = client.execute(captureRequest);
            Order order = response.result();

            if ("COMPLETED".equals(order.status())) {
                String captureId = order.purchaseUnits().get(0)
                        .payments().captures().get(0).id();

                System.out.println("[Stylus] record payment " + captureId + " for rental " + rentalId);

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "captureId", captureId
                ));
            }

            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "status", order.status()
            ));
        } catch (IOException e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok(Map.of("ok", true, "env", "sandbox"));
    }
}
