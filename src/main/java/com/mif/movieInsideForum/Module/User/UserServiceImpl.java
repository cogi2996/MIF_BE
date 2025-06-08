package com.mif.movieInsideForum.Module.User;

import com.mif.movieInsideForum.Collection.Role;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.UserUpdateDTO;
import com.mif.movieInsideForum.Util.Patcher;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findUserById(ObjectId id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    @Override
    public User getUserInfo(ObjectId id) {
        return userRepository.findById(id).orElse(null);
    }
    @Override
    public void updateProfile(ObjectId userId, UserUpdateDTO userUpdateDTO) throws IllegalAccessException {
        User dbUser = findUserById(userId);
        Patcher.userPatcher(dbUser, userUpdateDTO);
        userRepository.save(dbUser);
    }
    @Override
    public Slice<User> searchUsersInGroup(String name, ObjectId groupId, Pageable pageable) {
        return userRepository.findByNameAndGroupId(name, groupId, pageable);
    }
    @Override
    public void changeUserRole(ObjectId userId, Role newRole) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setRole(newRole);
        userRepository.save(user);
    }
    // find all
    @Override
    public Page<User> findAllAsPage(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public void setAccountStatus(ObjectId userId, boolean isLocked) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setIsLocked(isLocked);
        userRepository.save(user);
    }

    @Override
    public Map<Integer, Integer> countUsersByMonth(int year) {
        List<UserMonthCount> results = userRepository.countUsersByMonth(year);
        Map<Integer, Integer> monthCount = new HashMap<>();
        for (int i = 1; i <= 12; i++) monthCount.put(i, 0);
        for (UserMonthCount item : results) {
            monthCount.put(item.getMonth(), item.getCount());
        }
        return monthCount;
    }

    @Override
    public List<User> findUsersByIds(List<ObjectId> userIds) {
        return userRepository.findAllById(userIds);
    }
}