package ie.baloot6.model;

import jakarta.persistence.*;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "comments")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long commentId;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne
    @JoinColumn(name = "commodityId")
    private Commodity commodity;

    @Column(nullable = false)
    private String text;

    @Column(nullable = false)
    private Date date;

    @OneToMany(cascade=CascadeType.ALL)
    @JoinColumn(name = "commentId")
    private Set<Vote> votes = new HashSet<>();;

    public Comment(Long commentId, User user, Commodity commodity, String text, Date date) {
        this.commentId = commentId;
        this.user = user;
        this.commodity = commodity;
        this.text = text;
        this.date = date;
    }

    public Comment(User user, Commodity commodity, String text, Date date) {
        this.user = user;
        this.commodity = commodity;
        this.text = text;
        this.date = date;
    }

    public Comment() {
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Long getCommentId() {
        return commentId;
    }

    public void setCommentId(Long commentId) {
        this.commentId = commentId;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Date getDate() {
        return date;
    }

    public Commodity getCommodity() {
        return commodity;
    }

    public void setCommodity(Commodity commodity) {
        this.commodity = commodity;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Set<Vote> getVotes() {
        return votes;
    }

    public void setVotes(Set<Vote> votes) {
        this.votes = votes;
    }
}