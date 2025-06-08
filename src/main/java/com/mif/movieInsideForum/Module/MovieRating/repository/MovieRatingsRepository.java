package com.mif.movieInsideForum.Module.MovieRating.repository;

import com.mif.movieInsideForum.Collection.Field.Ratings;
import com.mif.movieInsideForum.Module.MovieRating.Entity.MovieRatings;
import com.mif.movieInsideForum.Module.MovieRating.dto.SentimentCountDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.SentimentStatsDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieSentimentExtremeDTO;
import com.mif.movieInsideForum.Module.MovieRating.dto.MovieRatingMonthCount;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface MovieRatingsRepository extends MongoRepository<MovieRatings, ObjectId> {
    Optional<MovieRatings> findByUserIdAndMovieId(ObjectId userId, ObjectId movieId);

   // average rating and number of ratings
   @Aggregation(pipeline = {
           "{ $match: { movieId: ?0 } }",
           "{ $group: { _id: null, averageRating: { $avg: '$ratingValue' }, numberOfRatings: { $sum: 1 } } }"
   })
    Ratings findAverageRatingByMovieId(ObjectId movieId);

   // find sum ratings
   @Aggregation(pipeline = {
           "{ $match: { movieId: ?0 } }",
           "{ $group: { _id: null, sumRatings: { $sum: '$ratingValue' } } }"
   })
    Optional<Double> findSumRatingByMovieId(ObjectId movieId);

   // sum  all ratings movie from list of movie ids
    @Aggregation(pipeline = {
              "{ $match: { movieId: { $in: ?0 } } }",
              "{ $group: { _id: null, sumRatings: { $sum: '$ratingValue' } } }"
    })
    Optional<Double> getSumRatingByMovieIds(List<ObjectId> movieIds);

    void deleteByUserIdAndMovieId(ObjectId userId, ObjectId movieId);


    Optional<Slice<MovieRatings>> findAllByMovieId(ObjectId movieId,Pageable pageable);

    @Aggregation(pipeline = {
            "{ $match: { movieId: ?0 } }",
            "{ $group: { " +
                "_id: '$sentiment', " +
                "count: { $sum: 1 } " +
            "} }",
            "{ $project: { " +
                "sentiment: '$_id', " +
                "count: 1, " +
                "_id: 0 " +
            "} }"
    })
    List<SentimentCountDTO> countSentimentsByMovieId(ObjectId movieId);

    @Aggregation(pipeline = {
        "{ $group: { " +
            "_id: null, " +
            "totalComments: { $sum: 1 }, " +
            "positiveCount: { $sum: { $cond: [ { $eq: ['$sentiment', 'POSITIVE'] }, 1, 0 ] } }, " +
            "negativeCount: { $sum: { $cond: [ { $eq: ['$sentiment', 'NEGATIVE'] }, 1, 0 ] } }, " +
            "neutralCount: { $sum: { $cond: [ { $eq: ['$sentiment', 'NEUTRAL'] }, 1, 0 ] } } " +
        "} }"
    })
    Optional<SentimentStatsDTO> getOverallSentimentStats();

    @Aggregation(pipeline = {
        "{ $group: { " +
            "_id: '$movieId', " +
            "totalComments: { $sum: 1 }, " +
            "positiveCount: { $sum: { $cond: [ { $eq: ['$sentiment', 'POSITIVE'] }, 1, 0 ] } }, " +
            "negativeCount: { $sum: { $cond: [ { $eq: ['$sentiment', 'NEGATIVE'] }, 1, 0 ] } } " +
        "} }",
        "{ $project: { " +
            "_id: 1, " +
            "positivePercentage: { $multiply: [{ $divide: ['$positiveCount', '$totalComments'] }, 100] }, " +
            "negativePercentage: { $multiply: [{ $divide: ['$negativeCount', '$totalComments'] }, 100] } " +
        "} }",
        "{ $facet: { " +
            "mostPositive: [{ $sort: { positivePercentage: -1 } }, { $limit: 1 }], " +
            "mostNegative: [{ $sort: { negativePercentage: -1 } }, { $limit: 1 }] " +
        "} }"
    })
    Optional<MovieSentimentExtremeDTO> findMoviesWithExtremeSentiments();

    @Aggregation(pipeline = {
        "{ $match: { $expr: { $eq: [{ $year: '$createdAt' }, ?0] } } }",
        "{ $group: { _id: { month: { $month: '$createdAt' } }, count: { $sum: 1 } } }",
        "{ $sort: { '_id.month': 1 } }",
        "{ $project: { month: '$_id.month', count: 1, _id: 0 } }"
    })
    java.util.List<MovieRatingMonthCount> countRatingsByMonth(int year);

    interface SentimentCount {
        String get_id();
        Long getCount();
    }

    interface SentimentStats {
        Long getTotalComments();
        Long getPositiveCount();
        Long getNegativeCount();
        Long getNeutralCount();
    }

    interface MovieSentimentExtreme {
        List<MovieSentimentPercentage> getMostPositive();
        List<MovieSentimentPercentage> getMostNegative();
    }

    interface MovieSentimentPercentage {
        ObjectId get_id();
        Double getPositivePercentage();
        Double getNegativePercentage();
    }
}
