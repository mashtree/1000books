package uxt6.psu.com.a1000books.adapter;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import uxt6.psu.com.a1000books.BookDetailActivity;
import uxt6.psu.com.a1000books.FormAddUpdateBookActivity;
import uxt6.psu.com.a1000books.R;
import uxt6.psu.com.a1000books.db.DatabaseContract;
import uxt6.psu.com.a1000books.entity.Book;
import uxt6.psu.com.a1000books.utility.ImageSaver;

/**
 * Created by aisyahumar on 2/27/2018.
 */

public class BookCursorAdapter extends RecyclerView.Adapter<BookCursorAdapter.BookViewHolder>{

    private Cursor listBooks;
    private Activity activity;

    public BookCursorAdapter(Activity activity){
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
    public void onBindViewHolder(final BookViewHolder holder, int position) {
        final Book book = getItem(position);
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText("by "+book.getAuthor());
        holder.tvRate.setText(String.valueOf(book.getRating()));
        holder.tvPublication.setText(book.getPublisher());
        Bitmap bitmap = new ImageSaver(activity)
                .setFileName(book.getCover())
                .setDirectoryName("bookCovers")
                .load();
        holder.ivCover.setImageBitmap(bitmap);
        holder.btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, FormAddUpdateBookActivity.class);
                Uri uri = Uri.parse(DatabaseContract.BOOK_CONTENT_URI+"/"+book.getId());
                intent.setData(uri);
                activity.startActivityForResult(intent, FormAddUpdateBookActivity.REQUEST_UPDATE);
            }
        });
        if(book.getServerId()==0){
            Log.d(BookCursorAdapter.class.getSimpleName(), "onBindViewHolder: "+(book.getServerId()==0));
            holder.btnUpload.setVisibility(View.GONE);
        }else{
            Log.d(BookCursorAdapter.class.getSimpleName(), "onBindViewHolder: "+(book.getServerId()==0));
            holder.btnUpload.setVisibility(View.VISIBLE);
        }
        /*holder.btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(activity, "Nothing to do with "+book.getTitle(), Toast.LENGTH_SHORT).show();
            }
        });*/
        holder.cvNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, BookDetailActivity.class);
                intent.putExtra(BookDetailActivity.EXTRA_BOOK, book.getId());
                intent.putExtra(BookDetailActivity.EXTRA_GPLUS, book.getIsGPlusShared());
                Log.d(BookCursorAdapter.class.getSimpleName(), "onClick: _id="+book.getId());
                //activity.startActivityForResult(intent, BookDetailActivity.REQUEST_UPLOAD);
                activity.startActivity(intent);
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
        ImageView ivCover;
        ImageButton btnEdit, btnUpload;
        CardView cvNote;

        public BookViewHolder(View v) {
            super(v);
            tvTitle = (TextView) v.findViewById(R.id.tv_title);
            tvAuthor = (TextView) v.findViewById(R.id.tv_author);
            tvRate = (TextView) v.findViewById(R.id.tv_rate);
            tvPublication = (TextView) v.findViewById(R.id.tv_publisher);
            ivCover = (ImageView) v.findViewById(R.id.iv_cover);
            btnEdit = (ImageButton) v.findViewById(R.id.btn_edit);
            btnUpload = (ImageButton) v.findViewById(R.id.btn_upload);
            cvNote = (CardView) itemView.findViewById(R.id.cv_item_note);
        }
    }

}
