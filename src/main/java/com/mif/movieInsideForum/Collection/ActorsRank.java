package com.mif.movieInsideForum.Collection;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "actors")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ActorsRank {
    private String id;
    private String actorId;
}
