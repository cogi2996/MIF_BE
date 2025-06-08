package com.mif.movieInsideForum.Module.User;

import com.mif.movieInsideForum.Collection.User;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, ObjectId> {
    Optional<User> findByEmail(String email);
    //exist by email
    Boolean existsByEmail(String email);



    @Aggregation(pipeline = {
            "{ $match: { 'displayName': { $regex: ?0, $options: 'i' } } }",
            "{ $lookup: { from: 'groups', localField: '_id', foreignField: 'members.userId', as: 'groups' } }",
            "{ $match: { 'groups._id': ?1 } }",
            "{ $skip: ?#{#pageable.offset} }",
            "{ $limit: ?#{#pageable.pageSize} }"
    })
    Slice<User> findByNameAndGroupId(String name, ObjectId groupId, Pageable pageable);

    // get total


//    long count();

    @Aggregation(pipeline = {
        "{ $match: { $expr: { $eq: [{ $year: '$createdAt' }, ?0] } } }",
        "{ $group: { _id: { month: { $month: '$createdAt' } }, count: { $sum: 1 } } }",
        "{ $sort: { '_id.month': 1 } }",
        "{ $project: { month: '$_id.month', count: 1, _id: 0 } }"
    })
    List<UserMonthCount> countUsersByMonth(int year);

}
