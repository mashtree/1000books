package uxt6.psu.com.a1000books.entity;

import android.util.Log;

import org.json.JSONObject;

/**
 * Created by aisyahumar on 3/7/2018.
 */

public class Comment {

    private int id;
    private String comment;
    private int reviewRating;
    private String commentator;
    private String commentatorUrlPhoto;

    private final String TAG = "COMMENT";

    public Comment(JSONObject object){
        try{
            id = object.getInt("id");
            comment = object.getString("comment");
            reviewRating = object.getInt("review_rating");
            commentator = object.getString("name");
            commentatorUrlPhoto= object.getString("reader_photo");
        }catch(Exception e){
            Log.e(TAG, "Error on constructor");
            e.printStackTrace();
        }
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getReviewRating() {
        return reviewRating;
    }

    public void setReviewRating(int reviewRating) {
        this.reviewRating = reviewRating;
    }

    public String getCommentator() {
        return commentator;
    }

    public void setCommentator(String commentator) {
        this.commentator = commentator;
    }

    public String getCommentatorUrlPhoto() {
        return commentatorUrlPhoto;
    }

    public void setCommentatorUrlPhoto(String commentatorUrlPhoto) {
        this.commentatorUrlPhoto = commentatorUrlPhoto;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
