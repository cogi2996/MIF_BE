package com.mif.movieInsideForum.Module.Movie;

import java.util.Date;
import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import com.mif.movieInsideForum.Collection.MovieCategory;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MovieCategoryServiceImpl implements MovieCategoryService {

    private final MovieCategoryRepository movieCategoryRepository;

    @Override
    public List<MovieCategory> getNewsCategories() {
        return movieCategoryRepository.findAll();
    }

    @Override
    public MovieCategory getNewsCategory(ObjectId id) {
        return movieCategoryRepository.findById(id).orElse(null);
    }

    @Override
    public MovieCategory createNewsCategory(MovieCategory movieCategory) {
        return movieCategoryRepository.save(movieCategory);
    }

    @Override
    public MovieCategory updateNewsCategory(ObjectId id, MovieCategory movieCategory) {
        if (movieCategoryRepository.existsById(id)) {
            MovieCategory newCategoryDB = movieCategoryRepository.findById(id).orElse(null);
            movieCategory.setId(id);
            movieCategory.setCreatedAt(newCategoryDB.getCreatedAt());
            movieCategory.setUpdatedAt(new Date());
            return movieCategoryRepository.save(movieCategory);
        }
        return null;
    }

    @Override
    public void deleteNewsCategory(ObjectId id) {
        movieCategoryRepository.deleteById(id);
    }
}