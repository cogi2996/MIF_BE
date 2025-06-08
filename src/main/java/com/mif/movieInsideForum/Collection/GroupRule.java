package com.mif.movieInsideForum.Collection;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.*;
import org.bson.types.ObjectId;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GroupRule {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id = new ObjectId(); // ID của quy tắc
    private String ruleDescription; // Mô tả quy tắc
}