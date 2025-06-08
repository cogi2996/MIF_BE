package com.mif.movieInsideForum.Collection.Event;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Annotation.ObjectIdToStringSerializer;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Document(collection = "events")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Event {
    @Id
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id; // Unique identifier for the event
    private String eventName; // Name of the event
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId groupId; // Group ID of the event
    @JsonSerialize(using = ToStringSerializer.class)
    private  ObjectId ownerId; // User ID of the event creator
    private String description; // Description of the event

    private Date startDate; // Start date of the event
    @Builder.Default
    private String eventPicture = "https://mif-bucket-1.s3.ap-southeast-1.amazonaws.com/d0851fb4-b4c5-4d1e-a217-1f5a48854670_event_default.jpg"; // Picture of the event
    private SocialType socialType; // Type of social event
    @NotNull(message = "Event type is required")
//    @EnumValidation(value = EventType.class, message = "Event type must be ONLINE or OFFLINE")
    private EventType eventType; // Type of event
    private String link; // Link to the event
    private String location; // Location of the event
    @Builder.Default
    @JsonSerialize(contentUsing = ObjectIdToStringSerializer.class)
    private List<ObjectId> userJoin = new ArrayList<>();
    private Date createdAt = new Date(); // Timestamp for when the event was created
}