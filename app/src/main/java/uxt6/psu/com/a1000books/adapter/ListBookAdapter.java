package uxt6.psu.com.a1000books.adapter;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import uxt6.psu.com.a1000books.R;
import uxt6.psu.com.a1000books.entity.Book;

/**
 * Created by aisyahumar on 2/22/2018.
 */

public class ListBookAdapter extends RecyclerView.Adapter<ListBookAdapter.CategoryViewHolder>{

    private List<Book> listBook;
    private Cursor bookCursor;
    private Context context;
    private int flag = 0;

    public ListBookAdapter(Context context){
        this.context = context;
    }

    List<Book> getListBooks(){
        return listBook;
    }
    Cursor getListBooksCursor(){
        return bookCursor;
    }

    public void setFlag(int flag){this.flag=flag;}

    public void setListBooks(List<Book> listMovies){
        this.listBook = listMovies;
    }
    public void setListBooks(Cursor listMovies){
        this.bookCursor = listMovies;
        notifyDataSetChanged();
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemRow = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent,false);
        return new CategoryViewHolder(itemRow);
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, final int position) {
        Log.d("ListMovieAdapter", "onBindViewHolder: flag "+flag);
        String url = "";
        if(flag==0){
            //Log.d("ListMovieAdapter", "onBindViewHolder: ");
            holder.tvTitle.setText(getListBooks().get(position).getTitle());
            holder.tvAuthor.setText(getListBooks().get(position).getAuthor());
            holder.tvRate.setText(getListBooks().get(position).getRating());
            holder.tvPublication.setText(getListBooks().get(position).getPublisher());
            //url = "http://image.tmdb.org/t/p/w185/"+getListMovies().get(position).getPosterPath();

        }else if(flag==1){
            final Book item = getItem(position);
            Log.d("ListMovieAdapter", "onBindViewHolder: "+item.getTitle());
            holder.tvTitle.setText(item.getTitle());


        }
        /*Picasso.with(context)
                .load(url)
                .placeholder(R.drawable.ic_photo_black_24dp)
                .error(R.drawable.ic_filter_b_and_w_black_24dp)
                .into(holder.imgPoster);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentToDetail = new Intent(context,DetailMovieActivity.class);
                if(flag==0){
                    intentToDetail.putExtra(DetailMovieActivity.EXTRA_MOVIE,listMovies.get(position));
                }else{
                    intentToDetail.putExtra(DetailMovieActivity.EXTRA_MOVIE,getItem(position));
                }

                // additional flag, for calling from outside activity context
                intentToDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentToDetail);

            }
        });*/
    }

    @Override
    public int getItemCount() {
        if(flag==0){
            if(listBook==null) return 0;
            return listBook.size();
        }else{
            if(bookCursor==null) return 0;
            return bookCursor.getCount();
        }
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout relativeLayout;
        ImageView imgCover;
        TextView tvTitle;
        TextView tvAuthor;
        TextView tvRate;
        TextView tvPublication;
        public CategoryViewHolder(View v) {
            super(v);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.list_books);
            imgCover = (ImageView) v.findViewById(R.id.iv_cover);
            tvTitle = (TextView) v.findViewById(R.id.tv_title);
            tvAuthor = (TextView) v.findViewById(R.id.tv_author);
            tvRate = (TextView) v.findViewById(R.id.tv_rate);
            tvPublication = (TextView) v.findViewById(R.id.tv_publisher);
        }
    }

    private Book getItem(int position){
        if(!bookCursor.moveToPosition(position)){
            throw new IllegalStateException("Position invalid");
        }
        return new Book(bookCursor);
    }

    public void clearData(){
        //listMovies.clear();
    }
}
