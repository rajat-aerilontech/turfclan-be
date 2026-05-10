package com.aerilon.turfclan.service.impl;

import com.aerilon.turfclan.dto.RecaptchaResponse;
import com.aerilon.turfclan.service.RecaptchaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecaptchaServiceImpl implements RecaptchaService {

    @Value("${google.recaptcha.key.secret}")
    private String secretKey;

    @Value("${google.recaptcha.url}")
    private String googleVerifySiteUrl;

    @Value("${google.recaptcha.score-threshold}")
    private Float thresholdScore;

    private final RestTemplate restTemplate = new RestTemplate();

    @Override
    public boolean isVerify(String token) {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("secret", secretKey);
        params.add("response", token);
        RecaptchaResponse response = restTemplate.postForObject(
                googleVerifySiteUrl, params, RecaptchaResponse.class
        );
        log.info("Recaptcha response: {} for thresholdScore: {}", response, thresholdScore);
        return response != null && response.success() && response.score() >= thresholdScore;
    }
}
