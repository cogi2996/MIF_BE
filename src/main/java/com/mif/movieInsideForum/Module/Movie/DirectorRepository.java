package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.Director;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DirectorRepository extends MongoRepository<Director, ObjectId> {

    // find by name
    Slice<Director> findByNameContainingIgnoreCase(String name, Pageable pageable);

}