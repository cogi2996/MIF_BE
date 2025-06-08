package com.mif.movieInsideForum.Module.Group;

import com.mif.movieInsideForum.Collection.Group;
import com.mif.movieInsideForum.DTO.GroupStatus;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GroupRepository extends MongoRepository<Group, ObjectId> {
    Group findByGroupName(String groupName);

    @Query(value = "{ 'owner.id' : ?0 }")
    Slice<Group> findByOwnerId(ObjectId ownerId, Pageable pageable);

    @Query(value = "{ 'members.userId' : ?0 }")
    Slice<Group> findByMemberId(ObjectId memberId, Pageable pageable);

    @Query(value = "{ 'members.userId' : { $nin: [?0] }, 'pendingInvitations.id' : { $nin: [?0]} }")
    Slice<Group> findGroupUserNotJoin(ObjectId userId, Pageable pageable);

    Slice<Group> findByGroupNameContainingIgnoreCase(String groupName, Pageable pageable);

    // batch check status of user in groups
    // status = 0: not join, status = 1: joined, status = 2: pending
    @Aggregation(
            pipeline = {
                    "{$match: { '_id': { $in: ?1 } }}",  // Lọc các group dựa trên danh sách groupIds được truyền vào
                    "{$project: { " +
                            "groupId: '$_id', " +  // Lấy _id của group hiện tại và đặt nó thành groupId
                            "members: '$members.userId', " + // Lấy danh sách userId trong members
                            "pendingInvitations: { " +
                            "$map: { " +
                            "input: '$pendingInvitations', " +  // Lấy danh sách pendingInvitations
                            "as: 'invitation', " +
                            "in: '$$invitation.$id' " +  // Trích xuất chỉ trường $id từ mỗi DBRef
                            "} " +
                            "} " +
                            "}}",
                    "{$project: { " +
                            "groupId: 1, " +
                            "status: { $switch: { " +
                            "branches: [ " +
                            "{ case: { $in: [ ?0, '$members'] }, then: 'JOINED' }, " +  // Kiểm tra xem user có trong danh sách members không
                            "{ case: { $in: [ ?0, '$pendingInvitations'] }, then: 'PENDING' } " +  // Kiểm tra xem user có trong danh sách pendingInvitations không
                            "], " +
                            "default: 'NOT_JOIN' " +
                            "} } " +
                            "} }"
            }
    )
    List<GroupStatus> findUserStatusInGroups(ObjectId userId, List<ObjectId> groupIds);

    @Query(value = "{ '_id': ?0, 'pendingInvitations.id': ?1 }", exists = true)
    boolean existsByIdAndPendingInvitations_Id(ObjectId groupId, ObjectId userId);

    @Query(value = "{ '_id': ?0, $or: [ { 'owner': ?1 }, { 'pendingInvitations': ?1 } ] }",
            exists = true)
    Boolean existsByCanUserRemovePendingInvitation(ObjectId groupId, ObjectId userId);

    @Query("{ 'members.userId': ?0, '_id': { $in: ?1 } }")
    List<Group> findJoinedGroupIdsByUserId(ObjectId userId, List<ObjectId> groupIds);

}