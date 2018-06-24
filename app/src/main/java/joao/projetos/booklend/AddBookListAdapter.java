package joao.projetos.booklend;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class AddBookListAdapter extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> bookList;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public AddBookListAdapter(@NonNull Context context, List<Book> list) {
        super(context, 0, list);
        this.context = context;
        this.bookList = list;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        final DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
        final String bookID = rootReference.child("books").push().getKey();

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.add_book_list_item_layout, parent, false);
        }

        final Book currentBook = bookList.get(position);

        currentBook.setBookID(bookID);

        ImageView bookCover = listItem.findViewById(R.id.book_cover_image);

        if (currentBook.getCoverLink() != null) {
            Picasso.get().load(currentBook.getCoverLink()).into(bookCover);
        } else {
            bookCover.setImageResource(R.drawable.no_image_found);
        }

        TextView title = listItem.findViewById(R.id.book_title);
        title.setText(currentBook.getTitle());

        TextView authors = listItem.findViewById(R.id.book_authors);
        authors.setText(currentBook.getAuthor());

        Button addButton = listItem.findViewById(R.id.add_book_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentUserID = user.getUid();

                rootReference.child("books").child(bookID).setValue(currentBook);
                rootReference.child("bookseachuser").child(currentUserID).child(bookID).setValue(currentBook);

                Toast.makeText(context, "Book added successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        return listItem;
    }
}
