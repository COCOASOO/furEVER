package com.bemen.furever;

import android.annotation.SuppressLint;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class signin extends AppCompatActivity {
    private FirebaseAuth mAuth;  // Objeto para la autenticación de Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance();  // Instancia de la base de datos de Firebase
    private static final String TAG = "EmailPassword";  // Etiqueta para mensajes de registro
    public static EditText nombreEditText, emailEditText, passwordEditText, passwordEditTextVal;  // Campos de entrada de texto en la interfaz de usuario
    private ImageButton bBack;  // Botón de retroceso
    private Button bSign;  // Botón de inicio de sesión
    String email, contraseña, contraseña2, nombre;  // Variables para almacenar los datos de correo electrónico, contraseña y nombre
    @SuppressLint("MissingInflatedId")  // Anotación para indicar que se está ignorando la falta de un ID inflado en este contexto

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);

        mAuth = FirebaseAuth.getInstance();  // Obtener instancia de autenticación de Firebase

        emailEditText = (EditText) findViewById(R.id.input_email);  // Campo de entrada de correo electrónico en la interfaz de usuario
        passwordEditText = (EditText) findViewById(R.id.input_password);  // Campo de entrada de contraseña en la interfaz de usuario
        passwordEditTextVal = (EditText) findViewById(R.id.input_password2);  // Campo de entrada de confirmación de contraseña en la interfaz de usuario
        nombreEditText = (EditText) findViewById(R.id.input_nombre);  // Campo de entrada de nombre en la interfaz de usuario

        bBack = (ImageButton) findViewById(R.id.back);  // Botón de retroceso en la interfaz de usuario
        bSign = (Button) findViewById(R.id.button_signin);  // Botón de inicio de sesión en la interfaz de usuario

        bSign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailEditText.getText().toString();  // Obtener el valor del correo electrónico ingresado
                contraseña = passwordEditText.getText().toString().trim();  // Obtener el valor de la contraseña ingresada
                contraseña2 = passwordEditTextVal.getText().toString().trim();  // Obtener el valor de la confirmación de contraseña ingresada
                nombre = nombreEditText.getText().toString().trim();  // Obtener el valor del nombre ingresado

                if (contraseña.equals(contraseña2)) {  // Verificar si las contraseñas coinciden
                    email = email.trim();  // Eliminar espacios en blanco al inicio y al final del correo electrónico
                    contraseña = contraseña.trim();  // Eliminar espacios en blanco al inicio y al final de la contraseña
                    createAccount(email, contraseña);  // Crear cuenta utilizando el correo electrónico y la contraseña proporcionados
                    upFirebase(contraseña, nombre);  // Actualizar datos en Firebase con la contraseña y el nombre
                } else {
                    Toast.makeText(signin.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                back();
            }
        });
    }

    private void upFirebase(String contraseña, String nombre) {
        // Crea una instancia del objeto "usuario" con los datos proporcionados
        usuario user = new usuario(nombre, email, "usuario", contraseña);

        // Guarda el objeto "user" en la colección "usuarios" y utiliza el email como identificador del documento
        db.collection("usuarios").document(email)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // La operación de escritura en Firestore se realizó exitosamente
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Ocurrió un error al escribir el documento en Firestore
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void createAccount(String email, String password) {
        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Registro exitoso, actualiza la interfaz de usuario con la información del usuario registrado
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // Si el registro falla, muestra un mensaje al usuario
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(signin.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        // Actualiza la interfaz de usuario y redirige a la actividad principal
        Intent intent= new Intent(this, login.class);
        startActivity(intent);
    }

    private void back() {
        // Redirige a la actividad principal
        Intent intent= new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}