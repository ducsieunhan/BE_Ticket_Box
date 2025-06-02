package com.ticket.box.UnitTest.Service;

import com.paypal.api.payments.Payment;
import com.paypal.base.rest.APIContext;
import com.paypal.base.rest.PayPalRESTException;
import com.ticket.box.config.VNPayConfig;
import com.ticket.box.domain.response.PaymentResDTO;
import com.ticket.box.service.PaymentService;
import com.ticket.box.util.VNPayUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc(addFilters = false)
public class PaymentTest {
  @Mock
  private VNPayConfig vnPayConfig;
  @Mock
  private APIContext apiContext;
  @Mock
  private HttpServletRequest request;
  @InjectMocks
  private PaymentService paymentService;

  private Map<String, String> vnpParams;

  @BeforeEach
  public void setUp(){
    vnpParams = new HashMap<>();
    vnpParams.put("vnp_Version", "1.1.1");
    vnpParams.put("vnp_Command", "pay");
    vnpParams.put("vnp_TmnCode", "ABCABC");
    vnpParams.put("vnp_CurrCode", "VND");
    vnpParams.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
    vnpParams.put("vnp_OrderInfo", "Thanh toan don hang:" + VNPayUtil.getRandomNumber(8));
    vnpParams.put("vnp_OrderType", "other");
    vnpParams.put("vnp_Locale", "vn");
    vnpParams.put("vnp_ReturnUrl", "http:/success");
    vnpParams.put("vnp_BankCode", "NCB");

    Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
    SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
    String vnpCreateDate = formatter.format(calendar.getTime());
    vnpParams.put("vnp_CreateDate", vnpCreateDate);

    calendar.add(Calendar.MINUTE, 15);
    String vnpExpireDate = formatter.format(calendar.getTime());
    vnpParams.put("vnp_ExpireDate", vnpExpireDate);

    APIContext context = new APIContext("abc", "1");
    ReflectionTestUtils.setField(paymentService, "apiContext", context);
  }

  @Test
  public void shouldSuccessVnpPayment() throws UnsupportedEncodingException {
    String amount = "10000";

    when(vnPayConfig.getVNPayConfig()).thenReturn(vnpParams);
    when(request.getParameter("amount")).thenReturn(amount);
    when(vnPayConfig.getVnpPayUrl()).thenReturn("http:/payback");
    when(vnPayConfig.getSecretKey()).thenReturn("secret");

    PaymentResDTO res = this.paymentService.createVnPayPayment(request);

    assertNotNull(res);
    assertAll("Assert check response payment",
            () -> assertEquals("ok", res.getStatus(), "Response status should ok"),
            () -> assertEquals("Successfully",res.getMessage(), "Response should be successfully"),
            () -> assertTrue(res.getUrl().contains("NCB"), "URL should contain the bank code"),
            () -> assertTrue(res.getUrl().contains("vnp_OrderInfo=" + URLEncoder.encode("Thanh toan don hang", StandardCharsets.US_ASCII.toString())), "URL should contain order information"),
            () -> assertTrue(res.getUrl().contains("vnp_Amount=" + amount), "URL should contain amount money")
            );

    verify(vnPayConfig).getVNPayConfig();
    verify(vnPayConfig).getVnpPayUrl();
    verify(vnPayConfig).getSecretKey();
    verify(request).getParameter("amount");
  }

//  @Test
//  public void ShouldSuccessPaypalPayment() throws PayPalRESTException {
//    double amount = 10000;
//    String currency = "USD";
//    String method = "paypal";
//    String intent = "sale";
//    String des = "test";
//    String success_url = "http:/sucess";
//    String failed_url = "http:/failed";
//
//    Payment payment = this.paymentService.createPaymentWithPayPal(amount, currency,method,intent,
//            des, success_url, failed_url);
//
//    assertNotNull(payment);
//  }
}
