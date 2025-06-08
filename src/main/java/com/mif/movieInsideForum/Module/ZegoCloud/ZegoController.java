package com.mif.movieInsideForum.Module.ZegoCloud;

import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/token")
@RequiredArgsConstructor
public class ZegoController {

    private final ZegoTokenService tokenService;

    private long appId = 84210812;

    private String serverSecret = "5bacd772ffbaef9a664945d0abb4c175";

    @PostMapping("/generate")
    public ResponseEntity<JSONObject> generateToken(@RequestParam String userId,
                                                    @RequestParam int effectiveTimeInSeconds,
                                                    @RequestParam(required = false) String payload) {
        String token = tokenService.generateToken(appId, userId, serverSecret, effectiveTimeInSeconds, payload);
        JSONObject response = new JSONObject();
        response.put("token", token);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/decrypt")
    public JSONObject decryptToken(@RequestParam String token) {
        // Gọi dịch vụ để giải mã token
        return tokenService.decryptToken(token, serverSecret);
    }
}