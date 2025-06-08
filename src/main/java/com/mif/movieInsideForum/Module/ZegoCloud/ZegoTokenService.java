package com.mif.movieInsideForum.Module.ZegoCloud;

import im.zego.serverassistant.utils.TokenServerAssistant;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.stereotype.Service;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Base64;

@Service
public class ZegoTokenService {

    private static final String AES_CBC_PKCS5_PADDING = "AES/CBC/PKCS5Padding";

    // Hàm tạo token
    public String generateToken(long appId, String userId, String serverSecret, int effectiveTimeInSeconds, String payload) {
        // Lấy token từ TokenServerAssistant (Giả sử bạn đã có TokenServerAssistant như trong mã mẫu)
        TokenServerAssistant.TokenInfo token = TokenServerAssistant.generateToken04(appId, userId, serverSecret, effectiveTimeInSeconds, payload);
        if (token.error == null || token.error.code == TokenServerAssistant.ErrorCode.SUCCESS) {
            return token.data;
        } else {
            throw new RuntimeException("Error generating token: " + token.error);
        }
    }

    // Hàm giải mã token
    public JSONObject decryptToken(String token, String secretKey) {
        String noVersionToken = token.substring(2);  // Loại bỏ phần version

        byte[] tokenBytes = Base64.getDecoder().decode(noVersionToken.getBytes());
        ByteBuffer buffer = ByteBuffer.wrap(tokenBytes);
        buffer.order(ByteOrder.BIG_ENDIAN);

        long expiredTime = buffer.getLong();
        int IVLength = buffer.getShort();
        byte[] ivBytes = new byte[IVLength];
        buffer.get(ivBytes);

        int contentLength = buffer.getShort();
        byte[] contentBytes = new byte[contentLength];
        buffer.get(contentBytes);

        try {
            SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            Cipher cipher = Cipher.getInstance(AES_CBC_PKCS5_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, key, iv);

            byte[] rawBytes = cipher.doFinal(contentBytes);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(new String(rawBytes));  // Trả về nội dung giải mã
        } catch (Exception e) {
            throw new RuntimeException("Token decryption failed", e);
        }
    }
}
