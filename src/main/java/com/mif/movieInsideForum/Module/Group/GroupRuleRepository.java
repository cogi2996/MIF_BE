package com.mif.movieInsideForum.Module.Group;

import com.mif.movieInsideForum.Collection.GroupRule;

import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface GroupRuleRepository extends MongoRepository<GroupRule, ObjectId> {
}