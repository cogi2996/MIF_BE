package com.mif.movieInsideForum.Module.Authentication;

import com.mif.movieInsideForum.Collection.Role;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Collection.Field.Provider;
import com.mif.movieInsideForum.DTO.AuthenticationRequest;
import com.mif.movieInsideForum.DTO.AuthenticationResponse;
import com.mif.movieInsideForum.DTO.OTP;
import com.mif.movieInsideForum.DTO.RegisterRequest;
import com.mif.movieInsideForum.Filter.JwtService;
import com.mif.movieInsideForum.Module.User.UserRepository;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Value;
import java.util.Collections;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private  final AuthenticationFacade authenticationFacade;

    @Value("${google.clientId}")
    private String googleClientId;

    @Transactional
    public void  changePassword(String newPassword, String email){
        // Validate input fields
        User user = userRepository.findByEmail(email).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        // Update password
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public void register(RegisterRequest request) {
        // Validate input fields
        if (request.getEmail() == null || request.getEmail().isEmpty()) {
            throw new RuntimeException("Email must not be empty");
        }

        if (request.getPassword() == null || request.getPassword().length() < 6) {
            throw new RuntimeException("Password must be at least 6 characters long");
        }

        if (request.getDisplayName() == null || request.getDisplayName().isEmpty()) {
            throw new RuntimeException("Display name must not be empty");
        }

        // Check if user already exists
        User dbUser = userRepository.findByEmail(request.getEmail()).orElse(null);

        if (dbUser != null) {
            if (dbUser.getIsActive()) {
                throw new RuntimeException("User already exists");
            } else {
                dbUser.setDisplayName(request.getDisplayName());
                dbUser.setPassword(passwordEncoder.encode(request.getPassword()));
                dbUser.setProfilePictureUrl(request.getProfilePictureUrl());
                dbUser.setBio(request.getBio());
                dbUser.setRole(Role.USER);
                dbUser.setIsActive(false);
                userRepository.save(dbUser);
                return;
            }
        }

        // Create new user
        User newUser = User.builder()
                .displayName(request.getDisplayName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .profilePictureUrl(request.getProfilePictureUrl())
                .bio(request.getBio())
                .role(Role.USER)
                .isActive(false)
                .build();

        userRepository.save(newUser);
    }


    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new RuntimeException("User not found")
        );
        if (!user.getIsActive() ) {
            throw new RuntimeException("Account is not activated");
        }
        if(!user.isAccountNonLocked()){
            throw new RuntimeException("Account is locked");
        }
        var jwtToken = jwtService.generateToken(user);
        return AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    public AuthenticationResponse registerOTP(OTP otp, HttpSession session) {
        // authen otp not correct return to register
        if (!session.getAttribute("OTP").equals(otp.getOtp())) {
            throw new RuntimeException("OTP not correct");
        }
        // save user
        Optional<User> userOtp = userRepository.findByEmail((String) session.getAttribute("email"));

        if (userOtp.isPresent()) {
            User user = userOtp.get();
            user.setIsActive(true);
            userRepository.save(user);
            // clear session
            session.removeAttribute("OTP");
            session.removeAttribute("email");
            return AuthenticationResponse.builder()
                    .accessToken(jwtService.generateToken(user))
                    .build();
        } else {
            throw new RuntimeException("User not found");
        }
    }

    public AuthenticationResponse googleLogin(String googleIdTokenString) {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
                .setAudience(Collections.singletonList(googleClientId))
                .build();
        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(googleIdTokenString);
        } catch (Exception e) {
            throw new RuntimeException("Invalid ID token");
        }
        if (idToken != null) {
            GoogleIdToken.Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name");
            // Tìm user theo email, nếu chưa có thì tạo mới
            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User newUser = User.builder()
                        .email(email)
                        .displayName(name)
                        .isActive(true)
                        .provider(Provider.GOOGLE)
                        .role(Role.USER)
                        .build();
                return userRepository.save(newUser);
            });
            // Sinh JWT token
            String token = jwtService.generateToken(user);
            return AuthenticationResponse.builder()
                    .accessToken(token)
                    .build();
        } else {
            throw new RuntimeException("Invalid ID token");
        }
    }

}