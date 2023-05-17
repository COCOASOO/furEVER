package com.bemen.furever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class login extends AppCompatActivity {
    Context context;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private static final String TAG = "EmailPassword";
    public static EditText emailEditText , passwordEditText ;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private FirebaseAuth mAuth;

    private Button blogin,blogin_google,bsign;

    String email,contraseña;

    String nameAT,emailAT,uidAT;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        context = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Inicialización de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Configuración de inicio de sesión con Google
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Obtención de referencias a las vistas del diseño
        emailEditText = (EditText) findViewById(R.id.EditText_Email);
        passwordEditText = (EditText) findViewById(R.id.EditText_Pass);

        // Definición de los botones y asignación de acciones
        blogin = (Button) findViewById(R.id.login);
        bsign = (Button) findViewById(R.id.sign);
        blogin_google = (Button) findViewById(R.id.login_google);

        // Acción al hacer clic en el botón de inicio de sesión
        blogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtención del correo electrónico y la contraseña ingresados
                email = emailEditText.getText().toString();
                contraseña = passwordEditText.getText().toString();
                email = email.trim();
                contraseña = contraseña.trim();

                // Llamada al método de inicio de sesión
                signIn(email, contraseña);
            }
        });
        // Acción al hacer clic en el botón de inicio de sesión con Google
        blogin_google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamada al método de inicio de sesión con Google
                signInGoogle();
            }
        });

        // Acción al hacer clic en el botón de registro
        bsign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Llamada al método de registro
                sign();
            }
        });

    }
    // Método para redirigir a la pantalla de registro
    private void sign() {
        Intent intent = new Intent(this, signin.class);
        startActivity(intent);
    }

    // Método para iniciar sesión con Google
    private void signInGoogle() {
        // Obtiene el intent de inicio de sesión de Google
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        // Inicia la actividad de inicio de sesión con Google y espera el resultado
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    private void signIn(String email, String password) {
        // [START sign_in_with_email]
        if (email.isEmpty() || password.isEmpty()) {
            // Verifica si el correo electrónico y la contraseña están vacíos
            Toast.makeText(login.this, "Please, Fill the fields.", Toast.LENGTH_SHORT).show();
        } else {
            // Inicia el proceso de inicio de sesión con correo electrónico y contraseña
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Si el inicio de sesión es exitoso, obtén la información del usuario y actualiza la interfaz de usuario
                                Log.d(TAG, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user);
                            } else {
                                // Si el inicio de sesión falla, muestra un mensaje al usuario
                                Log.w(TAG, "signInWithEmail:failure", task.getException());
                                Toast.makeText(login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                                updateUI(null);
                            }
                        }
                    });
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Resultado devuelto al lanzar la intención desde GoogleSignInApi.getSignInIntent(...)
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // El inicio de sesión con Google fue exitoso, autenticar con Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // El inicio de sesión con Google falló, actualizar la interfaz de usuario apropiadamente
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }

        // ...
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        // Obtener las credenciales de autenticación de Google
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Si la autenticación con credenciales de Google es exitosa, obtener el usuario actual y actualizar la interfaz de usuario
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            Login();
                            updateUI(user);

                            // ...
                        } else {
                            // Si la autenticación falla, mostrar un mensaje al usuario
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            // ...
                        }

                        // ...
                    }
                });
    }

    public void onStart() {
        super.onStart();

        // Verificar si el usuario ha iniciado sesión (no es nulo) y actualizar la interfaz de usuario en consecuencia.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload();
        }
    }
    private void reload() {
        // Método para recargar la interfaz de usuario
    }

    public void Login() {
        // Método para iniciar sesión
        Intent intent = new Intent(this, main_bar.class);
        startActivity(intent);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            // Obtener el nombre, dirección de correo electrónico y URL de la foto de perfil del usuario
            nameAT = user.getDisplayName();
            emailAT = user.getEmail();
            String copia = emailAT.toString().trim();

            if (copia.equals(email)) {
                System.out.println("login correcto");
                Login(); // Iniciar sesión si el correo electrónico coincide
            } else {
                System.out.println("login incorrecto");
            }

            // Verificar si el correo electrónico del usuario está verificado
            boolean emailVerifiedAT = user.isEmailVerified();

            uidAT = user.getUid(); // Obtener el UID del usuario
        }
    }

}
