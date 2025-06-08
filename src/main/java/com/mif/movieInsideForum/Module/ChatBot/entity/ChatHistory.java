package com.mif.movieInsideForum.Module.ChatBot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Module.Movie.dto.MovieResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Document(collection = "chatbot_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;

    private String userId;

    private String query;

    private String response;

    private List<MovieResponseDTO> movies;

    private Date timestamp;

    public ChatHistory(String userId, String query, String response, List<MovieResponseDTO> movies) {
        this.userId = userId;
        this.query = query;
        this.response = response;
        this.movies = movies;
        this.timestamp = new Date();
    }
} 