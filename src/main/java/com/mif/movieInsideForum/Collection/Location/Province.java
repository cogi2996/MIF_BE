package com.mif.movieInsideForum.Collection.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Document(collection = "provinces")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Province {
    @Id
    private ObjectId id;
    @Field("Code")
    private String code;
    @Field("Name")
    private String name;
    @Field("NameEn")
    private String nameEn;
    @Field("FullName")
    private String fullName;
    @Field("FullNameEn")
    private String fullNameEn;
    @Field("CodeName")
    private String codeName;
    @Field("AdministrativeUnitId")
    private Integer administrativeUnitId;
    @Field("AdministrativeRegionId")
    private Integer administrativeRegionId;
    @Field("District")
    private List<District> district;
}