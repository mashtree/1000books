package uxt6.psu.com.a1000books.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uxt6.psu.com.a1000books.DetailBookCommentActivity;
import uxt6.psu.com.a1000books.R;
import uxt6.psu.com.a1000books.entity.Book;

/**
 * Created by aisyahumar on 3/15/2018.
 */

public class BookAdapter extends BaseAdapter {

    private List<Book> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    public BookAdapter(Context context){
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmData(List<Book> books){
        mData = books;
        notifyDataSetChanged();
    }

    public void addItem(final Book book){
        mData.add(book);
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
    public Book getItem(int index) {
        return mData.get(index);
    }

    @Override
    public long getItemId(int index) {
        return mData.get(index).getId();
    }

    @Override
    public View getView(final int index, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if(view==null){
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.book_list_item,null);
            holder.imgCover = (ImageView) view.findViewById(R.id.img_reader_photo);
            holder.tvTitle= (TextView) view.findViewById(R.id.tv_title);
            holder.tvAuthor = (TextView) view.findViewById(R.id.tv_author);
            holder.ratingBar = (TextView) view.findViewById(R.id.tv_rating);
            holder.tvReader = (TextView) view.findViewById(R.id.tv_reader);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

        /**
         * retrieve poster
         */
        //String url = "http://image.tmdb.org/t/p/w185/"+mData.get(index).getPosterPath();

        Picasso.with(context)
                .load(mData.get(index).getCover())
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                .into(holder.imgCover);
        holder.tvTitle.setText(mData.get(index).getTitle());
        holder.tvAuthor.setText("by "+mData.get(index).getAuthor());
        holder.tvReader.setText(mData.get(index).getReader());
        //holder.ratingBar.setText(mData.get(index).getRating());
        //holder.txtRelease.setText(mData.get(index).getRelease());
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToDetail = new Intent(context,DetailBookCommentActivity.class);
                intentToDetail.putExtra(DetailBookCommentActivity.EXTRA_BOOK,mData.get(index));
                // additional flag, for calling from outside activity context
                intentToDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentToDetail);

            }
        });
        return view;
    }

    private static class ViewHolder{
        ImageView imgCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvReader;
        TextView ratingBar;
    }
}
