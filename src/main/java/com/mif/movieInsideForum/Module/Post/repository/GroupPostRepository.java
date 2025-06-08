package com.mif.movieInsideForum.Module.Post.repository;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.Update;

import com.mif.movieInsideForum.Module.Post.entity.GroupPost;
import com.mif.movieInsideForum.DTO.GroupPostCount;

public interface GroupPostRepository extends MongoRepository<GroupPost, ObjectId> {
    @Query("{ 'isBlock': ?0 }")
    Slice<GroupPost> findAllByIsBlock(boolean isBlock, Pageable pageable);

    @Aggregation(
            pipeline = {
                    "{$match: {group : ?0, isBlock: ?1}}",
                    "{ $addFields: { totalVotes: " +
                            "{ $sum: { $map: { input: { $objectToArray: '$userVotes' }, " +
                            "as: 'vote', " +
                            "in: { $cond: [ { $eq: [{ $strcasecmp: ['$$vote.v', 'UPVOTE'] }, 0] }, 1, 0 ] } } } } } }",
                    "{ $sort: { totalVotes: -1 } }",}
    )
    Slice<GroupPost> findFeaturedPosts(Pageable pageable, ObjectId groupId, boolean includeBlocked);

    @Query(value = "{ 'owner.id' : ?0, $or: [{ 'isBlock': false }, { $expr: { $eq: [?1, true] } }] }", sort = "{ 'createdAt' : -1 }")
    Slice<GroupPost> findProfilePostWithBlockOption(ObjectId userId, boolean includeBlocked, Pageable pageable);

    @Query(value = "{ 'group.$id': ?0, $or: [{ 'isBlock': false }, { $expr: { $eq: [?1, true] } }] }", sort = "{ 'createdAt' : -1 }")
    Slice<GroupPost> findByGroupIdWithBlockOption(ObjectId groupId, boolean includeBlocked, Pageable pageable);

    @Query(value = "{ 'group.$id': ?0 , 'isBlock': ?1 }")
    Slice<GroupPost> findByGroupId(ObjectId groupId, Pageable pageable, boolean includeBlocked);

    @Query(value = "{ 'group.$id': ?0, 'createdAt': { $gte: ?1, $lte: ?2 }, 'isBlock': ?3 }", count = true)
    Long countPostsForCurrentWeek(ObjectId groupId, Date startDate, Date endDate, boolean includeBlocked);

    @Aggregation(pipeline = {
            "{ $match: { 'group.$id': { $in: ?0 }, 'createdAt': { $gte: ?1, $lte: ?2 }, 'isBlock': ?3 } }",
            "{ $group: { _id: '$group.$id', count: { $sum: 1 } } }",
            "{ $project: { groupId: '$_id', count: 1 } }"
    })
    List<GroupPostCount> countPostsForGroupsSinceDate(List<ObjectId> groupIds, Date startDate, Date endDate, boolean includeBlocked);

    // detete by groupid
    @Query(value = "{ 'group': ?0 }", delete = true)
    void deleteByGroupId(ObjectId groupId);

    // update status by postid
    @Query(value = "{ 'id': ?0 }")
    @Update("{ 'isBlock': ?1 }")        
    void updateStatusByPostId(ObjectId postId, boolean isBlock);

    @Aggregation(pipeline = {
        "{ $match: { $expr: { $eq: [{ $year: '$createdAt' }, ?0] } } }",
        "{ $group: { _id: { month: { $month: '$createdAt' } }, count: { $sum: 1 } } }",
        "{ $sort: { '_id.month': 1 } }",
        "{ $project: { month: '$_id.month', count: 1, _id: 0 } }"
    })
    List<GroupPostMonthCount> countPostsByMonth(int year);

    // Lấy top 5 bài viết được upvote nhiều nhất
    @Aggregation(pipeline = {
        "{ $match: { 'isBlock': false } }",
        "{ $sort: { 'ratingCount': -1 } }",
        "{ $limit: 5 }"
    })
    List<GroupPost> findTop5MostUpvotedPosts();

    // Lấy trending posts trong tuần
    @Aggregation(pipeline = {
        "{ $match: { " +
            "'isBlock': false, " +
            "'createdAt': { $gte: ?0, $lte: ?1 } " + // Trong khoảng thời gian từ startDate đến endDate
        "} }",
        "{ $sort: { 'ratingCount': -1 } }", // Sắp xếp theo số lượng upvote giảm dần
        "{ $limit: 5 }"
    })
    List<GroupPost> findTrendingPostsInWeek(Date startDate, Date endDate);
}