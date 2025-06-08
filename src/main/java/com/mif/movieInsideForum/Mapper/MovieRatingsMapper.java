package com.mif.movieInsideForum.Mapper;

import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieRatings;
import com.mif.movieInsideForum.DTO.MovieRatingsRequestDTO;
import com.mif.movieInsideForum.DTO.MovieRatingsResponseDTO;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MovieRatingsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "sentiment", ignore = true)
    @Mapping(target = "positiveScore", ignore = true)
    @Mapping(target = "negativeScore", ignore = true)
    @Mapping(target = "neutralScore", ignore = true)
    @Mapping(target = "mixedScore", ignore = true)
    MovieRatings toEntity(MovieRatingsRequestDTO dto);

    MovieRatingsResponseDTO toDto(MovieRatings entity);

//    @AfterMapping
//    default void setUserFields(@MappingTarget MovieRatingsResponseDTO dto, MovieRatings entity) {
//        if (entity.getUser() != null) {
//            dto.set(entity.getUser().getId());
//        }
//    }

//    @Named("fromId")
//    default User fromId(String id) {
//        if (id == null) {
//            return null;
//        }
//        return User.builder()
//                .id(new ObjectId(id))
//                .build();
//    }
} 