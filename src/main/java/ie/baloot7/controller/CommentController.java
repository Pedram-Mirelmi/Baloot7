package ie.baloot7.controller;

import ie.baloot7.Utils.JWTUtility;
import ie.baloot7.data.IRepository;
import ie.baloot7.data.ISessionManager;
import ie.baloot7.exception.InvalidIdException;
import ie.baloot7.exception.InvalidRequestParamsException;
import ie.baloot7.exception.InvalidValueException;
import ie.baloot7.model.Comment;
import ie.baloot7.DTO.CommentDTO;
import ie.baloot7.model.User;
import org.springframework.web.bind.annotation.*;

import static ie.baloot7.Utils.Constants.*;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@RestController
public class CommentController {
    final IRepository repository;
//    final ISessionManager sessionManager;


    public CommentController(IRepository repository, ISessionManager sessionManager) {
        this.repository = repository;
//        this.sessionManager = sessionManager;
    }

    @GetMapping("/api/comments/{commentId}")
    public CommentDTO getSingleComment(@RequestHeader(AUTH_TOKEN) String authToken,
                                       @PathVariable(COMMENT_ID) long commentId) throws InvalidIdException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        return new CommentDTO(repository.getComment(commentId).get(),
                repository.getUserVoteForComment(user.getUsername(), commentId)) ;
    }

    @GetMapping("/api/comments")
    public List<CommentDTO> getCommodityComments(@RequestHeader(AUTH_TOKEN) String authToken, @RequestParam(COMMODITY_ID) long commodityId) {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        return repository.getCommentsForCommodity(commodityId).stream().map(
                comment -> new CommentDTO(comment, repository.getUserVoteForComment(user.getUsername(), comment.getCommentId()))
        ).toList();
    }

    @PostMapping("/api/comments")
    public Map<String, String> addComment(@RequestHeader(AUTH_TOKEN) String authToken, @RequestBody Map<String, Object> body) throws InvalidValueException{
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        repository.addComment(user.getUsername(), (int) body.get("commodityId"), (String) body.get("text"));
        return Map.of(STATUS, SUCCESS);
    }

    @PostMapping("/api/commentsVotes")
    public Map<String, Object> rateComment(@RequestHeader(AUTH_TOKEN) String authToken, @RequestBody Map<String, Long> body) throws InvalidIdException, InvalidValueException {
        User user = repository.getUserByUsername(JWTUtility.getUsernameFromToken(authToken));
        try {
            Comment comment = repository.getComment(body.get(COMMENT_ID)).get();
            repository.addVote(user.getUsername(), comment.getCommentId(), (int)body.get(VOTE).longValue());
            return Map.of(STATUS, SUCCESS,
                    LIKES, repository.getLikes(comment.getCommentId()),
                    DISLIKES, repository.getDislikes(comment.getCommentId()));
        }
        catch (NoSuchElementException e) {
            throw new InvalidRequestParamsException("Invalid comment Id");
        }
    }
}
