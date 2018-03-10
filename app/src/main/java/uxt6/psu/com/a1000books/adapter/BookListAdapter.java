package uxt6.psu.com.a1000books.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import uxt6.psu.com.a1000books.DetailBookCommentActivity;
import uxt6.psu.com.a1000books.R;
import uxt6.psu.com.a1000books.entity.Book;

/**
 * Created by aisyahumar on 3/6/2018.
 */

public class BookListAdapter extends RecyclerView.Adapter<BookListAdapter.BookViewHolder>{
    private List<Book> mData = new ArrayList<>();
    private LayoutInflater mInflater;
    private Context context;

    public BookListAdapter(Context context){
        this.context = context;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public BookListAdapter(Context context, List<Book> books){
        this.context = context;
        this.mData = books;
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setmData(List<Book> books){
        mData = books;
        notifyDataSetChanged();
    }

    List<Book> getListBooks(){
        return mData;
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
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_list_item,parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public long getItemId(int index) {
        return mData.get(index).getId();
    }

    @Override
    public int getItemCount() {
        if(mData!=null) return mData.size();
        return 0;
    }

    private Book getItem(int position){
        if(mData.isEmpty()){
            throw new IllegalStateException("Position invalid");
        }
        return mData.get(position);
    }

    public void notify(List<Book> list) {
        if (mData != null) {
            mData.clear();
            mData.addAll(list);

        } else {
            mData = list;
        }
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, final int position) {
        final Book book = getListBooks().get(position);
         /**
         * retrieve poster
         */
        String url = book.getCover();
        //Log.d(BookListAdapter.class.getSimpleName(), "onBindViewHolder: url="+url);
        Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                .into(holder.imgCover);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("by "+book.getAuthor());
        holder.tvRating.setText(String.valueOf(book.getRating()));
        holder.tvReader.setText(book.getReader());
        holder.imgCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToDetail = new Intent(context,DetailBookCommentActivity.class);
                intentToDetail.putExtra(DetailBookCommentActivity.EXTRA_BOOK, mData.get(position));
                // additional flag, for calling from outside activity context
                intentToDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentToDetail);

            }
        });
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{
        ImageView imgCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvRating;
        TextView tvReader;

        public BookViewHolder(View view) {
            super(view);
            imgCover = (ImageView) view.findViewById(R.id.img_reader_photo);
            tvTitle = (TextView) view.findViewById(R.id.tv_title);
            tvAuthor = (TextView) view.findViewById(R.id.tv_author);
            tvRating= (TextView) view.findViewById(R.id.tv_rating);
            tvReader = (TextView) view.findViewById(R.id.tv_reader);
        }
    }
}
