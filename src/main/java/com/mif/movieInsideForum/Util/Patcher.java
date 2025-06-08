package com.mif.movieInsideForum.Util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostRequestDTO;
import org.springframework.stereotype.Component;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.Module.Movie.Movie;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.DTO.GroupDTO;
import com.mif.movieInsideForum.DTO.UserUpdateDTO;

@Component
public class Patcher {
    public static void moviePatcher(Movie existingMovie, Movie incompleteMovie) throws IllegalAccessException {
        Class<?> movieClass = Movie.class;
        Field[] movieFields = movieClass.getDeclaredFields();
        System.out.println(movieFields.length);
        for (Field field : movieFields) {
            System.out.println(field.getName());
            field.setAccessible(true);
            Object value = field.get(incompleteMovie);
            if (value != null) {
                field.set(existingMovie, value);
            }
            field.setAccessible(false);
        }
    }


    public static void userPatcher(User existUser, UserUpdateDTO incompleteUser) throws IllegalAccessException {
        Class<?> userClass = User.class;
        Class<?> userUpdateDTOClass = UserUpdateDTO.class;

        // Create a map of fields in UserUpdateDTO for quick lookup
        Map<String, Field> userUpdateDTOFields = new HashMap<>();
        for (Field field : userUpdateDTOClass.getDeclaredFields()) {
            field.setAccessible(true);
            userUpdateDTOFields.put(field.getName(), field);
        }

        // Iterate over fields in User and update if present in UserUpdateDTO
        for (Field userField : userClass.getDeclaredFields()) {
            userField.setAccessible(true);
            // kiểm tra xem field có trong UserUpdateDTO không và lấy ra [field, value]
            Field updateField = userUpdateDTOFields.get(userField.getName());
            if (updateField != null) {
                // lấy value của updateField trong instant incompleteUser có
                Object value = updateField.get(incompleteUser);
                if (value != null) {
                    userField.set(existUser, value);
                }
            }
        }


    }

    public static void groupPatcher(Group existingGroup, GroupDTO incompleteGroup) throws IllegalAccessException {
        Class<?> groupClass = Group.class;
        Class<?> groupDTOClass = GroupDTO.class;

        // Create a map of fields in GroupDTO for quick lookup
        Map<String, Field> groupDTOFields = new HashMap<>();
        for (Field field : groupDTOClass.getDeclaredFields()) {
            field.setAccessible(true);
            groupDTOFields.put(field.getName(), field);
        }

        // Iterate over fields in Group and update if present in GroupDTO
        for (Field groupField : groupClass.getDeclaredFields()) {
            groupField.setAccessible(true);
            Field updateField = groupDTOFields.get(groupField.getName());
            if (updateField != null) {
                Object value = updateField.get(incompleteGroup);
                if (value != null) {
                    groupField.set(existingGroup, value);
                }
            }
        }
    }
    // grouppost patcher
    public static void groupPostPatcher(GroupPost existingGroup, GroupPostRequestDTO incompleteGroup) throws IllegalAccessException {
        Class<?> groupPostClass = GroupPost.class;
        Class<?> groupPostRequestDTOClassDTOClass = GroupPostRequestDTO.class;

        // Create a map of fields in GroupDTO for quick lookup
        Map<String, Field> groupDTOFields = new HashMap<>();
        for (Field field : groupPostRequestDTOClassDTOClass.getDeclaredFields()) {
            field.setAccessible(true);
            groupDTOFields.put(field.getName(), field);
        }

        // Iterate over fields in Group and update if present in GroupDTO
        for (Field groupField : groupPostClass.getDeclaredFields()) {
            groupField.setAccessible(true);
            Field updateField = groupDTOFields.get(groupField.getName());
            if (updateField != null) {
                Object value = updateField.get(incompleteGroup);
                if (value != null) {
                    groupField.set(existingGroup, value);
                }
            }
        }
    }

}


