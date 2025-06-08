package com.mif.movieInsideForum.Module.Comment;

import com.mif.movieInsideForum.Collection.Comment;
import com.mif.movieInsideForum.Collection.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
@RequiredArgsConstructor
public class CommentController {

    private final SimpMessagingTemplate messagingTemplate;
    private final CommentService commentService;

    @MessageMapping("/comment.sendComment") // Endpoint cho WebSocket
    public void sendComment(@Payload Comment comment, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Comment dbComment =  commentService.createComment(comment,user);
        messagingTemplate.convertAndSend("/topic/comments/" + comment.getPostId(), dbComment);
    }

    @MessageMapping("/comment.upvote")
    public void upvoteComment(@Payload ObjectId commentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Comment updatedComment = commentService.upvote(commentId, user.getId());
        messagingTemplate.convertAndSend("/topic/comments/" + updatedComment.getPostId(), updatedComment);
    }

    @MessageMapping("/comment.downvote") 
    public void downvoteComment(@Payload ObjectId commentId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Comment updatedComment = commentService.downvote(commentId, user.getId());
        messagingTemplate.convertAndSend("/topic/comments/" + updatedComment.getPostId(), updatedComment);
    }
}