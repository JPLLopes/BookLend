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

public class LibraryListAdapter extends ArrayAdapter<Book> {

    private Context context;
    private List<Book> bookList;
    private FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    public LibraryListAdapter(@NonNull Context context, List<Book> list) {
        super(context, 0, list);
        this.context = context;
        this.bookList = list;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;

        if (listItem == null) {
            listItem = LayoutInflater.from(context).inflate(R.layout.bookslist_list_item_layout, parent, false);
        }

        final Book currentBook = bookList.get(position);

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

        TextView isItLent = listItem.findViewById(R.id.is_it_lent);
        final String currentUserUsername = SplashScreenActivity.removeSpacesFromUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());
        if (!currentBook.getOwner().equals(currentUserUsername)) {
            isItLent.setText("É emprestado");
        } else {
            if (currentBook.isLent()) {
                isItLent.setText("Emprestado");
            } else {
                isItLent.setText("Não está emprestado");
            }
        }

        TextView owner = listItem.findViewById(R.id.book_owner);
        owner.setText(currentBook.getOwner());

        Button removeButton = listItem.findViewById(R.id.remove_book_button);

        if (currentBook.isLent()) {
            removeButton.setVisibility(View.GONE);
        } else {
            removeButton.setVisibility(View.VISIBLE);
        }

        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference bookseachuserCurrentBookReference = rootReference.child("bookseachuser").child(userID).child(currentBook.getBookID());
                DatabaseReference booksCurrentBookReference = rootReference.child("books").child(currentBook.getBookID());
                DatabaseReference currentUserBookCurrentBookReference = rootReference.child("users").child(userID).child("books").child(currentBook.getBookID());

                bookseachuserCurrentBookReference.removeValue();
                booksCurrentBookReference.removeValue();
                currentUserBookCurrentBookReference.removeValue();

                bookList.remove(position);

                Toast.makeText(context, "Book removed successfully!", Toast.LENGTH_SHORT).show();

            }
        });

        return listItem;
    }
}
