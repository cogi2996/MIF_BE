package com.mif.movieInsideForum.DTO;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.mif.movieInsideForum.Collection.Location.District;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProvinceDetailDTO {
    @JsonSerialize(using = ToStringSerializer.class)
    private ObjectId id;
    private String code;
    private String name;
    private String nameEn;
    private String fullName;
    private String fullNameEn;
    private String codeName;
    private Integer administrativeUnitId;
    private Integer administrativeRegionId;
    private List<District> district;
}
