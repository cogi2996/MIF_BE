package com.mif.movieInsideForum.Collection;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "tokens")
public class Token {
    @Id
    private String id;
    private String token;
    private TokenType tokenType;
    private boolean expired;
    private boolean revoked;

    @DBRef
    private User user;
}