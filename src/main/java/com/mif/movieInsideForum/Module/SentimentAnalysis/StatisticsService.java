package com.mif.movieInsideForum.Module.SentimentAnalysis;

import com.mif.movieInsideForum.DTO.Response.AdminStatisticsResponseDTO;
import com.mif.movieInsideForum.Module.Actor.ActorRepository;
import com.mif.movieInsideForum.Module.Group.GroupRepository;
import com.mif.movieInsideForum.Module.Movie.MovieRepository;
import com.mif.movieInsideForum.Module.MovieRating.repository.MovieRatingsRepository;
import com.mif.movieInsideForum.Module.Post.repository.GroupPostRepository;
import com.mif.movieInsideForum.Module.User.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class StatisticsService {
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final GroupPostRepository groupPostRepository;
    private final MovieRepository movieRepository;
    private final MovieRatingsRepository movieRatingsRepository;
    private final ActorRepository actorRepository;



    public AdminStatisticsResponseDTO getPostStatistics() {
        // total user
        long totalUser = userRepository.count();
        // total post
        long totalPost = groupPostRepository.count();
        // total group
        long totalGroup = groupRepository.count();
        // total movie
        long totalMovie = movieRepository.count();
        // total rating movie
        long totalRatingMovie = movieRatingsRepository.count();
        // total saved movie
        long totalActor = actorRepository.count();

        return AdminStatisticsResponseDTO.builder()
                .totalUser(totalUser)
                .totalPost(totalPost)
                .totalGroup(totalGroup)
                .totalMovie(totalMovie)
                .totalRatingMovie(totalRatingMovie)
                .totalActor(totalActor)
                .build();
    }

}
