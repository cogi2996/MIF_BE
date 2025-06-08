package com.mif.movieInsideForum.Module.User;

import com.mif.movieInsideForum.Collection.Role;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.UserUpdateDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import java.util.List;

public interface UserService {
    User findUserById(ObjectId id);
    User findUserByEmail(String email);
    User getUserInfo(ObjectId id); // New method
    void updateProfile(ObjectId userId, UserUpdateDTO userUpdateDTO) throws IllegalAccessException; // New method
    Slice<User> searchUsersInGroup(String name, ObjectId groupId, Pageable pageable);
    Page<User> findAllAsPage(Pageable pageable);
    void changeUserRole(ObjectId userId, Role newRole);
    void setAccountStatus(ObjectId userId, boolean isActive);
    // Thống kê số lượng người dùng theo từng tháng của một năm
    java.util.Map<Integer, Integer> countUsersByMonth(int year);
    List<User> findUsersByIds(List<ObjectId> userIds);
}