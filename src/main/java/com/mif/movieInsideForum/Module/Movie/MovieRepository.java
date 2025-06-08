package com.mif.movieInsideForum.Module.Movie;

import com.mif.movieInsideForum.Collection.Actor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface MovieRepository extends MongoRepository<Movie, ObjectId> {
    // find latest movie
    Slice<Movie> findAllByOrderByReleaseDateDesc(Pageable pageable);
    // find by title
    Slice<Movie> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    // find by description
    Slice<Movie> findByDescriptionContainingIgnoreCase(String description, Pageable pageable);
    //
    @Query("{}") // find all with no condition
    Slice<Movie> findAllWithPag(Pageable pageable);


    @Aggregation(pipeline = {
            "{ '$match': { '_id': ?0 } }",
            "{ '$unwind': '$cast' }",
            "{ '$match': { 'cast': { '$in': ?1 } } }",
            "{ '$project': { 'actorId': '$cast.id' } }"
    })
    List<ObjectId> findExistingActorIdsInCast(ObjectId movieId, List<ObjectId> actorIds);

    @Query("{ 'genre': ?0 }")
    Slice<Movie> findByCategoryId(ObjectId categoryId, Pageable pageable);

    @Query("{ 'genre': ?0 }")
    Page<Movie> findByCategoryIdAsPage(ObjectId categoryId, Pageable pageable);

    List<Movie> findAllByCastContains(Actor actor);

    @Aggregation(pipeline = {
        "{ $vectorSearch: { " +
            "index: 'vector_index', " +
            "path: 'embed', " +
            "queryVector: ?0, " +
            "numCandidates: 200, " +
            "limit: ?1" +
        "}}",
        "{ $project: { " +
            "id: 1, " +
            "title: 1, " +
            "description: 1, " +
            "releaseDate: 1, " +
            "genre: 1, " +
            "director: 1, " +
            "cast: 1, " +
            "posterUrl: 1, " +
            "trailerUrl: 1, " +
            "duration: 1, " +
            "country: 1, " +
            "budget: 1, " +
            "awards: 1, " +
            "score: { $meta: 'vectorSearchScore' } " +
        "}}",
        "{ $sort: { score: -1 } }"
    })
    List<Movie> findSimilarMovies(List<Double> queryVector, int limit);
}
