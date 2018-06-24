package joao.projetos.booklend;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchLendersFragment extends Fragment {

    private List<Book> bookList;

    public static SearchLendersFragment newInstance() {
        SearchLendersFragment fragment = new SearchLendersFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_search_lenders, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final String currentUserUsername = SplashScreenActivity.removeSpacesFromUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName());

        final EditText queryEditText = view.findViewById(R.id.search_lenders_search_query);

        final ListView listview = view.findViewById(R.id.lenders_list);

        final SearchLendersAdapter searchLendersAdapter = new SearchLendersAdapter(getActivity(), bookList);

        Button searchButton = view.findViewById(R.id.search_lenders_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String query = queryEditText.getText().toString();

                DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();
                DatabaseReference allBooks = rootReference.child("books");

                allBooks.orderByChild("title").startAt(query).endAt(query + "\uf8ff").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        bookList.clear();
                        for (DataSnapshot books : dataSnapshot.getChildren()) {
                            Book resultBook = books.getValue(Book.class);
                            if (!resultBook.getOwner().equals(currentUserUsername)) {
                                bookList.add(resultBook);
                            }
                        }
                        listview.setAdapter(searchLendersAdapter);
                    }
                    
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

    }
}
