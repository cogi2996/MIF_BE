package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.Director;
import com.mif.movieInsideForum.DTO.DirectorRequestDTO;
import com.mif.movieInsideForum.DTO.DirectorResponseDTO;
import com.mif.movieInsideForum.ModelMapperUtil.DirectorConverter;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
@RequiredArgsConstructor
public class DirectorServiceImpl implements DirectorService {
    private final DirectorRepository directorRepository;
    private final DirectorConverter directorConverter;
    private static final Logger logger = Logger.getLogger(DirectorServiceImpl.class.getName());

    @Override
    public DirectorResponseDTO createDirector(DirectorRequestDTO directorRequestDTO) {
        logger.info("createDirector called");
        Director director = directorConverter.convertToEntity(directorRequestDTO);
        Director savedDirector = directorRepository.save(director);
        return directorConverter.convertToDTO(savedDirector);
    }

    @Override
    public DirectorResponseDTO getDirectorById(ObjectId id) {
        logger.info("getDirectorById called");
        Optional<Director> director = directorRepository.findById(id);
        return director.map(directorConverter::convertToDTO).orElse(null);
    }

    @Override
    public List<DirectorResponseDTO> getAllDirectors() {
        logger.info("getAllDirectors called");
        List<Director> directors = directorRepository.findAll();
        return directors.stream().map(directorConverter::convertToDTO).toList();
    }

    @Override
    public DirectorResponseDTO updateDirector(ObjectId id, DirectorRequestDTO directorRequestDTO) {
        logger.info("updateDirector called");
        Optional<Director> directorOptional = directorRepository.findById(id);
        if (directorOptional.isEmpty()) {
            return null;
        }
        Director director = directorConverter.convertToEntity(directorRequestDTO);
        director.setId(id);
        Director updatedDirector = directorRepository.save(director);
        return directorConverter.convertToDTO(updatedDirector);
    }

    @Override
    public void deleteDirector(ObjectId id) {
        logger.info("deleteDirector called");
        directorRepository.deleteById(id);
    }
    @Override
    public Slice<DirectorResponseDTO> findByNameContainingIgnoreCase(String name, Pageable pageable) {
        Slice<Director> directors = directorRepository.findByNameContainingIgnoreCase(name, pageable);
        return directors.map(directorConverter::convertToDTO);
    }
}