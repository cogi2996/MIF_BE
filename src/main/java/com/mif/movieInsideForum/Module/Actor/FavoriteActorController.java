package com.mif.movieInsideForum.Module.Actor;

import com.mif.movieInsideForum.DTO.ActorResponseDTO;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import com.mif.movieInsideForum.Security.AuthenticationFacade;
import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/favoriteActors")
@RequiredArgsConstructor
public class FavoriteActorController {

    private final ActorService actorService;
    private final AuthenticationFacade authenticationFacade;

    @GetMapping("/{actorId}")
    public ResponseEntity<ResponseWrapper<Boolean>> isActorFavorite(@PathVariable ObjectId actorId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        boolean isFavorite = actorService.isActorFavorite(userId, actorId);
        ResponseWrapper<Boolean> response = new ResponseWrapper<>("success", "Actor favorite status retrieved", isFavorite);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{actorId}")
    public ResponseEntity<ResponseWrapper<Void>> addFavoriteActor(@PathVariable ObjectId actorId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        actorService.addFavoriteActor(userId, actorId);
        ResponseWrapper<Void> response = new ResponseWrapper<>("success", "Actor added to favorites");
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{actorId}")
    public ResponseEntity<ResponseWrapper<Void>> removeFavoriteActor(@PathVariable ObjectId actorId) {
        ObjectId userId = authenticationFacade.getUser().getId();
        actorService.removeFavoriteActor(userId, actorId);
        ResponseWrapper<Void> response = new ResponseWrapper<>("success", "Actor removed from favorites");
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<ResponseWrapper<Slice<ActorResponseDTO>>> getFavoriteActors(@PageableDefault(size = 5) Pageable pageable) {
        ObjectId userId = authenticationFacade.getUser().getId();
        Slice<ActorResponseDTO> favoriteActors = actorService.getFavoriteActorsByUserId(userId, pageable);
        ResponseWrapper<Slice<ActorResponseDTO>> response = new ResponseWrapper<>("success", "Favorite actors retrieved", favoriteActors);
        return ResponseEntity.ok(response);
    }
}