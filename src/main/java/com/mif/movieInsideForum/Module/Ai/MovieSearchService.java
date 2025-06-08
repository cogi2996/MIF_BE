package com.mif.movieInsideForum.Module.Ai;

import com.mif.movieInsideForum.Module.Movie.Movie;
import com.mif.movieInsideForum.Module.Movie.MovieRepository;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import com.mif.movieInsideForum.ModelMapperUtil.MovieConverter;
import org.bson.types.ObjectId;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieSearchService {
    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;
    private final MovieRepository movieRepository;
    private final MovieConverter movieConverter;
    private static final Logger log = LoggerFactory.getLogger(MovieSearchService.class);
    
    // Constants
    private static final int TOP_K_RESULTS = 5;
    private static final double SIMILARITY_THRESHOLD = 0.7;
    private static final String MOVIE_ID_KEY = "movieId";
    private static final String TITLE_KEY = "title";
    private static final String RELEASE_YEAR_KEY = "releaseYear";

    /**
     * Tạo embedding cho một phim
     */
    public List<Double> createMovieEmbedding(Movie movie) {
        try {
            String movieText = buildMovieText(movie);
            float[] embeddingArray = embeddingModel.embed(movieText);
            return convertToDoubleList(embeddingArray);
        } catch (Exception e) {
            log.error("Lỗi khi tạo embedding cho phim {}: {}", movie.getTitle(), e.getMessage());
            return new ArrayList<>();
        }
    }

    private String buildMovieText(Movie movie) {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Phim %s. ", movie.getTitle()));
        
        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            builder.append(String.format("Nội dung: %s. ", movie.getDescription()));
        }
        
        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            builder.append("Đạo diễn bởi ")
                   .append(movie.getDirector().stream()
                           .map(d -> d.getName())
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        if (movie.getCast() != null && !movie.getCast().isEmpty()) {
            builder.append("Với sự tham gia của các diễn viên ")
                   .append(movie.getCast().stream()
                           .map(a -> a.getName())
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            builder.append("Thuộc thể loại ")
                   .append(movie.getGenre().stream()
                           .map(g -> g.getCategoryName())
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        if (movie.getReleaseDate() != null) {
            builder.append(String.format("Phát hành năm %d. ", 
                movie.getReleaseDate().getYear() + 1900));
        }

        if (movie.getCountry() != null && !movie.getCountry().isEmpty()) {
            builder.append(String.format("Quốc gia: %s. ", movie.getCountry()));
        }

        if (movie.getDuration() != null) {
            builder.append(String.format("Thời lượng: %d phút. ", movie.getDuration()));
        }

        if (movie.getMovieType() != null) {
            builder.append(String.format("Loại phim: %s. ", movie.getMovieType()));
            if (movie.getMovieType().equals("SERIES") && movie.getTotalEpisodes() != null) {
                builder.append(String.format("Số tập: %d. ", movie.getTotalEpisodes()));
            }
        }
        
        return builder.toString();
    }

    private List<Double> convertToDoubleList(float[] array) {
        List<Double> list = new ArrayList<>();
        for (float value : array) {
            list.add((double) value);
        }
        return list;
    }
    
    /**
     * Lưu thông tin phim vào vector store
     */
    public void saveMovieToVectorStore(Movie movie, List<Double> embedding) {
        try {
            if (movie == null || embedding == null || embedding.isEmpty()) {
                log.error("Dữ liệu phim hoặc embedding không hợp lệ");
                return;
            }

            Map<String, Object> metadata = buildMovieMetadata(movie);
            String content = buildMovieContent(movie);

            // Tạo document với thông tin cơ bản
            Document document = Document.builder()
                    .text(content)
                    .metadata(metadata)
                    .build();

            // Thêm document vào vector store
            vectorStore.add(List.of(document));
            log.info("Đã lưu phim vào vector store: {}", movie.getTitle());
            
        } catch (Exception e) {
            log.error("Lỗi khi lưu phim vào vector store: {}", e.getMessage(), e);
        }
    }

    private Map<String, Object> buildMovieMetadata(Movie movie) {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("movieId", movie.getId().toHexString());
        metadata.put("title", movie.getTitle());
        metadata.put("releaseYear", movie.getReleaseDate() != null ? movie.getReleaseDate().getYear() + 1900 : null);
        metadata.put("movieType", movie.getMovieType().toString());
        metadata.put("duration", movie.getDuration());
        metadata.put("country", movie.getCountry());
        metadata.put("genres", movie.getGenre().stream()
                .map(g -> g.getCategoryName())
                .collect(Collectors.toList()));
        metadata.put("totalEpisodes", movie.getTotalEpisodes());
        metadata.put("posterUrl", movie.getPosterUrl());
        metadata.put("trailerUrl", movie.getTrailerUrl());
        
        return metadata;
    }

    private String buildMovieContent(Movie movie) {
        StringBuilder content = new StringBuilder();
        content.append(String.format("Phim: %s. ", movie.getTitle()));
        
        if (movie.getDescription() != null && !movie.getDescription().isEmpty()) {
            content.append(String.format("Mô tả: %s. ", movie.getDescription()));
        }
        
        if (movie.getDirector() != null && !movie.getDirector().isEmpty()) {
            content.append("Đạo diễn: ")
                   .append(movie.getDirector().stream()
                           .map(d -> d.getName())
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        if (movie.getCast() != null && !movie.getCast().isEmpty()) {
            content.append("Diễn viên: ")
                   .append(movie.getCast().stream()
                           .map(a -> a.getName())
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        if (movie.getGenre() != null && !movie.getGenre().isEmpty()) {
            content.append("Thể loại: ")
                   .append(movie.getGenre().stream()
                           .map(g -> g.getCategoryName())
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        if (movie.getReleaseDate() != null) {
            content.append(String.format("Phát hành năm: %d. ", 
                movie.getReleaseDate().getYear() + 1900));
        }

        if (movie.getCountry() != null && !movie.getCountry().isEmpty()) {
            content.append(String.format("Quốc gia: %s. ", movie.getCountry()));
        }

        if (movie.getDuration() != null) {
            content.append(String.format("Thời lượng: %d phút. ", movie.getDuration()));
        }

        if (movie.getMovieType() != null) {
            content.append(String.format("Loại phim: %s. ", movie.getMovieType()));
            if (movie.getMovieType().equals("SERIES") && movie.getTotalEpisodes() != null) {
                content.append(String.format("Số tập: %d. ", movie.getTotalEpisodes()));
            }
        }

        if (movie.getAwards() != null && !movie.getAwards().isEmpty()) {
            content.append("Giải thưởng: ")
                   .append(movie.getAwards().stream()
                           .map(a -> a.getName() + " (" + a.getDate() + ")")
                           .collect(Collectors.joining(", ")))
                   .append(". ");
        }
        
        return content.toString();
    }

    /**
     * Tìm kiếm phim bằng semantic search
     */
    public List<MovieResponseDTO> searchMovies(String query) {
        try {
            log.info("Đang tìm kiếm phim theo RAG flow: {}", query);
            log.info("Sử dụng embedding model: {}", embeddingModel.getClass().getSimpleName());


            // Tìm kiếm vector với ngưỡng tương đồng
            var results = vectorStore.similaritySearch(SearchRequest.builder()
                    .query(query)
                    .topK(TOP_K_RESULTS)
                    .similarityThreshold(SIMILARITY_THRESHOLD)
                    .build());

            log.info("Đã thực hiện vector search, số kết quả: {}", results != null ? results.size() : 0);

            if (results == null || results.isEmpty()) {
                log.info("Không tìm thấy kết quả từ vector search");
                return List.of();
            }

            // Lấy các movieId từ kết quả tìm kiếm và chuyển sang ObjectId
            List<ObjectId> movieIds = results.stream()
                    .map(doc -> new ObjectId(doc.getMetadata().get(MOVIE_ID_KEY).toString()))
                    .collect(Collectors.toList());

            // Lấy thông tin phim từ database
            List<Movie> movies = movieRepository.findAllById(movieIds);

            // Chuyển đổi sang DTO
            return movies.stream()
                    .map(movieConverter::convertToDTO)
                    .toList();

        } catch (Exception e) {
            log.error("Lỗi khi tìm kiếm phim: {}", e.getMessage(), e);
            return List.of();
        }
    }
}