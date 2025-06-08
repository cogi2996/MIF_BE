package com.mif.movieInsideForum.Module.Actor;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Logger;

import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.mif.movieInsideForum.Collection.Actor;
import com.mif.movieInsideForum.Collection.FavoriteActor;
import com.mif.movieInsideForum.Module.Movie.Movie;
import com.mif.movieInsideForum.DTO.ActorRequestDTO;
import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.ModelMapperUtil.ActorConverter;
import com.mif.movieInsideForum.ModelMapperUtil.MovieConverter;
import com.mif.movieInsideForum.Module.MovieRating.repository.MovieRatingsRepository;
import com.mif.movieInsideForum.Module.User.UserRepository;
import com.mif.movieInsideForum.Module.Movie.MovieRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActorServiceImpl implements ActorService {
    private final ActorRepository actorRepository;
    private final ActorConverter actorConverter;
    private final MovieRatingsRepository movieRatingsRepository;
    private final FavoriteActorRepository favoriteActorRepository;
    private final UserRepository userRepository;
    private final MovieConverter movieConverter;
    private final AtomicBoolean initialized = new AtomicBoolean(false);
    private final MovieRepository movieRepository;

    private static final Logger logger = Logger.getLogger(ActorServiceImpl.class.getName());

//    @PostConstruct
//    public void onStartup() {
//        if (initialized.compareAndSet(false, true)) {
//            initializeActorRankings();
//        }
//    }

    public void initializeActorRankings() {
        long now = System.currentTimeMillis() / 1000;
        logger.info("Initializing actor rankings - " + now);
        updateActorRankingsAsync();
    }

    @Scheduled(cron = "0 0 0 1 * ?", zone = "UTC")
    public void updateMonthlyActorRankings() {
        long now = System.currentTimeMillis() / 1000;
        logger.info("Updating actor rankings - " + now);
        updateActorRankingsAsync();
    }

    @Async("taskExecutor")
    public void updateActorRankingsAsync() {
        int page = 0;
        int size = 100;
        Pageable pageable = PageRequest.of(page, size);
        Page<Actor> actorPage;
        do {
            actorPage = actorRepository.findAll(pageable);
            for (Actor actor : actorPage.getContent()) {
                List<ObjectId> movieIds = actor.getFilmography().stream().map(Movie::getId).toList();
                Double optAverageRating = movieRatingsRepository.getSumRatingByMovieIds(movieIds).orElse(0.0);
                actor.setPreviousScoreRank(actor.getScoreRank());
                actor.setScoreRank(1 * 0.7 + optAverageRating * 0.3);
                actorRepository.save(actor);
            }
            pageable = actorPage.nextPageable();
        } while (actorPage.hasNext());
    }

    @Override
    public ActorResponseDTO createActor(ActorRequestDTO actorRequestDTO) {
        logger.info("createActor called");
        Actor actor = actorConverter.convertToEntity(actorRequestDTO);
        Actor savedActor = actorRepository.save(actor);
        Optional<Actor> retrievedActor = actorRepository.findById(savedActor.getId());
        ActorResponseDTO actorResponseDTO = retrievedActor.map(actorConverter::convertToDTO).orElse(null);
        logger.info("actorMovieDTO: " + actorResponseDTO);
        return actorResponseDTO;
    }

    @Override
    public ActorResponseDTO getActorById(ObjectId id) {
        logger.info("getActorById called");
        Optional<Actor> actor = actorRepository.findById(id);
        return actor.map(actorConverter::convertToDTO).orElse(null);
    }

    @Override
    public Slice<ActorResponseDTO> findAllWithPag(Pageable pageable) {
        Slice<Actor> actors = actorRepository.findAllWithPag(pageable);
        return actors.map(actorConverter::convertToDTO);
    }

    @Override
    public ActorResponseDTO updateActor(ObjectId id, ActorRequestDTO actorRequestDTO) {
        logger.info("updateActor called");
        Optional<Actor> actorOptional = actorRepository.findById(id);
        if (actorOptional.isEmpty()) {
            return null;
        }
        Actor actor = actorConverter.convertToEntity(actorRequestDTO);
        actor.setId(id);
        Actor updatedActor = actorRepository.save(actor);
        return actorConverter.convertToDTO(updatedActor);
    }

    @Override
    public void deleteActor(ObjectId id) {
        logger.info("deleteActor called");
        
        // Retrieve the actor to be deleted
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Actor not found with id: " + id));
        
        // Get the list of movies where this actor is part of the cast
        List<Movie> movies = movieRepository.findAllByCastContains(actor);
        
        // Remove the actor from the cast of each movie
        for (Movie movie : movies) {
            movie.getCast().remove(actor);
            // Update the filmography of the actor
            updateActorFilmography(movie, List.of(actor.getId()), true);
            movieRepository.save(movie); // Save the updated movie
        }
        
        // Finally, delete the actor
        actorRepository.deleteById(id);
    }

    @Override
    public Slice<ActorResponseDTO> getTopActors(int page, int size) {
        logger.info("getTopActors called");
        Slice<Actor> topActors = actorRepository.findAllByOrderByScoreRankDesc(PageRequest.of(page, size));
        return topActors.map(actorConverter::convertToDTO);
    }

    @Override
    public List<MovieResponseDTO> getActorFilmography(ObjectId actorId) {
        Optional<Actor> actorOpt = actorRepository.findById(actorId);
        if (actorOpt.isPresent()) {
            List<Movie> filmography = actorOpt.get().getFilmography();
            return filmography.stream().map(movieConverter::convertToDTO).toList();
        } else {
            return List.of();
        }
    }

    @Override
    public boolean isActorFavorite(ObjectId userId, ObjectId actorId) {
        return favoriteActorRepository.findByUserIdAndActorId(userId, actorId).isPresent();
    }

    @Override
    public void addFavoriteActor(ObjectId userId, ObjectId actorId) {
        Optional<Actor> actorOpt = actorRepository.findById(actorId);
        if (actorOpt.isEmpty()) {
            throw new IllegalArgumentException("Actor not found");
        }

        if (!isActorFavorite(userId, actorId)) {
            FavoriteActor favoriteActor = FavoriteActor.builder()
                    .userId(userId)
                    .actorId(actorId)
                    .build();
            favoriteActorRepository.save(favoriteActor);

            Actor actor = actorOpt.get();
            actor.setFavoriteCount(actor.getFavoriteCount() + 1);
            actorRepository.save(actor);
        }
    }

    @Override
    @Transactional
    public void removeFavoriteActor(ObjectId userId, ObjectId actorId) {
        Optional<Actor> actorOpt = actorRepository.findById(actorId);
        if (actorOpt.isEmpty()) {
            throw new IllegalArgumentException("Actor not found");
        }
        favoriteActorRepository.deleteByUserIdAndActorId(userId, actorId);

        Actor actor = actorOpt.get();
        actor.setFavoriteCount(actor.getFavoriteCount() - 1);
        actorRepository.save(actor);
    }

    @Override
    public Slice<ActorResponseDTO> getFavoriteActorsByUserId(ObjectId userId, Pageable pageable) {
        Slice<FavoriteActor> favoriteActors = favoriteActorRepository.findAllByUserIdWithPage(userId, pageable);
        return favoriteActors.map(favoriteActor -> actorRepository.findById(favoriteActor.getActorId())
                .map(actorConverter::convertToDTO)
                .orElse(null));
    }

    @Override
    public Slice<ActorResponseDTO> findActorsByTitle(String title, Pageable pageable) {
        Slice<Actor> actors = actorRepository.findByNameContainingIgnoreCase(title, pageable);
        return actors.map(actorConverter::convertToDTO);
    }

    @Override
    public Page<ActorResponseDTO> getAllActorsAsPage(Pageable pageable) {
        Page<Actor> actors = actorRepository.findAll(pageable);
        return new PageImpl<>(actors.getContent().stream().map(actorConverter::convertToDTO).toList(), pageable, actors.getTotalElements());
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
}