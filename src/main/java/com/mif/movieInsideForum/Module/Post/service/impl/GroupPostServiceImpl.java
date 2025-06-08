package com.mif.movieInsideForum.Module.Post.service.impl;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.DTO.Response.NewestPostResponseDTO;
import com.mif.movieInsideForum.Mapper.GroupPostMapper;
import com.mif.movieInsideForum.Mapper.ProfilePostMapper;
import com.mif.movieInsideForum.Mapper.UserMapper;
import com.mif.movieInsideForum.Module.Comment.CommentRepository;
import com.mif.movieInsideForum.Module.Group.GroupRepository;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRatingsRepository;
import com.mif.movieInsideForum.Module.Post.repository.SavedPostRepository;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostRequestDTO;
import com.mif.movieInsideForum.Module.Post.DTO.GroupPostResponseDTO;
import com.mif.movieInsideForum.Module.Post.entity.GroupPostReport;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;
import com.mif.movieInsideForum.Module.Post.service.GroupPostService;
import com.mif.movieInsideForum.Util.Patcher;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;

import com.mif.movieInsideForum.DTO.GroupPostCount;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.DTO.ProfilePostDTO;
import com.mif.movieInsideForum.ModelMapperUtil.GroupPostConverter;
import com.mif.movieInsideForum.Security.AuthenticationFacade;

import lombok.RequiredArgsConstructor;
import com.mif.movieInsideForum.Module.ActivityAnalytics.activity.ActivityMessageService;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import com.mif.movieInsideForum.Module.Post.repository.GroupPostMonthCount;
import com.mif.movieInsideForum.Module.Post.repository.ReferenceMovieRepository;
import com.mif.movieInsideForum.Module.Post.entity.ReferenceMovie;

@Slf4j
@Service
@RequiredArgsConstructor
public class GroupPostServiceImpl implements GroupPostService {
    private final GroupPostRepository groupPostRepository;
    private final GroupPostConverter groupPostConverter;
    private final AuthenticationFacade authenticationFacade;
    private final ProfilePostMapper profilePostMapper;
    private final GroupPostMapper groupPostMapper;
    private final GroupRepository groupRepository;
    private final CommentRepository commentRepository;
    private final SavedPostRepository savedPostRepository;
    private final GroupPostRatingsRepository groupPostRatingsRepository;
    private final ActivityMessageService activityMessageService;
    private final UserMapper userMapper;
    private final MongoTemplate mongoTemplate;
    private final ReferenceMovieRepository referenceMovieRepository;
    
    private static final Pattern MOVIE_REFERENCE_PATTERN = Pattern.compile("@\\[(.*?)\\]\\((.*?)\\)");

    // private final GroupPostReportService reportService;

    @Override
    public GroupPostResponseDTO createPost(GroupPostRequestDTO groupPostRequestDTO) {
        GroupPost groupPost = groupPostMapper.convertToEntity(groupPostRequestDTO);
        GroupPost savedPost = groupPostRepository.save(groupPost);
        GroupPost groupPostDB = groupPostRepository.findById(savedPost.getId()).orElseThrow(() -> new RuntimeException("Post not found"));
        
        // Xử lý tham chiếu phim
        processMovieReferences(groupPostDB);
        
        // Send activity message for post creation
        activityMessageService.sendGroupPostCreated(
            authenticationFacade.getUser().getId().toString(),
            groupPostDB.getId().toString(),
            groupPostDB.getGroup().getId().toString(),
            groupPostRequestDTO.getOwner().getId().toString()
        );
        
        return groupPostMapper.toGroupPostResponseDTO(groupPostDB);
    }

    private void processMovieReferences(GroupPost post) {
        String content = post.getContent();
        Matcher matcher = MOVIE_REFERENCE_PATTERN.matcher(content);
        
        while (matcher.find()) {
            String movieName = matcher.group(1);
            String movieId = matcher.group(2);
            
            try {
                ObjectId movieObjectId = new ObjectId(movieId);
                ObjectId groupId = post.getGroup().getId();
                
                Optional<ReferenceMovie> existingRef = referenceMovieRepository.findByMovieIdAndGroupId(movieObjectId, groupId);
                
                if (existingRef.isPresent()) {
                    ReferenceMovie ref = existingRef.get();
                    ref.setReferenceCount(ref.getReferenceCount() + 1);
                    ref.setUpdatedAt(new Date());
                    referenceMovieRepository.save(ref);
                } else {
                    ReferenceMovie newRef = ReferenceMovie.builder()
                        .movieId(movieObjectId)
                        .movieName(movieName)
                        .groupId(groupId)
                        .referenceCount(1)
                        .build();
                    referenceMovieRepository.save(newRef);
                }
            } catch (IllegalArgumentException e) {
                log.error("Invalid movie ID format: {}", movieId);
            }
        }
    }

    @Override
    public GroupPostResponseDTO getPostById(ObjectId postId) {
        Optional<GroupPost> optionalPost = groupPostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            GroupPost post = optionalPost.get();
            GroupPostResponseDTO dto = groupPostMapper.toGroupPostResponseDTO(post);
            
            // Sử dụng UserMapper để lấy thông tin user và huy hiệu
            dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), post.getGroupId().toString()));
            return dto;
        }
        return null;
    }

    @Override
    public Slice<NewestPostResponseDTO> getAllPosts(Pageable pageable) {
        Slice<GroupPost> posts = groupPostRepository.findAllByIsBlock(false, pageable);
        List<ObjectId> groupIds = posts.stream().map(GroupPost::getGroupId).toList();

        ObjectId userId = authenticationFacade.getUserId();
        List<ObjectId> joinedGroupIds = groupRepository.findJoinedGroupIdsByUserId(userId, groupIds).stream().map(Group::getId).toList();
        
        return posts.map(groupPost -> {
            NewestPostResponseDTO dto = groupPostMapper.toNewestPostResponseDTO(groupPost);
            dto.setIsJoined(joinedGroupIds.contains(groupPost.getGroupId()));
            dto.setOwner(userMapper.toUserDTOWithGroupBadge(groupPost.getOwner(), groupPost.getGroupId().toString()));
            return dto;
        });
    }

    @Override
    public Slice<GroupPostResponseDTO> getFeaturedPosts(Pageable pageable, ObjectId groupId) {
        return groupPostRepository.findFeaturedPosts(pageable, groupId, false).map(post -> {
            GroupPostResponseDTO dto = groupPostConverter.convertToDTO(post);
            dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), groupId.toString()));
            return dto;
        });
    }

    @Override
    public GroupPostResponseDTO updatePost(ObjectId postId, GroupPostRequestDTO groupPostRequestDTO) {
        Optional<GroupPost> optionalPost = groupPostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            GroupPost post = optionalPost.get();
            if (!authenticationFacade.getUser().getId().equals(post.getOwner().getId())) {
                throw new RuntimeException("User is not the owner of this post");
            }
            // Validate the incoming request DTO
            if (groupPostRequestDTO == null || groupPostRequestDTO.getContent() == null || groupPostRequestDTO.getContent().isEmpty()) {
                throw new IllegalArgumentException("Post content cannot be null or empty");
            }
            groupPostRequestDTO.setGroupId(null);
            groupPostRequestDTO.setOwner(null);

            // patcher
            try {
                Patcher.groupPostPatcher(post, groupPostRequestDTO);
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Error updating post");
            }
            GroupPost updatedPost = groupPostRepository.save(post);
            return groupPostMapper.toGroupPostResponseDTO(updatedPost); // Return DTO
        } else {
            throw new RuntimeException("Post not found");
        }
    }

    @Override
    public void deletePost(ObjectId postId) {
        Optional<GroupPost> optionalPost = groupPostRepository.findById(postId);
        if (optionalPost.isPresent()) {
            GroupPost post = optionalPost.get();
            if (!authenticationFacade.getUser().getId().equals(post.getOwner().getId())) {
                throw new RuntimeException("User is not the owner of this post");
            }
            //delete all comments of this post
            commentRepository.deleteByPostId(postId);
            // delete savedPost of this post
            savedPostRepository.deleteByPostId(postId);
            // delete all likes of this post
            groupPostRatingsRepository.deleteByPostId(postId);
            // delete post
            groupPostRepository.delete(post);

        } else {
            throw new RuntimeException("Post not found");
        }
    }

    @Override
    public Slice<ProfilePostDTO> getProfilePost(ObjectId userId, Pageable pageable, boolean includeBlocked) {
        var groupPosts = groupPostRepository.findProfilePostWithBlockOption(userId, includeBlocked, pageable);
        return groupPosts.map(post -> {
            ProfilePostDTO dto = profilePostMapper.toProfilePostDTO(post);
            dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), post.getGroupId().toString()));
            return dto;
        });
    }

    @Override
    public Slice<GroupPostResponseDTO> getPostsByGroupId(ObjectId groupId, Pageable pageable, boolean includeBlocked) {
        return groupPostRepository.findByGroupIdWithBlockOption(groupId, includeBlocked, pageable).map(post -> {
            GroupPostResponseDTO dto = groupPostMapper.toGroupPostResponseDTO(post);
            dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), groupId.toString()));
            return dto;
        });
    }

    @Override
    public Long getPostCountForCurrentWeek(ObjectId groupId) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());

        Long count = groupPostRepository.countPostsForCurrentWeek(groupId, startDate, endDate, false);
        return count != null ? count : 0L;
    }

    @Override
    public List<GroupPostCount> getPostCountsForCurrentWeek(List<Group> groups) {
        LocalDate now = LocalDate.now();
        LocalDate startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        Date startDate = Date.from(startOfWeek.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(endOfWeek.atTime(LocalTime.MAX).atZone(ZoneId.systemDefault()).toInstant());
        List<ObjectId> groupIds = groups.stream().map(Group::getId).toList();
        return groupPostRepository.countPostsForGroupsSinceDate(groupIds, startDate, endDate, false);
    }

    @Override
    public Page<GroupPostResponseDTO> findAllAsPage(Pageable pageable) {
        Page<GroupPost> posts = groupPostRepository.findAll(pageable);
        return posts.map(post -> {
            GroupPostResponseDTO dto = groupPostMapper.toGroupPostResponseDTO(post);
            dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), post.getGroupId().toString()));
            return dto;
        });
    }

    @Override
    public void markReportsAsResolved(String postId) {
        Query query = new Query(Criteria.where("groupPostId").is(postId));
        Update update = new Update()
        .set("resolvedAt", new Date())
        .set("isResolved", true);
        mongoTemplate.updateMulti(query, update, GroupPostReport.class);
    }

    @Override
    public java.util.Map<Integer, Integer> countPostsByMonth(int year) {
        java.util.List<GroupPostMonthCount> results = groupPostRepository.countPostsByMonth(year);
        java.util.Map<Integer, Integer> monthCount = new java.util.HashMap<>();
        for (int i = 1; i <= 12; i++) monthCount.put(i, 0);
        for (GroupPostMonthCount item : results) {
            monthCount.put(item.getMonth(), item.getCount());
        }
        return monthCount;
    }

    @Override
    public List<GroupPostResponseDTO> getTop5MostUpvotedPosts() {
        List<GroupPost> topPosts = groupPostRepository.findTop5MostUpvotedPosts();
        return topPosts.stream()
            .map(post -> {
                GroupPostResponseDTO dto = groupPostMapper.toGroupPostResponseDTO(post);
                dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), post.getGroupId().toString()));
                return dto;
            })
            .toList();
    }

    @Override
    public List<GroupPostResponseDTO> getTrendingPostsInWeek(Date startDate, Date endDate) {
        List<GroupPost> trendingPosts = groupPostRepository.findTrendingPostsInWeek(startDate, endDate);
        return trendingPosts.stream()
            .map(post -> {
                GroupPostResponseDTO dto = groupPostMapper.toGroupPostResponseDTO(post);
                dto.setOwner(userMapper.toUserDTOWithGroupBadge(post.getOwner(), post.getGroupId().toString()));
                return dto;
            })
            .toList();
    }
}