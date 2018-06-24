package joao.projetos.booklend;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import android.app.AlertDialog.Builder;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SplashScreenActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuthenticator;
    private GoogleSignInClient googleSignInClient;
    private DatabaseReference rootReference;
    private DatabaseReference usersReference;
    public static final String EXTRA_FROM_SIGN_IN = "username";
    private static final int RC_SIGN_IN = 9001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen_with_google);

        GoogleSignInOptions googleSignInOptions = createGoogleSignInOptions();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

        SignInButton signInButton = findViewById(R.id.google_sign_in_button);
        signInButton.setSize(SignInButton.SIZE_WIDE);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithGoogle();
            }
        });

        firebaseAuthenticator = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        rootReference = FirebaseDatabase.getInstance().getReference();

        usersReference = rootReference.child("users");

        FirebaseUser currentUser = firebaseAuthenticator.getCurrentUser();

        if (currentUser != null) {
            String usernameWithoutSpaces = removeSpacesFromUsername(currentUser.getDisplayName());
            Intent goToDashboard = new Intent(SplashScreenActivity.this, DashboardActivity.class);
            goToDashboard.putExtra(EXTRA_FROM_SIGN_IN, usernameWithoutSpaces);
            startActivity(goToDashboard);
        }
    }

    private GoogleSignInOptions createGoogleSignInOptions() {
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        return googleSignInOptions;
    }

    private void signInWithGoogle() {
        Intent signInWithGoogleIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInWithGoogleIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account);
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SplashScreenActivity", "signInResult:failed code=" + e.getStatusCode());
        }
    }


    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        firebaseAuthenticator.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            Log.d("SplashScreenActivity", "signInWithCredential:success");
                            FirebaseUser currentUser = firebaseAuthenticator.getCurrentUser();

                            String usernameWithoutSpaces = removeSpacesFromUsername(currentUser.getDisplayName());
                            User user = new User(usernameWithoutSpaces, currentUser.getEmail());

                            usersReference.child(currentUser.getUid()).setValue(user);
                            rootReference.child("displaynames").child(currentUser.getUid()).setValue(usernameWithoutSpaces);

                            Intent toDashboard = new Intent(SplashScreenActivity.this, DashboardActivity.class);
                            toDashboard.putExtra(EXTRA_FROM_SIGN_IN, usernameWithoutSpaces);
                            startActivity(toDashboard);
                        } else {
                            //Sign in failed.
                            Log.w("SplashScreenActivity", "signInWithCredential:failure", task.getException());
                            Toast.makeText(SplashScreenActivity.this, "Authentication Failed", Toast.LENGTH_SHORT).show();
                        }


                    }
                });

    }

    public static String removeSpacesFromUsername(String username) {
        return username.replaceAll("\\s+", "");
    }

}
