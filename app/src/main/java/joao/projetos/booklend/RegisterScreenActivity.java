package joao.projetos.booklend;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterScreenActivity extends AppCompatActivity {


    private FirebaseAuth firebaseAuth;
    private DatabaseReference userReference;
    private EditText usernameForm, emailForm, passwordForm, passwordConfirmationForm;
    private static final Pattern PASSWORD_REGEX = Pattern.compile("^(?=.+\\d)(?=.*[A-Z])(?=.*[a-z])([^\\s]){8,16}");
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");
    private List<String> usernameList;
    public String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_screen);

        usernameList = new ArrayList<>();
        firebaseAuth = FirebaseAuth.getInstance();

        usernameForm = findViewById(R.id.username_register_form);
        emailForm = findViewById(R.id.email_register_form);
        passwordForm = findViewById(R.id.password_register_form);
        passwordConfirmationForm = findViewById(R.id.password_confirm_register_form);

    }

    @Override
    protected void onStart() {
        super.onStart();

        DatabaseReference rootReference = FirebaseDatabase.getInstance().getReference();

        if (rootReference.child("users") == null) {
            rootReference.child("users").push();
        }

            userReference = rootReference.child("users");

        userReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                getUsersFromDatabase(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(RegisterScreenActivity.this, "A database error has occurred, please try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void register(View v) {

        String username = usernameForm.getText().toString();
        String email = emailForm.getText().toString();
        String password = passwordForm.getText().toString();
        String passwordConfirmation = passwordConfirmationForm.getText().toString();

        boolean isTaken = doesUserExist(usernameList, username);

        final Matcher passwordMatcher = PASSWORD_REGEX.matcher(password);
        final Matcher emailMatcher = EMAIL_REGEX.matcher(email);

        validateRegistration(isTaken, emailMatcher.matches(), passwordMatcher.matches(), password.equals(passwordConfirmation), username, email, password);

    }

    private void createAccount(final String email, String password) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //Sign in successful
                            firebaseAuth = FirebaseAuth.getInstance();
                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                            User user = new User(usernameForm.getText().toString(), email);
                            userReference.child(firebaseUser.getUid()).push().setValue(user);



                            //Toast.makeText(RegisterScreenActivity.this, "Registration successful.",
                                    //Toast.LENGTH_SHORT).show();

                        } else {
                            //Failed sign in
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(RegisterScreenActivity.this, "There's already an user with that e-mail address.", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(RegisterScreenActivity.this, "There's a network issue or there's already an account with that e-mail.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }

                    }
                });

        Intent toDashboard = new Intent(this, DashboardActivity.class);
        startActivity(toDashboard);

    }

    private void getUsersFromDatabase(DataSnapshot dataSnapshot) {
        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
            String user = snapshot.getKey();
            usernameList.add(user);
        }
    }

    private boolean doesUserExist(List<String> usernameArrayList, String usernameToVerify) {
        for (String username : usernameArrayList) {
            if (usernameToVerify.toUpperCase().equals(username.toUpperCase())) {
                return true;
            }
        }
        return false;
    }

    private void validateRegistration(boolean isUsernameTaken, boolean isEmailValid, boolean isPasswordValid, boolean doPasswordsMatch, String username, String email, String password) {
        if (isUsernameTaken) {
            Toast.makeText(RegisterScreenActivity.this, "That username is already taken.", Toast.LENGTH_SHORT).show();
            return;
        } else {
            if (!isEmailValid) {
                Toast.makeText(RegisterScreenActivity.this, "Please enter a valid e-mail.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                if (!isPasswordValid) {
                    Toast.makeText(RegisterScreenActivity.this, "Password must be at least 8 characters long and have at least 1 upper case and 1 lower case character.", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    if (!doPasswordsMatch) {
                        Toast.makeText(RegisterScreenActivity.this, "The passwords don't match, try again.", Toast.LENGTH_SHORT).show();
                        return;

                    }else{
                        createAccount(email, password);
                    }
                }
            }
        }

    }

}
