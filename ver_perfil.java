package com.bemen.furever;
import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

public class ver_perfil extends Fragment {

    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Instancia de Firestore para acceder a la base de datos
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Obtener el usuario actualmente autenticado

    private EditText eNombre, eEmail, ePassword; // Campos de texto para el nombre, email y contraseña
    private Button editar; // Botón para editar
    View rootView; // Vista raíz

    public FirebaseAuth mAuth; // Instancia de FirebaseAuth para la autenticación
    FirebaseUser currentUser; // Usuario actualmente autenticado
    public Button bEditar; // Botón para editar
    public ImageButton imagen; // Botón de imagen

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.ver_perfil, container, false); // Inflar el diseño de la vista de perfil
        bEditar = rootView.findViewById(R.id.bEditarPerfil); // Obtener referencia al botón de edición de perfil
        bEditar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), editar_perfil.class); // Crear un intent para abrir la actividad de edición de perfil
                startActivity(intent); // Iniciar la actividad de edición de perfil
            }
        });
        return rootView; // Devolver la vista raíz actualizada
    }

    public void onStart() {
        super.onStart();
        mostrar(); // Llamar al método para mostrar información
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = mAuth.getCurrentUser(); // Obtener el usuario actualmente autenticado
        if(currentUser != null){
            reload(); // Si el usuario no es nulo, recargar la interfaz de usuario
        }
    }

    public void mostrar (){
        mAuth = FirebaseAuth.getInstance(); // Obtener instancia de FirebaseAuth
        currentUser = mAuth.getCurrentUser(); // Obtener usuario actualmente autenticado
        eNombre = rootView.findViewById(R.id.ouput_nombre); // Obtener referencia al campo de texto para el nombre
        eEmail = rootView.findViewById(R.id.ouput_email); // Obtener referencia al campo de texto para el correo electrónico
        ePassword = rootView.findViewById(R.id.ouput_password); // Obtener referencia al campo de texto para la contraseña
        imagen = rootView.findViewById(R.id.fotoperfil); // Obtener referencia a la imagen de perfil

        DocumentReference docRef = db.collection("usuarios").document(user.getEmail()); // Obtener referencia al documento del usuario en la colección "usuarios"

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult(); // Obtener el documento
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData()); // Imprimir los datos del documento en el registro
                        String nombre = document.getString("nombre"); // Obtener el valor del campo "nombre"
                        String email = document.getId(); // Obtener el ID del documento como el correo electrónico
                        String pass = document.getString("contraseña"); // Obtener el valor del campo "contraseña"
                        eNombre.setText((CharSequence) nombre); // Establecer el nombre en el campo de texto correspondiente
                        eEmail.setText((CharSequence) email); // Establecer el correo electrónico en el campo de texto correspondiente
                        ePassword.setText((CharSequence) pass); // Establecer la contraseña en el campo de texto correspondiente

                        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult(); // Obtener el documento
                                    if (document.exists()) {
                                        // El documento existe, podemos acceder a sus campos
                                        String campoString = document.getString("urlfotoperfil"); // Obtener el valor del campo "urlfotoperfil"
                                        desImagen(imagen, campoString); // Llamar a un método para procesar la imagen con el campoString
                                        // hacer algo con el campoString
                                    } else {
                                        System.out.println("no existe el documento"); // Imprimir un mensaje indicando que el documento no existe
                                    }
                                } else {
                                    System.out.println("error en la task"); // Imprimir un mensaje indicando un error en la tarea
                                }
                            }
                        });
                    } else {
                        Log.d(TAG, "No such document"); // Imprimir mensaje iinformando que no hay documento
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException()); // Dar mensaje con la excepción
                }
            }
        });

    }
    public void desImagen(ImageButton imagen, String url) {
        Picasso.get().load(url).into(imagen); // Cargar la imagen desde la URL y asignarla al ImageButton utilizando Picasso
    }


    private void reload() { }
}