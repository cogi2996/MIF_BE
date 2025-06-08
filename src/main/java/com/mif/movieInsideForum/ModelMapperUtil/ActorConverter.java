package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Collection.Actor;
import com.mif.movieInsideForum.Module.Movie.Movie;
import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.DTO.ActorRequestDTO;
import com.mif.movieInsideForum.Module.Actor.ActorRepository;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ActorConverter {
    private final ModelMapper mapper;
    private final ActorRepository actorRepository;
    private final AuthenticationFacade authenticationFacade;

    public ActorResponseDTO convertToDTO(Actor actor) {


//        Converter<String, Double> userRatingConverter = context -> {
//            String actorId = context.getSource();
//            String userId = getCurrentUserId();
//            Optional<Double> userRating = actorRatingsRepository.getUserRating(actorId, userId);
//            return userRating.orElse(-1.0);
//        };
        if (this.mapper.getTypeMap(Actor.class, ActorResponseDTO.class) == null) {
            this.mapper.typeMap(Actor.class, ActorResponseDTO.class).addMappings(mapper -> {
            });
        }
        return mapper.map(actor, ActorResponseDTO.class);
    }

    private ObjectId getCurrentUserId() {
        return authenticationFacade.getUser().getId();
    }


    public Actor convertToEntity(ActorRequestDTO actorRequestDTO) {
        Converter<List<ObjectId>, List<Movie>> filmographyConverter = context -> {
            List<ObjectId> source = context.getSource();
            if (source == null) {
                return List.of(); // Return an empty list if the source is null
            }
            return source.stream().map(id -> Movie.builder().id(id).build()).toList();
        };

        this.mapper.typeMap(ActorRequestDTO.class, Actor.class).addMappings(mapper -> {
            mapper.using(filmographyConverter).map(ActorRequestDTO::getFilmographyIds, Actor::setFilmography);
        });

        return mapper.map(actorRequestDTO, Actor.class);
    }


}