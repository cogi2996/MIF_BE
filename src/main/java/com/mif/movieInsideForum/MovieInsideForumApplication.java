package com.mif.movieInsideForum;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mif.movieInsideForum.Module.Movie.MovieService;
import com.mif.movieInsideForum.Module.Movie.dto.MovieRequestDTO;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import com.mif.movieInsideForum.Module.Movie.Movie;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;

import java.util.Arrays;

@SpringBootApplication
@EnableConfigurationProperties
@RequiredArgsConstructor
@Slf4j
public class MovieInsideForumApplication {
	
//	private final ApplicationContext applicationContext;
//	private final MongoTemplate mongoTemplate;
//
//	@Bean
//	public ObjectMapper objectMapper() {
//		return new ObjectMapper();
//	}
//
//	@Bean
//	@Lazy
//	public CommandLineRunner importMovies() {
//		return args -> {
//			try {
//				// Lấy MovieService từ context
//				MovieService movieService = applicationContext.getBean(MovieService.class);
//
//				// Đọc file JSON
//				ObjectMapper mapper = objectMapper();
//
//				ClassPathResource resource = new ClassPathResource("movies.json");
//				MovieRequestDTO[] movies = mapper.readValue(resource.getInputStream(), MovieRequestDTO[].class);
//
//				// Lưu từng phim thông qua service
//				Arrays.stream(movies).forEach(movieDTO -> {
//					try {
//						movieService.createMovie(movieDTO);
//						log.info("Đã lưu phim: {}", movieDTO.getTitle());
//					} catch (Exception e) {
//						log.error("Lỗi khi lưu phim {}: {}", movieDTO.getTitle(), e.getMessage());
//					}
//				});
//
//				log.info("Đã import thành công {} phim", movies.length);
//			} catch (Exception e) {
//				log.error("Lỗi khi import dữ liệu phim: {}", e.getMessage());
//			}
//		};
//	}

	public static void main(String[] args) {
		SpringApplication.run(MovieInsideForumApplication.class, args);
	}
}
