package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
public class UserGroupStatusDTO {
    private List<ObjectId> groupIds;
}
