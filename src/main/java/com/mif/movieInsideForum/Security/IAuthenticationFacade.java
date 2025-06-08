package com.mif.movieInsideForum.Security;

import com.mif.movieInsideForum.Collection.Role;
import com.mif.movieInsideForum.Collection.User;
import org.bson.types.ObjectId;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

public interface IAuthenticationFacade {
    Authentication getAuthentication();
    UserDetails getUserDetails();
    Role getRole();
    User getUser();
    ObjectId getUserId();
}
