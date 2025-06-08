package com.mif.movieInsideForum.Collection.Location;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class District {
    private String Code;
    private String Name;
    private String NameEn;
    private String FullName;
    private String FullNameEn;
    private String CodeName;
    private String ProvinceCode;
    private Integer AdministrativeUnitId;
    private List<Ward> Ward;

    // Getters and Setters


}