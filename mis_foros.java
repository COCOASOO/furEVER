package com.bemen.furever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class mis_foros extends AppCompatActivity {
    Context context;
    private Spinner spinner;
    private ImageButton bBack;
    FloatingActionButton bCrearForo2;
    View rootView;

    private AdaptadorMisForos AdaptadorMisForos;

    private List<Publicacion> List;

    private FirebaseFirestore db;

    private RecyclerView recyclerView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.mis_foros); // Establece el diseño de la actividad como mis_foros.xml

        recyclerView = findViewById(R.id.mlistaMF); // Obtiene la referencia del RecyclerView desde el layout
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context)); // Establece un LinearLayoutManager para el RecyclerView

        List = new ArrayList<>(); // Crea una nueva lista vacía
        AdaptadorMisForos = new AdaptadorMisForos(List); // Crea una instancia del adaptador AdaptadorMisForos y pasa la lista
        recyclerView.setAdapter(AdaptadorMisForos); // Establece el adaptador en el RecyclerView

        db = FirebaseFirestore.getInstance(); // Obtiene una instancia de FirebaseFirestore

        lista_For(); // Llama al método lista_For() para cargar la lista de foros

        bBack = findViewById(R.id.backMisForos); // Obtiene la referencia del botón "backMisForos" desde el layout
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Configura la acción al hacer clic en el botón para volver atrás
            }
        });

        bCrearForo2 = findViewById(R.id.bCrearForo2); // Obtiene la referencia del botón "bCrearForo2" desde el layout
        bCrearForo2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mis_foros.this, crear_foro.class); // Crea un Intent para ir a la actividad de crear_foro
                startActivity(intent); // Inicia la actividad de crear_foro
            }
        });
    }
    public void lista_For() {
        db.collection("/usuarios/"+user.getEmail()+"/Publicaciones") // Accede a la colección "Publicaciones" dentro del usuario actual
                .get() // Obtiene los documentos de la colección
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // El proceso de obtención de documentos fue exitoso
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Itera sobre cada documento obtenido
                                String titulo = document.getString("titulo"); // Obtiene el valor del campo "titulo" del documento
                                String categoria = document.getString("categoria"); // Obtiene el valor del campo "categoria" del documento
                                String content = document.getString("contenido"); // Obtiene el valor del campo "contenido" del documento
                                String usuario = document.getString("id_Usuario"); // Obtiene el valor del campo "id_Usuario" del documento
                                List.add(new Publicacion(titulo, categoria, content, usuario)); // Agrega una nueva Publicacion a la lista usando los valores obtenidos
                            }
                            AdaptadorMisForos.notifyDataSetChanged(); // Notifica al adaptador que los datos han cambiado y debe actualizar la vista
                        } else {
                            // Hubo un error al obtener los documentos
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    // ADAPTADOR Y VIEWHOLDER

    public class AdaptadorMisForos extends RecyclerView.Adapter<MyViewHolder> {
        private java.util.List<Publicacion> List; // Lista de publicaciones
        private Context context; // Contexto de la aplicación
        dentro_foro met = new dentro_foro(); // Instancia de la clase dentro_foro

        public AdaptadorMisForos(List<Publicacion> List) {
            this.List = List; // Asigna la lista de publicaciones
            this.context = context; // Asigna el contexto (¡Ojo! No está inicializado)
        }

        @NonNull
        @Override
        public mis_foros.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.estilo_foros, parent, false);
            // Infla el diseño de estilo_foros.xml para cada elemento del RecyclerView
            return new MyViewHolder(view); // Retorna una nueva instancia de MyViewHolder
        }


        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            Publicacion currentItem = List.get(position); // Obtiene la publicación actual según la posición

            holder.titulo.setText((CharSequence) currentItem.getTitulo()); // Establece el título en el TextView correspondiente
            holder.usuario.setText((CharSequence) currentItem.getUsuario()); // Establece el usuario en el TextView correspondiente
            holder.categoria.setText((CharSequence) currentItem.getCategoria()); // Establece la categoría en el TextView correspondiente

            holder.bEntrarForo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí va el código para abrir la nueva pantalla
                    Intent intent = new Intent(mis_foros.this, dentro_foro.class); // Crea un Intent para abrir la actividad dentro_foro
                    startActivity(intent); // Inicia la actividad dentro_foro
                    met.mostrar(currentItem.getTitulo(),currentItem.getUsuario(),currentItem.getContenido()); // Llama al método mostrar() de la instancia de dentro_foro
                }
            });
        }

        @Override
        public int getItemCount() {
            return List.size(); // Retorna el tamaño de la lista de publicaciones
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titulo, categoria, usuario;
        public Button bEntrarForo;

        public MyViewHolder(View view) {
            super(view);
            titulo = itemView.findViewById(R.id.nombre_foro); // TextView para el título del foro
            categoria = itemView.findViewById(R.id.tipo_foro); // TextView para la categoría del foro
            usuario = itemView.findViewById(R.id.autor_foro); // TextView para el usuario del foro

            bEntrarForo = itemView.findViewById(R.id.bEntrarForo); // Botón para entrar al foro
        }
    }

}


