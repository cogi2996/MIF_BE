package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.DTO.DirectorRequestDTO;
import com.mif.movieInsideForum.DTO.DirectorResponseDTO;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface DirectorService {
    DirectorResponseDTO createDirector(DirectorRequestDTO directorRequestDTO);
    DirectorResponseDTO getDirectorById(ObjectId id);
    List<DirectorResponseDTO> getAllDirectors();
    DirectorResponseDTO updateDirector(ObjectId id, DirectorRequestDTO directorRequestDTO);
    void deleteDirector(ObjectId id);
    Slice<DirectorResponseDTO> findByNameContainingIgnoreCase(String name, Pageable pageable);

}