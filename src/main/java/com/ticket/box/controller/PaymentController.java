package com.ticket.box.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nimbusds.jose.shaded.gson.Gson;
import com.nimbusds.jose.shaded.gson.JsonObject;
import com.paypal.api.payments.Links;
import com.paypal.api.payments.Payment;
import com.paypal.base.rest.PayPalRESTException;
import com.ticket.box.config.VNPayConfig;
import com.ticket.box.domain.Order;
import com.ticket.box.domain.response.PaymentResDTO;
import com.ticket.box.domain.response.ResOrderDTO;
import com.ticket.box.service.OrderService;
import com.ticket.box.service.PaymentService;

import io.micrometer.core.ipc.http.HttpSender.Response;
import jakarta.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/v1")
public class PaymentController {

    private static final String SUCCESS_URL = "http://localhost:5173/payment/success";

    private static final String CANCEL_URL = "http://localhost:5173/payment/failed";
    private final PaymentService paymentService;
    private final OrderService orderService;

    public PaymentController(PaymentService paymentService, OrderService orderService) {
        this.paymentService = paymentService;
        this.orderService = orderService;
    }

    @GetMapping("/create_payment")
    public ResponseEntity<?> createPayment(HttpServletRequest request) throws UnsupportedEncodingException {
        PaymentResDTO paymentResDTO = paymentService.createVnPayPayment(request);

        return ResponseEntity.ok().body(paymentResDTO);
    }

    @GetMapping("/vn-pay-callback")
    public ResponseEntity<PaymentResDTO> callbackHandle(@RequestParam("vnp_ResponseCode") String code) {
        String status = code;
        if (status.equals("00")) {
            return ResponseEntity.ok()
                    .body(new PaymentResDTO("ok", "successfully", "http://localhost:5173/payment/success"));
        } else {
            return ResponseEntity.badRequest()
                    .body(new PaymentResDTO("failed", "failed", "http://localhost:5173/payment/failed"));
        }
    }

    @PutMapping("/update-payment-status")
    public ResponseEntity<Void> handleAfterPayment(@RequestBody Long orderId) {

        this.orderService.handleAfterPayment(orderId);
        return ResponseEntity.ok().body(null);
    }

    // Paypal
    @PostMapping("paypal/pay")
    public ResponseEntity<PaymentResDTO> processPayment(@RequestParam("amount") Double amount) {
        try {
            Payment payment = paymentService.createPaymentWithPayPal(
                    amount,
                    "USD",
                    "paypal",
                    "sale",
                    "Description",
                    CANCEL_URL,
                    SUCCESS_URL

            );
            for (Links link : payment.getLinks()) {
                if (link.getRel().equals("approval_url")) {
                    return ResponseEntity.ok().body(new PaymentResDTO("ok", "successfully", link.getHref()));
                }
            }
        } catch (PayPalRESTException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body(new PaymentResDTO("failed", "failed", null));
    }

    @GetMapping("/paypal/success")
    public ResponseEntity<?> success(@RequestParam("paymentId") String paymentId,
            @RequestParam("PayerID") String payerId) {
        try {
            Payment payment = paymentService.executePayment(paymentId, payerId);
            if (payment.getState().equals("approved")) {
                return ResponseEntity.ok().body(payment);
            }

        } catch (PayPalRESTException e) {
            // TODO: handle exception
            e.printStackTrace();
        }
        return ResponseEntity.badRequest().body("Failed to execute payment");
    }

    @GetMapping("/paypal/cancel")
    public ResponseEntity<?> cancel() {
        return ResponseEntity.ok().body(CANCEL_URL);
    }

}
