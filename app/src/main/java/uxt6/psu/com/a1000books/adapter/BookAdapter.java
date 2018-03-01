package uxt6.psu.com.a1000books.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import uxt6.psu.com.a1000books.FormAddUpdateBookActivity;
import uxt6.psu.com.a1000books.R;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;

/**
 * Created by aisyahumar on 2/27/2018.
 */

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.BookViewHolder>{

    private Cursor listBooks;
    private Activity activity;

    public BookAdapter(Activity activity){
        this.activity = activity;
    }

    public Cursor getListBooks(){
        return listBooks;
    }

    public void setListBooks(Cursor listNotes){
        this.listBooks = listNotes;
    }

    @Override
    public BookViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.book_item,parent, false);
        return new BookViewHolder(view);
    }

    @Override
    public void onBindViewHolder(BookViewHolder holder, int position) {
        final Book book = getItem(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvRate.setText("");
        holder.tvPublication.setText(book.getPublisher());
        holder.cvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FormAddUpdateBookActivity.class);
                Uri uri = Uri.parse(DatabaseContract.BOOK_CONTENT_URI+"/"+book.getId());
                intent.setData(uri);
                activity.startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_UPDATE);
            }
        });
    }

    @Override
    public int getItemCount() {
        if(listBooks==null) return 0;
        return listBooks.getCount();
    }

    private Book getItem(int position){
        if(!listBooks.moveToPosition(position)){
            throw new IllegalStateException("Position invalid");
        }
        return new Book(listBooks);
    }

    public class BookViewHolder extends RecyclerView.ViewHolder{
        TextView tvTitle, tvAuthor, tvRate, tvPublication;
        CardView cvNote;

        public BookViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tv_title);
            tvAuthor = (TextView) v.findViewById(R.id.tv_author);
            tvRate = (TextView) v.findViewById(R.id.tv_rate);
            tvPublication = (TextView) v.findViewById(R.id.tv_publisher);
            cvNote = (CardView) itemView.findViewById(R.id.cv_item_note);
        }
    }

}
