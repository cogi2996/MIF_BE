package com.mif.movieInsideForum.Module.Comment;

import com.mif.movieInsideForum.Collection.Comment;
import com.mif.movieInsideForum.DTO.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
//@RequestMapping("/api")
@RequiredArgsConstructor
public class CommentRestController {
    private final CommentService commentService;

@GetMapping("/post/{postId}/comments")
public ResponseEntity<ResponseWrapper<Slice<Comment>>> getCommentsByPostId(@PathVariable ObjectId postId, @PageableDefault(size = 10, sort = "createAt", direction = Sort.Direction.DESC) Pageable pageable) {
    Slice<Comment> comments = commentService.getCommentsByPostId(postId, pageable);
    ResponseWrapper<Slice<Comment>> responseWrapper = new ResponseWrapper<>("success", "Comments retrieved successfully", comments);
    return ResponseEntity.ok(responseWrapper);
}
}