package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class infoFragment extends Fragment {
    FirebaseStorage storage = FirebaseStorage.getInstance();

    StorageReference storageRef = storage.getReference();
    StorageReference imagesRef = storageRef.child("imagenes/puñetaSI.jpg");

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    public static TextView eNombre;
    public static TextView eEdad;
    public static TextView eEsteril;
    public static TextView eFecha;
    public static TextView ePeso;
    public static TextView eRaza;
    public TextView eID;
    public static TextView eSexo;
    public Button bEditarMascota;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    View rootView;
    static ImageView imagen;

    public String nombre,raza,sexo,esteril,fecha;
    public int edad, peso;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el layout para este fragmento
        rootView = inflater.inflate(R.layout.info_fragment, container, false);
        // Inicializar los elementos de la interfaz de usuario
        bEditarMascota = rootView.findViewById(R.id.bEditarMascota);
        eNombre = rootView.findViewById(R.id.nom_perro);
        eEdad = rootView.findViewById(R.id.edad_perro);
        eRaza = rootView.findViewById(R.id.Raza_perro);
        eSexo = rootView.findViewById(R.id.sexo);
        eEsteril = rootView.findViewById(R.id.Esteril);
        eFecha = rootView.findViewById(R.id.naci);
        ePeso = rootView.findViewById(R.id.peso);
        imagen = rootView.findViewById(R.id.imagen_perro);

        // Establecer un onClickListener para el botón de editar mascota
        bEditarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad de editar mascota
                Intent intent = new Intent(getActivity(), editar_mascota.class);
                startActivity(intent);
            }
        });
        // Devolver la vista raíz
        return rootView;
    }


    public void onStart() {
        super.onStart();
        // Llamar al método mostrar
        // Verificar si el usuario ha iniciado sesión y actualizar la interfaz de usuario en consecuencia
    }

    public void mostrar(String id,String n,String e,String r,String s, String es,String f,String p,String url){
        // Obtener una instancia de FirebaseAuth y el usuario actual
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Obtener una referencia al documento en la colección de perros del usuario
        DocumentReference docRef = db.collection("/usuarios/"+user.getEmail()+"/Perros").document(id);

        // Obtener el documento y añadir un OnCompleteListener
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Si el documento existe, mostrar los datos en la interfaz de usuario
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        eNombre.setText((CharSequence) n);
                        eRaza.setText((CharSequence) r);
                        eEdad.setText( e);
                        eSexo.setText((CharSequence) s);
                        eEsteril.setText((CharSequence) es);
                        eFecha.setText((CharSequence) f);
                        ePeso.setText( p);
                        desImagen(imagen,url);
                    } else {
                        // Si el documento no existe, mostrar un mensaje en el registro
                        Log.d(TAG, "No such document");
                    }
                } else {
                    // Si la tarea falla, mostrar un mensaje en el registro
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    public void desImagen (ImageView imagen, String url){
        // Cargar la imagen desde la URL en el ImageView utilizando Picasso
        Picasso.get().load(url).into(imagen);
    }

    private void reload() {
        // Método para recargar
    }

}
