package uxt6.psu.com.a1000books.adapter;

import android.content.Context;
import android.content.Intent;
import android.media.Rating;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import uxt6.psu.com.a1000books.R;
import uxt6.psu.com.a1000books.entity.Comment;

/**
 * Created by aisyahumar on 3/9/2018.
 */

public class CommentAdapter extends BaseAdapter{

    private List<Comment> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    public CommentAdapter(Context context){
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmData(List<Comment> comments){
        mData = comments;

        notifyDataSetChanged();
        //Log.d(CommentAdapter.class.getSimpleName(), "setmData: "+mData.size());
    }

    public void addItem(final Comment comment){
        mData.add(comment);
        notifyDataSetChanged();
    }

    public void clearData(){
        mData.clear();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mData==null?0:mData.size();
    }

    @Override
    public Comment getItem(int index) {
        return mData.get(index);
    }

    @Override
    public long getItemId(int index) {
        return mData.get(index).getId();
        //return 0;
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view==null){
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.comment_list_item,null);
            holder.imgPhoto = (de.hdodenhof.circleimageview.CircleImageView) view.findViewById(R.id.img_reader_photo);
            holder.txtReaderName = (TextView) view.findViewById(R.id.tv_name);
            holder.rating = (RatingBar) view.findViewById(R.id.ratingBar);
            holder.txtComment = (TextView) view.findViewById(R.id.tv_comment);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        /**
         * retrieve poster
         */
        //String url = "http://image.tmdb.org/t/p/w185/"+mData.get(index).getPosterPath();

        Picasso.with(context)
                .load(mData.get(index).getCommentatorUrlPhoto())
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                .into(holder.imgPhoto);
        holder.txtReaderName.setText(mData.get(index).getCommentator());
        holder.rating.setRating(mData.get(index).getReviewRating());
        holder.txtComment.setText(mData.get(index).getComment());
        return view;
    }

    private static class ViewHolder{
        de.hdodenhof.circleimageview.CircleImageView imgPhoto;
        TextView txtReaderName;
        RatingBar rating;
        TextView txtComment;
    }
}
