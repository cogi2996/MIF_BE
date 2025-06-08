package com.mif.movieInsideForum.Collection;

import com.mif.movieInsideForum.Module.Movie.Movie;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Document(collection = "saved_movies")
@Data
public class SavedMovie {
    @Id
    private ObjectId id;

    @DBRef(lazy = true)
    private User user;

    @DBRef(lazy = true)
    private Movie movie; // Reference to the Movie entity


    @CreatedDate
    private Date savedAt; // Timestamp for when the movie was saved
}