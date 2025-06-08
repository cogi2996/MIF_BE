package com.mif.movieInsideForum.Mapper;

import com.mif.movieInsideForum.Collection.Location.Province;
import com.mif.movieInsideForum.DTO.ProvinceDetailDTO;
import com.mif.movieInsideForum.DTO.ProvincesDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProvinceMapper {
    // to provinceDTO
    ProvincesDTO toProvinceDTO(Province province);
    // to provinceDetailDTO
    ProvinceDetailDTO toProvinceDetailDTO(Province province);
}
