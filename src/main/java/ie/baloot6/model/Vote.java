package ie.baloot6.model;

import jakarta.persistence.*;

@Entity
@Table(name = "commentsVotes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long voteId;

    @ManyToOne
    @JoinColumn(name = "commentId")
    private Comment comment;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    private int  vote;

    public Vote() {
    }

    public Vote(Comment comment, User user, int vote) {
        this.comment = comment;
        this.user = user;
        this.vote = vote;
    }

    public long getVoteId() {
        return voteId;
    }

    public void setVoteId(long voteId) {
        this.voteId = voteId;
    }

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getVote() {
        return vote;
    }

    public void setVote(int vote) {
        this.vote = vote;
    }
}
