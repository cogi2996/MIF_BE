package com.mif.movieInsideForum.Module.Post.service.impl;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.Module.Post.repository.SavedPostRepository;
import com.mif.movieInsideForum.Module.Post.service.SavedPostService;
import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.Collection.SavedPost;
import com.mif.movieInsideForum.Collection.User;
import com.mif.movieInsideForum.Mapper.GroupPostMapper;
import com.mif.movieInsideForum.ModelMapperUtil.GroupPostConverter;
import com.mif.movieInsideForum.Module.Group.GroupRepository;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;
import com.mif.movieInsideForum.Module.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SavedPostServiceImpl implements SavedPostService {
    private static final Logger logger = LoggerFactory.getLogger(SavedPostServiceImpl.class);
    private final SavedPostRepository savedPostRepository;
    private final UserRepository userRepository;
    private final GroupPostRepository groupPostRepository;
    private final GroupPostConverter groupPostConverter;
    private final GroupRepository groupRepository;
    private final GroupPostMapper groupPostMapper;

    @Override
    @Transactional
    public void savePost(ObjectId userId, ObjectId postId) {
        if (!savedPostRepository.existsByUserIdAndPostId(userId, postId)) {
            User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
            GroupPost post = groupPostRepository.findById(postId).orElseThrow(() -> new RuntimeException("Post not found"));
            // check availability of group's post
            Optional<Group> groupOpt = groupRepository.findById(post.getGroup().getId());
            if (groupOpt.isEmpty()) {
                throw new RuntimeException("Group not found");
            }

            SavedPost savedPost = new SavedPost();
            savedPost.setUser(user);
            savedPost.setPost(post);
            savedPost.setGroupId(groupOpt.get().getId());

            savedPostRepository.save(savedPost);
        }
    }

    @Override
    public void unsavePost(ObjectId userId, ObjectId postId) {
        savedPostRepository.deleteByUserIdAndPostId(userId, postId);
    }

    @Override
    public boolean isPostSaved(ObjectId userId, ObjectId postId) {
        return savedPostRepository.existsByUserIdAndPostId(userId, postId);
    }

    @Override
    public Slice<GroupPostResponseDTO> getSavedPosts(ObjectId userId, Pageable pageable) {
        Optional<Slice<SavedPost>> savedPosts = savedPostRepository.findByUserId(userId, pageable);
        // Return an empty slice if no saved posts are found
        return savedPosts.map(posts -> posts.map(savedPost -> groupPostMapper.toGroupPostResponseDTO(savedPost.getPost())))
                .orElseGet(() -> new SliceImpl<>(List.of(), pageable, false));
    }

    @Override
    public Map<String, Boolean> batchCheckSavedStatus(ObjectId userId, List<ObjectId> postIds) {
        List<SavedPost> savedPosts = savedPostRepository.findByUserIdAndPostIdIn(userId, postIds);
        Map<String, Boolean> result = new HashMap<>();
        for (ObjectId postId : postIds) {
            result.put(postId.toHexString(), false);
        }
        for (SavedPost savedPost : savedPosts) {
            String postId = savedPost.getPost().getId().toHexString();
            result.put(postId, true);
        }
        return result;
    }
}