package ie.baloot6.DJO;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommentDJO {


    @SerializedName("userEmail")
    private String userEmail;
    @SerializedName("commodityId")
    private Long commodityId;
    @SerializedName("text")
    private String text;
    @SerializedName("date")
    private String date;
    @SerializedName("username")
    private String username;

    @Expose(serialize = false, deserialize = false)
    private Long commentId;




    public CommentDJO(Long commentId, Long commodityId, String username, String text, String date) {
        this.commentId = commentId;
        this.commodityId = commodityId;
        this.username = username;
        this.text = text;
        this.date = date;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public void setCommodityId(Long commodityId) {
        this.commodityId = commodityId;
    }

    public void setDate(String date) {
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

    public String getDate() {
        return date;
    }

    public Long getCommodityId() {
        return commodityId;
    }
}