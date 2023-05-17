package com.bemen.furever;
// Declara el paquete en el que se encuentra la clase crear_foro

import static android.content.ContentValues.TAG;
// Importa la constante TAG para el uso de registros de depuración

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
// Importa las clases necesarias para la actividad de la aplicación

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;
// Importa las clases y widgets de la interfaz de usuario necesarios

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
// Importa las clases necesarias para trabajar con Firebase

public class crear_foro extends AppCompatActivity {
    // Declara la clase crear_foro y la hace extender AppCompatActivity

    Context context;
    private Spinner spinner;
    private ImageButton bBack;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // Declara e inicializa las instancias necesarias para trabajar con Firebase

    private EditText autorEdt, contetEdt, tituloEdt;
    // Declara los EditText utilizados en la actividad

    private Button bGuardar;
    String autor, content, categoria, titulo, selectedItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Método que se llama cuando se crea la actividad

        setContentView(R.layout.crear_foro);
        // Establece el diseño de la actividad

        spinner = findViewById(R.id.spinner);
        // Busca y asigna el Spinner en la interfaz de usuario

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_array, android.R.layout.simple_spinner_item);
        // Crea un adaptador para el Spinner utilizando un array de opciones definido en los recursos

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        // Configura el adaptador para el Spinner y lo asigna

        contetEdt = findViewById(R.id.input_contenido);
        tituloEdt = findViewById(R.id.input_titulo);
        // Busca y asigna los EditText en la interfaz de usuario

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = parent.getItemAtPosition(position).toString();
                // Obtiene el elemento seleccionado del Spinner y lo guarda en selectedItem
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(crear_foro.this, "Seleccione una categoria",
                        Toast.LENGTH_SHORT).show();
                // Muestra un mensaje si no se selecciona ninguna categoría en el Spinner
            }
        });
        bGuardar = (Button) findViewById(R.id.button_aceptar);
        // Busca y asigna el botón "Guardar" en la interfaz de usuario

        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoria = selectedItem;
                autor = user.getEmail();
                content = contetEdt.getText().toString().trim();
                titulo = tituloEdt.getText().toString().trim();
                // Obtiene los valores de los EditText y los asigna a las variables correspondientes

                upFirebaseAll(autor, content, categoria, titulo);
                upFirebaseUser(autor, content, categoria, titulo);
                // Llama a los métodos para almacenar los datos en Firebase

                onBackPressed();
                // Vuelve a la actividad anterior
            }
        });

        bBack = findViewById(R.id.backCrearForo);
        // Busca y asigna el botón "Volver" en la interfaz de usuario

        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                // Vuelve a la actividad anterior
            }
        });
    }

    private void upFirebaseAll(String a, String cont, String c, String t) {
        // Método para almacenar la publicación en la colección "Publicaciones" de Firebase

        Publicacion publi = new Publicacion(t, c, cont, a);
        // Crea un objeto Publicacion con los datos proporcionados

        db.collection("/Publicaciones").document(t)
                .set(publi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(crear_foro.this, "Publicacion creada",
                                Toast.LENGTH_SHORT).show();
                        // Muestra un mensaje de éxito cuando se guarda correctamente la publicación en Firebase
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        // Registra un mensaje de error si falla el almacenamiento en Firebase
                    }
                });
    }

    private void upFirebaseUser(String a, String cont, String c, String t) {
        // Método para almacenar la publicación en la colección "Publicaciones" del usuario actual en Firebase

        Publicacion publi = new Publicacion(t, c, cont, a);
        // Crea un objeto Publicacion con los datos proporcionados

        db.collection("/usuarios/" + user.getEmail() + "/Publicaciones").document(t)
                .set(publi)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(crear_foro.this, "Publicacion creada",
                                Toast.LENGTH_SHORT).show();
                        // Muestra un mensaje de éxito cuando se guarda correctamente la publicación en Firebase
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                        // Registra un mensaje de error si falla el almacenamiento en Firebase
                    }
                });
    }
}
