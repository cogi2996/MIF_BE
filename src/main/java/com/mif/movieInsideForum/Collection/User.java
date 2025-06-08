package com.mif.movieInsideForum.Collection;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Map;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Field.Provider;
import com.mif.movieInsideForum.Module.ActivityAnalytics.dto.UserBadgeDTO;
import com.mif.movieInsideForum.Module.ActivityAnalytics.enums.BadgeLevel;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@Document(collection = "users")
public class User implements UserDetails {
    @JsonSerialize(using = ToStringSerializer.class)
    @Id
    private ObjectId id;

    private String displayName;

    private String email;
    @JsonIgnore
    private String password;

    private String profilePictureUrl;

    private String bio;

    // day of birth
    private Date dob;
    @Builder.Default
    @JsonIgnore
    private Boolean isActive = false;
    @Builder.Default
    @JsonIgnore
    private Boolean isLocked = false;
    @CreatedDate
    private Date createdAt;
    @LastModifiedDate
    private Date updatedAt;
    @JsonIgnore
    private Role role;
    private Provider provider;
    private String googleSub;

    private UserType userType = UserType.NORMAL; // New field for user type
    private double balance = 0.0; // New field for account balance

    // Map lưu trữ huy hiệu của user trong từng group
    // Key: groupId, Value: thông tin huy hiệu
    private Map<String, BadgeLevel> badgeMap = new HashMap<>();

    @JsonIgnore
    @DBRef(lazy = true)
    private List<Token> tokens;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.id.toString();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !isLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return isActive;
    }
}