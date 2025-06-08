package com.mif.movieInsideForum.Module.Movie;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import com.mif.movieInsideForum.Collection.Field.MovieType;
import com.mif.movieInsideForum.Module.Ai.MovieSearchService;
import com.mif.movieInsideForum.Module.File.FileService;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mif.movieInsideForum.Collection.Actor;
import com.mif.movieInsideForum.Collection.Director;
import com.mif.movieInsideForum.Collection.MovieCategory;
import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieRequestDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieUpdateDTO;
import com.mif.movieInsideForum.ModelMapperUtil.ActorConverter;
import com.mif.movieInsideForum.ModelMapperUtil.MovieConverter;
import com.mif.movieInsideForum.Module.Actor.ActorRepository;

import lombok.RequiredArgsConstructor;

@Slf4j
@Service
@RequiredArgsConstructor
public class MovieServiceImpl implements MovieService {
    private final MovieRepository movieRepository;
    private final MovieConverter movieConverter;
    private final ActorRepository actorRepository;
    private final ActorConverter actorConverter;
    private final DirectorRepository directorRepository;
    private final SavedMovieRepository savedMovieRepository;
    private final FileService fileService;
    
    @Autowired
    private MovieSearchService movieSearchService;

    @Override
    @Transactional
//    @CachePut(value = "movies", key = "#result.id")
    public MovieResponseDTO createMovie(MovieRequestDTO movieRequest) {
        Integer totalEpisodes = movieRequest.getTotalEpisodes();
        if (movieRequest.getMovieType().equals(MovieType.SERIES) && (totalEpisodes == null || totalEpisodes <= 1)) {
            throw new IllegalArgumentException("Total episodes must be greater than 1 for TV series.");
        }
        if (movieRequest.getMovieType().equals(MovieType.SINGLE) && totalEpisodes != null) {
            throw new IllegalArgumentException("Total episodes must be null for single movies.");
        }
        Movie movie = movieConverter.convertToEntity(movieRequest);
        movie.setCreatedAt(new Date());
        Movie savedMovie = movieRepository.save(movie);
        Movie savedMovieWithId = movieRepository.findById(savedMovie.getId()).orElseThrow();
        List<ObjectId> castIds = savedMovie.getCast().stream().map(Actor::getId).toList();
        List<ObjectId> directorIds = savedMovieWithId.getDirector().stream().map(Director::getId).toList();
        updateDirectorFilmography(savedMovie, directorIds, false);
        updateActorFilmography(savedMovie, castIds, false);
        
        // Tạo embedding và lưu vào vector store
        try {
            List<Double> embedding = movieSearchService.createMovieEmbedding(savedMovieWithId);
            savedMovieWithId.setEmbed(embedding);
            movieRepository.save(savedMovieWithId);
            
            // Lưu vào vector store để tìm kiếm
            movieSearchService.saveMovieToVectorStore(savedMovieWithId, embedding);
            log.info("Đã tạo embedding và lưu vào vector store cho phim: {}", savedMovieWithId.getTitle());
        } catch (Exception e) {
            log.error("Không thể tạo embedding cho phim: " + e.getMessage());
        }
        
        return movieConverter.convertToDTO(savedMovieWithId);
    }

    @Override
    public MovieResponseDTO getMovieById(ObjectId id) {
        Optional<Movie> movie = movieRepository.findById(id);
        return movie.map(movieConverter::convertToDTO).orElse(null);
    }

    @Override
    public Slice<MovieResponseDTO> getAllMovies(Pageable pageable) {
        return movieRepository.findAllWithPag(pageable).map(movieConverter::convertToDTO);
    }

    @Override
    @Transactional
//    @CacheEvict(value = "movies", key = "#id")
    public void deleteMovie(ObjectId id) {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        savedMovieRepository.deleteByMovieId(id);
        List<ObjectId> actorIds = existingMovie.getCast().stream()
                .map(Actor::getId)
                .toList();
        updateActorFilmography(existingMovie, actorIds, true);
        movieRepository.deleteById(id);
    }

    @Override
    public Slice<MovieResponseDTO> getLatestMovie(Pageable pageable) {
        return movieRepository.findAllByOrderByReleaseDateDesc(pageable).map(movieConverter::convertToDTO);
    }

    @Override
    public List<MovieResponseDTO> get4RandomMovies() {
        List<Movie> movies = movieRepository.findAll();
        Collections.shuffle(movies, new Random());
        return movies.subList(0, Math.min(4, movies.size())).stream().map(movieConverter::convertToDTO).toList();
    }

    @Override
    public Slice<MovieResponseDTO> findMoviesByTitle(String title, Pageable pageable) {
        return movieRepository.findByTitleContainingIgnoreCase(title, pageable).map(movieConverter::convertToDTO);
    }


    @Override
    @Transactional
//    @CacheEvict(value = "movies", key = "#id")
    public List<ActorResponseDTO> addCast(ObjectId id, List<ObjectId> actorIds) {
        Movie existingMovie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        List<ObjectId> existingActorIds = movieRepository.findExistingActorIdsInCast(id, actorIds);
        if (!existingActorIds.isEmpty()) {
            throw new IllegalArgumentException("The following actors are already part of the cast: " + existingActorIds);
        }
        List<Actor> newActors = actorRepository.findAllById(actorIds);
        if (newActors.size() != actorIds.size()) {
            throw new IllegalArgumentException("One or more actor IDs are invalid.");
        }
        existingMovie.getCast().addAll(newActors);
        updateActorFilmography(existingMovie, actorIds, false);
        movieRepository.save(existingMovie);
        return newActors.stream().map(actorConverter::convertToDTO).toList();
    }

    @Override
    @Transactional
//    @CacheEvict(value = "movies", key = "#id")
    public Boolean removeCast(ObjectId id, List<ObjectId> actorIds) {
        Optional<Movie> existingMovieOpt = movieRepository.findById(id);
        if (existingMovieOpt.isPresent()) {
            Movie existingMovie = existingMovieOpt.get();
            List<Actor> actorsToRemove = actorRepository.findAllById(actorIds);
            List<Actor> currentCast = existingMovie.getCast();
            boolean removed = currentCast.removeAll(actorsToRemove);
            if (removed) {
                existingMovie.setCast(currentCast);
                updateActorFilmography(existingMovie, actorIds, true);
                movieRepository.save(existingMovie);
            }
            return removed;
        } else {
            return false;
        }
    }

    @Override
    public List<ActorResponseDTO> getMovieCast(ObjectId movieId) {
        Optional<Movie> movieOpt = movieRepository.findById(movieId);
        if (movieOpt.isPresent()) {
            List<Actor> cast = movieOpt.get().getCast();
            return cast.stream().map(actorConverter::convertToDTO).toList();
        } else {
            return List.of();
        }
    }

    @Override
//    @CachePut(value = "movies", key = "#id")
    public boolean updateMovie(ObjectId id, MovieUpdateDTO movieUpdateDTO) {
        Optional<Movie> existingMovieOpt = movieRepository.findById(id);
        if (existingMovieOpt.isPresent()) {
            Movie existingMovie = existingMovieOpt.get();
            updateMovieFields(existingMovie, movieUpdateDTO);
            Movie updatedMovie = movieRepository.save(existingMovie);
            
            // Cập nhật embedding nếu title hoặc description thay đổi
            if (movieUpdateDTO.getTitle() != null || movieUpdateDTO.getDescription() != null) {
                try {
                    List<Double> newEmbedding = movieSearchService.createMovieEmbedding(updatedMovie);
                    updatedMovie.setEmbed(newEmbedding);
                    movieRepository.save(updatedMovie);
                    
                    // Cập nhật trong vector store
                    movieSearchService.saveMovieToVectorStore(updatedMovie, newEmbedding);
                    log.info("Đã cập nhật embedding cho phim: {}", updatedMovie.getTitle());
                } catch (Exception e) {
                    log.error("Không thể cập nhật embedding cho phim: " + e.getMessage());
                }
            }
            
            return true;
        } else {
            return false;
        }
    }

    private void updateMovieFields(Movie existingMovie, MovieUpdateDTO movieUpdateDTO) {
        if (movieUpdateDTO.getTitle() != null) {
            existingMovie.setTitle(movieUpdateDTO.getTitle());
        }
        if (movieUpdateDTO.getDescription() != null) {
            existingMovie.setDescription(movieUpdateDTO.getDescription());
        }
        if (movieUpdateDTO.getReleaseDate() != null) {
            existingMovie.setReleaseDate(movieUpdateDTO.getReleaseDate());
        }
        if (movieUpdateDTO.getGenreIds() != null) {
            existingMovie.setGenre(movieUpdateDTO.getGenreIds().stream().map(id -> MovieCategory.builder().id(id).build()).toList());
        }
        if (movieUpdateDTO.getPosterUrl() != null) {
            existingMovie.setPosterUrl(movieUpdateDTO.getPosterUrl());
        }
        if (movieUpdateDTO.getTrailerUrl() != null) {
            existingMovie.setTrailerUrl(movieUpdateDTO.getTrailerUrl());
        }
        if (movieUpdateDTO.getDuration() != null) {
            existingMovie.setDuration(movieUpdateDTO.getDuration());
        }
        if (movieUpdateDTO.getCountry() != null) {
            existingMovie.setCountry(movieUpdateDTO.getCountry());
        }
        if (movieUpdateDTO.getBudget() != null) {
            existingMovie.setBudget(movieUpdateDTO.getBudget());
        }
        if (movieUpdateDTO.getAwards() != null) {
            existingMovie.setAwards(movieUpdateDTO.getAwards());
        }
    }

    @Transactional
//    @CachePut(value = "movies", key = "#id")
    public void updateDirectorFilmography(Movie movie, List<ObjectId> directorIds, boolean isRemove) {
        List<Director> directors = directorRepository.findAllById(directorIds);
        directors.forEach(director -> {
            if (isRemove) {
                director.getFilmography().remove(movie);
            } else {
                if (!director.getFilmography().contains(movie)) {
                    director.getFilmography().add(movie);
                }
            }
        });
        directorRepository.saveAll(directors);
    }

    public void updateActorFilmography(Movie movie, List<ObjectId> actorIds, boolean isRemove) {
        List<Actor> actors = actorRepository.findAllById(actorIds);
        actors.forEach(actor -> {
            if (isRemove) {
                actor.getFilmography().remove(movie);
            } else {
                if (!actor.getFilmography().contains(movie)) {
                    actor.getFilmography().add(movie);
                }
            }
        });
        actorRepository.saveAll(actors);
    }

    @Override
    public Slice<MovieResponseDTO> findMoviesByCategory(ObjectId categoryId, Pageable pageable) {
        return movieRepository.findByCategoryId(categoryId, pageable).map(movieConverter::convertToDTO);
    }

    @Override
    public Page<MovieResponseDTO> getAllMoviesAsPage(Pageable pageable) {
        Page<Movie> movies = movieRepository.findAll(pageable);
        return new PageImpl<>(movies.getContent().stream().map(movieConverter::convertToDTO).toList(), pageable, movies.getTotalElements());
    }

    @Override
    public Page<MovieResponseDTO> findMoviesByCategoryAsPage(ObjectId categoryId, Pageable pageable) {
        Page<Movie> movies = movieRepository.findByCategoryIdAsPage(categoryId, pageable);
        return new PageImpl<>(movies.getContent().stream().map(movieConverter::convertToDTO).toList(), pageable, movies.getTotalElements());

    }

    @Override
    public List<String> getMovieImages(ObjectId id) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        return movie.getRelatedImages();
    }

    @Override
    @Transactional
//    @CachePut(value = "movies", key = "#id")
    public void updateMovieImages(ObjectId id, List<String> newImages) {
        Movie movie = movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Movie not found with id: " + id));
        List<String> currentImages = movie.getRelatedImages();
        List<String> imagesToDelete = currentImages.stream()
                .filter(image -> !newImages.contains(image))
                .collect(Collectors.toList());
        for (String imageUrl : imagesToDelete) {
            String uniqueFileName = extractFileNameFromUrl(imageUrl);
            fileService.deleteFile(uniqueFileName);
        }
        movie.setRelatedImages(newImages);
        movieRepository.save(movie);
    }

    private String extractFileNameFromUrl(String imageUrl) {
        return imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
    }
}