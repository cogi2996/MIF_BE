package com.mif.movieInsideForum.DTO.message;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventMessage {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId eventId; // ID of the event
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId subscriberId; // user ID of the subscriber
    private String subscriberEmail; // email of the subscriber
    private long delayMillis; // Thời gian delay
    private String subject; // Tiêu đề
    private String content; // Nội dung
}
