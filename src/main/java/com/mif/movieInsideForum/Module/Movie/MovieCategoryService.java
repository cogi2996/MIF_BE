package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.MovieCategory;
import org.bson.types.ObjectId;

import java.util.List;

public interface MovieCategoryService {
    List<MovieCategory> getNewsCategories();
    MovieCategory getNewsCategory(ObjectId id);
    MovieCategory createNewsCategory(MovieCategory movieCategory);
    MovieCategory updateNewsCategory(ObjectId id, MovieCategory movieCategory);
    void deleteNewsCategory(ObjectId id);
}