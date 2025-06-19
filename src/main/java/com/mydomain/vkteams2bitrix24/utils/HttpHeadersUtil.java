package com.mydomain.vkteams2bitrix24.utils;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

public class HttpHeadersUtil {

    public static HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }

}
