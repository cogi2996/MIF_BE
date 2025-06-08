package com.mif.movieInsideForum.Collection.Field;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Episode {
    private Integer totalEpisodes = null; // Only for TV Series
    private List<Integer> episodeDuration;
}
