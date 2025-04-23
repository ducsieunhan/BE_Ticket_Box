package com.ticket.box.config;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import com.ticket.box.util.VNPayUtil;

@Configuration
public class VNPayConfig {

    @Value("${vnp_PayUrl}")
    private String vnpPayUrl; // Changed to non-static

    @Value("${vnp_ReturnUrl}")
    private String vnpReturnUrl;

    @Value("${vnp_TmnCode}")
    private String vnpTmnCode;

    @Value("${secretKey}")
    private String secretKey; // Changed to non-static

    @Value("${vnp_ApiUrl}")
    private String vnpApiUrl; // Fixed placeholder syntax

    @Value("${vnp_Version}")
    private String vnpVersion;

    @Value("${vnp_Command}")
    private String vnpCommand; // Fixed placeholder syntax

    @Value("${orderType}")
    private String orderType;

    public Map<String, String> getVNPayConfig() {
        Map<String, String> vnpParamsMap = new HashMap<>();
        vnpParamsMap.put("vnp_Version", this.vnpVersion);
        vnpParamsMap.put("vnp_Command", this.vnpCommand);
        vnpParamsMap.put("vnp_TmnCode", this.vnpTmnCode);
        vnpParamsMap.put("vnp_CurrCode", "VND");
        vnpParamsMap.put("vnp_TxnRef", VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderInfo", "Thanh toan don hang:" + VNPayUtil.getRandomNumber(8));
        vnpParamsMap.put("vnp_OrderType", this.orderType);
        vnpParamsMap.put("vnp_Locale", "vn");
        vnpParamsMap.put("vnp_ReturnUrl", this.vnpReturnUrl);
        vnpParamsMap.put("vnp_BankCode", "NCB");

        Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone("Etc/GMT+7"));
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        String vnpCreateDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_CreateDate", vnpCreateDate);

        calendar.add(Calendar.MINUTE, 15);
        String vnpExpireDate = formatter.format(calendar.getTime());
        vnpParamsMap.put("vnp_ExpireDate", vnpExpireDate);

        return vnpParamsMap;
    }

    public String getVnpPayUrl() {
        return vnpPayUrl;
    }

    public String getSecretKey() {
        return secretKey;
    }
}