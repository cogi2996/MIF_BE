// package com.mif.movieInsideForum.Module.Movie;

// import com.fasterxml.jackson.databind.ObjectMapper;
// import com.mif.movieInsideForum.Module.Movie.dto.MovieRequestDTO;
// import lombok.RequiredArgsConstructor;
// import lombok.extern.slf4j.Slf4j;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.core.io.ClassPathResource;
// import org.springframework.stereotype.Component;

// import java.io.IOException;
// import java.util.Arrays;
// import java.util.List;

// @Slf4j
// @Component
// @RequiredArgsConstructor
// public class MovieDataInitializer implements CommandLineRunner {

//     private final MovieService movieService;
//     private final ObjectMapper objectMapper;

//     @Override
//     public void run(String... args) throws Exception {
//         try {
//             // Đọc file JSON
//             ClassPathResource resource = new ClassPathResource("movies.json");
//             MovieRequestDTO[] movies = objectMapper.readValue(resource.getInputStream(), MovieRequestDTO[].class);
            
//             // Tạo phim từ dữ liệu JSON
//             for (MovieRequestDTO movie : movies) {
//                 try {
//                     movieService.createMovie(movie);
//                     log.info("Created movie: {}", movie.getTitle());
//                 } catch (Exception e) {
//                     log.error("Error creating movie {}: {}", movie.getTitle(), e.getMessage());
//                 }
//             }
            
//             log.info("Successfully initialized {} movies", movies.length);
//         } catch (IOException e) {
//             log.error("Error reading movies.json: {}", e.getMessage());
//         }
//     }
// } 