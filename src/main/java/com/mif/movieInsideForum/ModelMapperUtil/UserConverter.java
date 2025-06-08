package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.UserDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserConverter {
    private final ModelMapper mapper;

    public UserDTO convertToDTO(User user) {
        return mapper.map(user, UserDTO.class);
    }

    public User convertToEntity(UserDTO userDTO) {
        return mapper.map(userDTO, User.class);
    }
}
