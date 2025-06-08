package com.mif.movieInsideForum.ModelMapperUtil;

import com.mif.movieInsideForum.Collection.Director;
import com.mif.movieInsideForum.Module.Movie.Movie;
import com.mif.movieInsideForum.DTO.DirectorRequestDTO;
import com.mif.movieInsideForum.DTO.DirectorResponseDTO;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class DirectorConverter {
    private final ModelMapper mapper;

    public DirectorResponseDTO convertToDTO(Director director) {
//        Converter<List<Movie>, List<String>> filmographyConverter = context -> {
//            List<Movie> source = context.getSource();
//            if (source == null) {
//                return List.of(); // Return an empty list if the source is null
//            }
//            return source.stream().map(Movie::getId).toList();
//        };
//
//        if (this.mapper.getTypeMap(Director.class, DirectorResponseDTO.class) == null) {
//            this.mapper.typeMap(Director.class, DirectorResponseDTO.class).addMappings(mapper -> {
//                mapper.using(filmographyConverter).map(Director::getFilmography, DirectorResponseDTO::setFilmographyIds);
//            });
//        }
        return mapper.map(director, DirectorResponseDTO.class);
    }

    public Director convertToEntity(DirectorRequestDTO directorRequestDTO) {
        Converter<List<ObjectId>, List<Movie>> filmographyConverter = context -> {
            List<ObjectId> source = context.getSource();
            if (source == null) {
                return List.of(); // Return an empty list if the source is null
            }
            return source.stream().map(id -> Movie.builder().id(id).build()).toList();
        };

        if (this.mapper.getTypeMap(DirectorRequestDTO.class, Director.class) == null) {
            this.mapper.typeMap(DirectorRequestDTO.class, Director.class).addMappings(mapper -> {
                mapper.using(filmographyConverter).map(DirectorRequestDTO::getFilmographyIds, Director::setFilmography);
            });
        }

        return mapper.map(directorRequestDTO, Director.class);
    }
}