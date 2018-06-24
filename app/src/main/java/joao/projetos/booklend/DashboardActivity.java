package joao.projetos.booklend;

import android.content.Intent;
import android.graphics.Color;

import android.app.Fragment;
import android.app.FragmentTransaction;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;


public class DashboardActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        Intent fromSplashScreen = getIntent();
        String username = fromSplashScreen.getStringExtra(SplashScreenActivity.EXTRA_FROM_SIGN_IN);

        Toolbar actionBar = findViewById(R.id.action_toolbar);

        actionBar.setTitle(username);
        actionBar.setTitleTextColor(Color.rgb(255, 255, 255));
        setSupportActionBar(actionBar);


        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container, LibraryFragment.newInstance());
        transaction.commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.dashboard_bottom_navigation_bar);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment selectedFragment = null;
                switch (item.getItemId()) {
                    case R.id.dashboard_menu_my_books:
                        selectedFragment = LibraryFragment.newInstance();
                        break;

                    case R.id.dashboard_menu_requests:
                        selectedFragment = RequestsFragment.newInstance();
                        break;

                    case R.id.dashboard_menu_add_books:
                        selectedFragment = AddBookFragment.newInstance();
                        break;

                    case R.id.dashboard_menu_search_lenders:
                        selectedFragment = SearchLendersFragment.newInstance();
                        break;
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, selectedFragment);
                transaction.commit();

                return true;
            }
        });

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {

            }
        });
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_bar_logout:
                FirebaseAuth.getInstance().signOut();
                Intent backToSplashScreen = new Intent(DashboardActivity.this, SplashScreenActivity.class);
                startActivity(backToSplashScreen);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar_menu, menu);
        return true;
    }
}
