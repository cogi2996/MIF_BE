package com.mif.movieInsideForum.Module.Authentication;

import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.*;
import com.mif.movieInsideForum.DTO.Request.PasswordDTO;
import com.mif.movieInsideForum.Messaging.Producer.EmailProducer;
import com.mif.movieInsideForum.Property.AppProperties;
import com.mif.movieInsideForum.Module.User.UserRepository;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import com.mif.movieInsideForum.Util.OTPGenerator;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService service;
    private final EmailProducer emailProducer;
    private final UserRepository userRepository;


    @PostMapping("/reset-password/OTP")
    public ResponseEntity<ResponseWrapper<Void>> requestPasswordReset(HttpSession session, @RequestBody Map<String, String> email) {
        Optional<User> userOptional = userRepository.findByEmail(email.get("email"));
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            String otp = OTPGenerator.generateOTP();
            session.setAttribute("OTP-reset-pass", otp);
            session.setAttribute("email-reset-pass", user.getEmail());
            // expire in 5 minutes
            session.setMaxInactiveInterval(300);
            emailProducer.sendEmail(user.getEmail(), "OTP MIF for reset password, please don't leak this!", otp);
            return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                    .status("success")
                    .message("If the email exists, a reset link has been sent.")
                    .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.<Void>builder()
                        .status("fail")
                        .message("User not found")
                        .build());

    }

    @PostMapping("/reset-password/OTP/verify")
    public ResponseEntity<ResponseWrapper<Void>> verifyRequestPassOTP(@RequestBody OTP otp, HttpSession session) {
        if (session.getAttribute("OTP-reset-pass") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.<Void>builder()
                            .status("fail")
                            .message("OTP is expired")
                            .build());
        }
        if (otp.getOtp().equals(session.getAttribute("OTP-reset-pass"))) {
            // add atttribute verify true
            session.setAttribute("isVerified", true);
            return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                    .status("success")
                    .message("OTP verified successfully")
                    .build());
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ResponseWrapper.<Void>builder()
                        .status("fail")
                        .message("OTP is incorrect")
                        .build());
    }

    // api nhan token
    @PostMapping("/reset-password")
    public ResponseEntity<ResponseWrapper<Void>> resetPassword(@Valid @RequestBody PasswordDTO newPassword, HttpSession session) {
        // Retrieve the token from the session
        if (session.getAttribute("isVerified") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.<Void>builder()
                            .status("fail")
                            .message("Token is expired")
                            .build());
        }
        try {

            if (session.getAttribute("isVerified").equals(true)) {
                service.changePassword(newPassword.getNewPassword(), session.getAttribute("email-reset-pass").toString());
                // remove attribute isVerified
                session.removeAttribute("isVerified");

                return ResponseEntity.ok(ResponseWrapper.<Void>builder()
                        .status("success")
                        .message("Password reset successfully")
                        .build());

            }
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.<Void>builder()
                            .status("fail")
                            .message("Token is incorrect")
                            .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.<Void>builder()
                            .status("fail")
                            .message(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> register(@Valid @RequestBody RegisterRequest registerRequest, HttpSession session) {
        service.register(registerRequest);
        String otp = OTPGenerator.generateOTP();

        // Gửi message đến RabbitMQ để gửi email
        emailProducer.sendEmail(registerRequest.getEmail(), "OTP MIF, please don't leak this!", otp);

        session.setAttribute("OTP", otp);
        session.setAttribute("email", registerRequest.getEmail());
        // expire in 5 minutes
        session.setMaxInactiveInterval(300);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseWrapper.<AuthenticationResponse>builder()
                        .status("success")
                        .message("Đăng ký thành công, hãy tiến hành xác thực email")
                        .build());
    }

    @PostMapping("/login")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> authenticate(@Valid @RequestBody AuthenticationRequest loginRequest) {
        AuthenticationResponse response = service.authenticate(loginRequest);
        return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
                .status("success")
                .message("Đăng nhập thành công")
                .data(response)
                .build());
    }

    @PostMapping("/register/OTP")
    public ResponseEntity<ResponseWrapper<?>> registerOTP(@RequestBody OTP otp, HttpSession session) {
        // nếu không có session thì trả về lỗi
        if (session.getAttribute("OTP") == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseWrapper.<Void>builder()
                            .status("fail")
                            .message("OTP is expired")
                            .build());
        }

        AuthenticationResponse response = service.registerOTP(otp, session);
        return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
                .status("success")
                .message("OTP verified successfully")
                .data(response)
                .build());
    }

    @PostMapping("/google-login")
    public ResponseEntity<ResponseWrapper<AuthenticationResponse>> googleLogin(@RequestBody Map<String, String> request) {
        String googleIdToken = request.get("idToken");
        try {
            AuthenticationResponse response = service.googleLogin(googleIdToken);
            return ResponseEntity.ok(ResponseWrapper.<AuthenticationResponse>builder()
                    .status("success")
                    .message("Google login successful")
                    .data(response)
                    .build());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ResponseWrapper.<AuthenticationResponse>builder()
                            .status("error")
                            .message("Google login failed: " + e.getMessage())
                            .build());
        }
    }

}