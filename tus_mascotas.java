package com.bemen.furever;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class tus_mascotas extends Fragment {

    // Contexto
    Context context2;

    // RecyclerView y adaptador
    private RecyclerView recyclerView;
    private DocumentAdapter documentAdapter;

    // Lista de perros
    private List<Perros> documentList;

    // Instancia de FirebaseFirestore
    private FirebaseFirestore db;

    // Botón de mascota
    private Button boton_mascota;

    // Instancia de FirebaseUser para obtener el usuario actual
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Vista raíz
    View rootView;

    // Referencia a la colección de perros del usuario
    CollectionReference collectionRef = FirebaseFirestore.getInstance().collection("/usuarios/"+user.getEmail()+"/Perros");

    // Botón de editar mascota
    FloatingActionButton bEditarMascota;

    public void onStart() {
        super.onStart();

        // Agregamos un SnapshotListener a la referencia de la colección de perros
        collectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    // Si ocurre un error al escuchar los cambios en la colección, se registra un mensaje de error
                    Log.w(TAG, "Listen failed.", e);
                    return;
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inflar el diseño del fragmento tus_mascotas
        rootView = inflater.inflate(R.layout.tus_mascotas, container, false);

        // Inflar otro diseño de mascotas (estilo_mascotas) - ¿Se utiliza en otro lugar?
        View otroLayout = LayoutInflater.from(getContext()).inflate(R.layout.estilo_mascotas, null);

        // Registrar los fragmentos
        recyclerView = rootView.findViewById(R.id.mlista);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context2));

        documentList = new ArrayList<>();
        documentAdapter = new DocumentAdapter(documentList, getContext());
        recyclerView.setAdapter(documentAdapter);

        db = FirebaseFirestore.getInstance();

        // Llamar al método lista_Mas() para obtener la lista de mascotas
        lista_Mas();

        bEditarMascota = rootView.findViewById(R.id.bCrearMascota);
        bEditarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se crea un intent para abrir la actividad ins_mascota
                Intent intent = new Intent(getActivity(), ins_mascota.class);
                startActivity(intent);
            }
        });

        boton_mascota = otroLayout.findViewById(R.id.button_mascota);
        boton_mascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se crea un nuevo fragmento infoFragment
                Fragment fragment = new infoFragment();
                // Se inicia una transacción para reemplazar el fragmento actual con el nuevo fragmento
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.fragmento_mascotas, fragment);
                // Se agrega la transacción al historial de retroceso
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return rootView;
    }

    public void lista_Mas() {
        // Obtener la colección de perros del usuario actual
        db.collection("/usuarios/" + user.getEmail() + "/Perros")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Si la tarea fue exitosa, se obtienen los documentos de la colección
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener los valores de los campos del documento
                                String title = document.getString("nombre");
                                String edad = document.getString("edad");
                                String est = document.getString("esterilización");
                                String fecha = document.getString("fecha_nacimiento");
                                String peso = document.getString("peso");
                                String raza = document.getString("raza");
                                String sexo = document.getString("sexo");
                                String url = document.getString("urlfotoperfil");

                                // Crear un nuevo objeto Perros con los valores obtenidos y agregarlo a la lista documentList
                                documentList.add(new Perros(title, title, edad, est, fecha, peso, raza, sexo, url));
                            }

                            // Notificar al adaptador que los datos han cambiado
                            documentAdapter.notifyDataSetChanged();
                        } else {
                            // Si ocurrió un error, imprimir el mensaje de error en el registro
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

}