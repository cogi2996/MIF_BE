package com.mif.movieInsideForum.Module.Actor;

import com.mif.movieInsideForum.Collection.FavoriteActor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.Optional;

public interface FavoriteActorRepository extends MongoRepository<FavoriteActor, ObjectId> {
    Optional<FavoriteActor> findByUserIdAndActorId(ObjectId userId, ObjectId actorId);
    void deleteByUserIdAndActorId(ObjectId userId, ObjectId actorId);
    @Query("{userId: ?0}")
    Slice<FavoriteActor> findAllByUserIdWithPage(ObjectId userId, Pageable pageable);
}