package com.legacykeep.notification.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Event DTO for user OTP verification requests.
 * 
 * This event is published when a user requests OTP verification
 * and triggers the notification service to send an OTP email.
 * 
 * @author LegacyKeep Team
 * @version 1.0.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserOtpVerificationRequestedEvent {
    
    private String eventId;
    private Long userId;
    private String email;
    private String username;
    private String firstName;
    private String lastName;
    private String fullName;
    private String otpCode;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime requestedAt;
    
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime expiresAt;
    
    private String sourceService;
    
    @Builder.Default
    private String eventType = "USER_OTP_VERIFICATION_REQUESTED";

    /**
     * Factory method to create OTP verification event.
     */
    public static UserOtpVerificationRequestedEvent create(Long userId, String email, String username,
                                                           String firstName, String lastName,
                                                           String otpCode, LocalDateTime expiresAt) {
        String fullName = (firstName != null && lastName != null) ?
            firstName + " " + lastName :
            (firstName != null ? firstName : (lastName != null ? lastName : username));

        return UserOtpVerificationRequestedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .userId(userId)
                .email(email)
                .username(username)
                .firstName(firstName)
                .lastName(lastName)
                .fullName(fullName)
                .otpCode(otpCode)
                .requestedAt(LocalDateTime.now())
                .expiresAt(expiresAt)
                .sourceService("auth-service")
                .eventType("USER_OTP_VERIFICATION_REQUESTED")
                .build();
    }
}
