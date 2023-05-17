package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;

// Clase para mostrar información dentro de un foro
public class dentro_foro extends AppCompatActivity {
    // Declaración de variables y objetos
    private ImageButton bBack;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    public static EditText asuntoEdt,tituloEdt;
    public static TextView tittleEdt;
    public static TextView autorEdt;
    public static TextView contentEdt;
    public static TextView nombre_foro;
    FirebaseAuth mAuth;
    FirebaseUser currentUser;

    FirebaseFirestore db = FirebaseFirestore.getInstance();

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Establecer el layout de la actividad
        setContentView(R.layout.dentro_foro);
        // Inicializar las vistas
        tittleEdt= findViewById(R.id.titulo);
        autorEdt= findViewById(R.id.autor_foro);
        nombre_foro= findViewById(R.id.nombre_foro);
        contentEdt = findViewById(R.id.descripcion_foro);
        mAuth = FirebaseAuth.getInstance();
        bBack = findViewById(R.id.backDentroForo);
        // Establecer un listener para el botón de volver atrás
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void onStart() {
        super.onStart();
        //mostrar();
        // Check if user is signed in (non-null) and update UI accordingly.

    }

    // Método para mostrar información de una publicación en el foro
    public void mostrar(String t,String a,String c){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Obtener la referencia del documento de la publicación en Firebase
        DocumentReference docRef = db.collection("/Publicaciones/").document(t);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Si el documento existe, mostrar la información en las vistas correspondientes

                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());

                        tittleEdt.setText((CharSequence) t);
                        autorEdt.setText((CharSequence) a);
                        contentEdt.setText( c);
                        nombre_foro.setText((CharSequence) t);

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

