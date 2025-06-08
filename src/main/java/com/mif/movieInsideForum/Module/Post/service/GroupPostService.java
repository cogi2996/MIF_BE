package com.mif.movieInsideForum.Module.Post.service;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.DTO.GroupPostCount;
import com.mif.movieInsideForum.DTO.ProfilePostDTO;

import com.mif.movieInsideForum.DTO.Response.NewestPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostRequestDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GroupPostService {
    GroupPostResponseDTO createPost(GroupPostRequestDTO groupPostRequestDTO);
    GroupPostResponseDTO getPostById(ObjectId postId);
    Slice<NewestPostResponseDTO> getAllPosts(Pageable pageable);
    Slice<GroupPostResponseDTO> getFeaturedPosts(Pageable pageable, ObjectId groupId);
    GroupPostResponseDTO updatePost(ObjectId postId, GroupPostRequestDTO groupPostRequestDTO);
    void deletePost(ObjectId postId);
    Slice<ProfilePostDTO> getProfilePost(ObjectId userId, Pageable pageable, boolean includeBlocked);
    Slice<GroupPostResponseDTO> getPostsByGroupId(ObjectId groupId, Pageable pageable, boolean includeBlocked);
    Long getPostCountForCurrentWeek(ObjectId groupId);
    List<GroupPostCount> getPostCountsForCurrentWeek(List<Group> groups);
    Page<GroupPostResponseDTO> findAllAsPage(Pageable pageable);
    void markReportsAsResolved(String postId);
    Map<Integer, Integer> countPostsByMonth(int year);
    
    // Lấy top 5 bài viết được upvote nhiều nhất
    List<GroupPostResponseDTO> getTop5MostUpvotedPosts();

    // Lấy trending posts trong tuần
    List<GroupPostResponseDTO> getTrendingPostsInWeek(Date startDate, Date endDate);
}