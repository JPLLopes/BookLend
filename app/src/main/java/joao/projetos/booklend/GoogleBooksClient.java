package joao.projetos.booklend;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

public class GoogleBooksClient extends AsyncTask<String, Void, String> {

    private List<Book> bookList;
    private ListView listView;
    private AddBookListAdapter addBookListAdapter;
    private Activity activity;
    private String usernameOfTheCurrentUser;

    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";
    private static final String QUERY_PARAM = "q";
    private static final String MAX_RESULTS = "maxResults";
    private static final String PRINT_TYPE = "printType";

    GoogleBooksClient(List<Book> bookList, ListView listView, AddBookListAdapter addBookListAdapter, Activity activity) {
        this.bookList = bookList;
        this.listView = listView;
        this.addBookListAdapter = addBookListAdapter;
        this.activity = activity;
    }

    @Override
    protected String doInBackground(String... params) {

        String query = params[0];

        HttpsURLConnection urlConnection = null;
        BufferedReader client = null;
        String resultJSONString = null;

        try {

            Uri builtURI = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, query)
                    .appendQueryParameter(MAX_RESULTS, "15")
                    .appendQueryParameter(PRINT_TYPE, "books")
                    .build();

            URL bookRequestURL = new URL(builtURI.toString());

            // Open the network connection.
            urlConnection = (HttpsURLConnection) bookRequestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream streamFromAPI = urlConnection.getInputStream();

            // Read the response string into a StringBuilder.
            StringBuilder responseBuilder = new StringBuilder();

            client = new BufferedReader(new InputStreamReader(streamFromAPI));

            String line;
            while ((line = client.readLine()) != null) {
                responseBuilder.append(line);
            }

            if (responseBuilder.length() == 0) {
                return null;
            }

            resultJSONString = responseBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();

        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (client != null) {
                try {
                    client.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }

        return resultJSONString;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            // Convert the response into a JSON object.
            JSONObject jsonObject = new JSONObject(s);
            // Get the JSONArray of book items.
            JSONArray itemsArray = jsonObject.getJSONArray("items");

            // Initialize iterator and results fields.
            String title = null;
            String authors = null;
            String thumbnailURL = null;

            // Look for results in the items array, exiting when both the title and author
            // are found or when all items have been checked.
            for (int i = 0; i < itemsArray.length(); i++) {
                // Get the current item information.
                JSONObject jsonBook = itemsArray.getJSONObject(i);
                JSONObject volumeInfo = jsonBook.getJSONObject("volumeInfo");

                // Try to get the author and title from the current item,
                // catch if either field is empty and move on.
                try {
                    title = volumeInfo.getString("title");
                    authors = volumeInfo.getString("authors").replaceAll("[\\[\\]\\-\"]", "").replaceAll("[,]", ", ");
                    JSONObject bookImages = volumeInfo.getJSONObject("imageLinks");

                    if (bookImages.getString("thumbnail") != null) {
                        thumbnailURL = bookImages.getString("thumbnail");
                        Book book = new BookBuilder().setTitle(title).setAuthor(authors).setCoverLink(thumbnailURL).setIsLent(false).setOwner(SplashScreenActivity.removeSpacesFromUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())).createBook();
                        bookList.add(book);
                    } else {
                        if (bookImages.getString("smallThumbnail") != null) {
                            thumbnailURL = bookImages.getString("smallThumbnail");
                            Book book = new BookBuilder().setTitle(title).setAuthor(authors).setCoverLink(thumbnailURL).setIsLent(false).setOwner(SplashScreenActivity.removeSpacesFromUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())).createBook();
                            bookList.add(book);
                        }
                    }

                } catch (Exception e) {
                    Book book = new BookBuilder().setTitle(title).setAuthor(authors).setCoverLink(thumbnailURL).setIsLent(false).setOwner(SplashScreenActivity.removeSpacesFromUsername(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())).createBook();
                    bookList.add(book);
                }

            }

            listView.setAdapter(addBookListAdapter);

        } catch (Exception e) {
            Toast.makeText(activity, "No books were found.", Toast.LENGTH_SHORT).show();
        }
    }
}



