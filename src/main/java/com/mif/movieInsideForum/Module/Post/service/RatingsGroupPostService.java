package com.mif.movieInsideForum.Module.Post.service;

import com.mif.movieInsideForum.DTO.RatingsGroupPostRequestDTO;

public interface RatingsGroupPostService {
    void addUpVote(RatingsGroupPostRequestDTO requestDTO);
    void addDownVote(RatingsGroupPostRequestDTO requestDTO);
    void removeVote(RatingsGroupPostRequestDTO requestDTO);
}