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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class LoginScreenActivity extends AppCompatActivity {

    private EditText emailForm, passwordForm;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        firebaseAuth = FirebaseAuth.getInstance();

        emailForm = findViewById(R.id.login_email_form);
        passwordForm = findViewById(R.id.login_password_form);


    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();

        if (user != null) {
            startActivity(new Intent(this, DashboardActivity.class));
        }
    }

    public void login(View v) {

        final Intent toDashboard = new Intent(this, DashboardActivity.class);

        String userEmail = emailForm.getText().toString();
        String userPassword = passwordForm.getText().toString();

        firebaseAuth.signInWithEmailAndPassword(userEmail, userPassword).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success
                    startActivity(toDashboard);
                } else {
                    if ((task.getException() instanceof FirebaseAuthInvalidUserException)) {
                        Toast.makeText(LoginScreenActivity.this, "The account corresponding to this e-mail does not exist or it has been disabled.", Toast.LENGTH_LONG).show();
                    } else {
                        if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(LoginScreenActivity.this, "The password you entered is invalid, try again.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });

    }
}
