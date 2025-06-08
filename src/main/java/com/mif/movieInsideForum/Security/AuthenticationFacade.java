package com.mif.movieInsideForum.Security;

import com.mif.movieInsideForum.Collection.Role;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Module.User.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class AuthenticationFacade implements IAuthenticationFacade {
    private final UserDetailsService userDetailsService;
    private final UserService userService;

    @Override
    public Authentication getAuthentication() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        return authentication;
    }


    @Override
    public UserDetails getUserDetails() {
        Authentication authentication = getAuthentication();
        // không authen hoặc authen nhưng là người dùng ẩn danh ( chưa có trong db )
        if (this.getAuthentication() == null || authentication instanceof AnonymousAuthenticationToken) {
            return null;
        }
        return (UserDetails) this.getAuthentication().getPrincipal();
    }


    @Override
    public Role getRole() {
        if (this.getUserDetails() == null) {
            return null;
        }
        User user = (User) this.getUserDetails();
        return user.getRole();
    }
    @Override
    public User getUser() {
        return (User) getUserDetails();
    }

    @Override
    public ObjectId getUserId() {
        return getUser().getId();
    }
}
