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

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class AddBookFragment extends Fragment {

    private AddBookListAdapter addBookListAdapter;
    private List<Book> bookList;

    public static AddBookFragment newInstance() {
        AddBookFragment fragment = new AddBookFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookList = new ArrayList<>();

        addBookListAdapter = new AddBookListAdapter(getActivity(), bookList);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_book, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final ListView listView = view.findViewById(R.id.add_book_list);
        final EditText searchQueryView = view.findViewById(R.id.add_book_search_query);

        Button searchButton = view.findViewById(R.id.add_book_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                bookList.clear();

                String query = searchQueryView.getText().toString();

                GoogleBooksClient client = new GoogleBooksClient(bookList, listView, addBookListAdapter, getActivity());
                client.execute(query);
            }
        });

    }

}


