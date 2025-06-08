package com.mif.movieInsideForum.Module.Actor;

import java.util.List;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import com.mif.movieInsideForum.DTO.ActorRequestDTO;
import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;

public interface ActorService {
    ActorResponseDTO createActor(ActorRequestDTO actor);
    ActorResponseDTO getActorById(ObjectId id);
    ActorResponseDTO updateActor(ObjectId id, ActorRequestDTO actorRequestDTO);
    void deleteActor(ObjectId id);
    void updateMonthlyActorRankings();
    Slice<ActorResponseDTO> getTopActors(int page, int size);
    List<MovieResponseDTO> getActorFilmography(ObjectId actorId);
    Slice<ActorResponseDTO> findAllWithPag(Pageable pageable);
    boolean isActorFavorite(ObjectId userId, ObjectId actorId);
    void addFavoriteActor(ObjectId userId, ObjectId actorId);
    void removeFavoriteActor(ObjectId userId, ObjectId actorId);
    Slice<ActorResponseDTO> getFavoriteActorsByUserId(ObjectId userId, Pageable pageable);
    Slice<ActorResponseDTO> findActorsByTitle(String title, Pageable pageable);
    Page<ActorResponseDTO> getAllActorsAsPage(Pageable pageable);

}