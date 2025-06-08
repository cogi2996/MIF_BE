package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.MovieCategory;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface MovieCategoryRepository extends MongoRepository<MovieCategory, ObjectId> {

}
