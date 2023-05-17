package com.bemen.furever;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class foros extends Fragment {
    Context context;
    private RecyclerView recyclerView;
    FloatingActionButton bCrearForo;
    FloatingActionButton bEditarForo;

    private AdaptadorForos AdaptadorForos;

    private List<Publicacion> List;

    private FirebaseFirestore db;

    DatabaseReference ref;

    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    SearchView searchView;

    View rootView, rootView2;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar la vista para el diseño foros
        rootView = inflater.inflate(R.layout.foros, container, false);

        // Encontrar el RecyclerView por su ID y configurarlo
        recyclerView = rootView.findViewById(R.id.mlistaF);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        // Encontrar el SearchView por su ID
        searchView = rootView.findViewById(R.id.buscarForos);

        // Crear una nueva Lista y un nuevo AdaptadorForos y asignarlo al RecyclerView
        List = new ArrayList<>();
        AdaptadorForos = new AdaptadorForos(List);
        recyclerView.setAdapter(AdaptadorForos);

        // Obtener una instancia de FirebaseFirestore y llamar al método lista_For
        db = FirebaseFirestore.getInstance();
        lista_For();

        // Encontrar el botón flotante para crear un foro por su ID y asignarle un OnClickListener
        bCrearForo = rootView.findViewById(R.id.bCrearForo);
        bCrearForo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad crear_foro al hacer clic en el botón
                Intent intent = new Intent(getActivity(), crear_foro.class);
                startActivity(intent);
            }
        });

        // Encontrar el botón para editar foros por su ID y asignarle un OnClickListener
        bEditarForo = rootView.findViewById(R.id.bMisForos);
        bEditarForo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Iniciar la actividad mis_foros al hacer clic en el botón
                Intent intent = new Intent(getActivity(), mis_foros.class);
                startActivity(intent);
            }
        });

        // Devolver la vista inflada
        return rootView;
    }



    public void onStart() {
        super.onStart();
        // Verificar si la referencia no es nula
        if(ref != null){
            // Añadir un ValueEventListener a la referencia
            ref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Código para manejar los cambios en los datos
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    // Código para manejar errores en la base de datos
                }
            });
        }
        // Verificar si searchView no es nulo
        if (searchView != null)
        {
            // Establecer un OnQueryTextListener para searchView
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    // Código para manejar la presentación de la consulta de búsqueda
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    // Buscar el nuevo texto y devolver verdadero
                    search(newText);
                    return true;
                }
            });
        }
    }


    // Método search que recibe un parámetro: str
    private void search(String str){
        // Crear una lista vacía de objetos Publicacion
        ArrayList<Publicacion> myList =new ArrayList<>();

        // Recorrer la lista List de objetos Publicacion
        for (Publicacion object : List){
            // Si el título del objeto Publicacion contiene la cadena de texto str (ignorando mayúsculas y minúsculas)
            if (object.getTitulo().toLowerCase().contains(str.toLowerCase()))
            {
                // Agregar el objeto Publicacion a la lista myList
                myList.add(object);
            }
        }

        // Crear un objeto AdaptadorForos con la lista myList
        AdaptadorForos adaptadorForos = new AdaptadorForos(myList);

        // Establecer el adaptador del objeto recyclerView como el objeto adaptadorForos
        recyclerView.setAdapter(adaptadorForos);
    }

    // Método lista_For
    public void lista_For() {
        // Obtener todos los documentos de la colección "Publicaciones"
        db.collection("Publicaciones")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Si se obtienen los documentos correctamente, se ejecutará el siguiente código

                            // Recorrer los documentos obtenidos
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener los valores de los campos titulo, categoria, contenido y usuario del documento
                                String titulo = document.getString("titulo");
                                String categoria = document.getString("categoria");
                                String content = document.getString("contenido");
                                String usuario = document.getString("usuario");

                                // Agregar un nuevo objeto Publicacion a la lista List con los valores obtenidos del documento
                                List.add(new Publicacion(titulo, categoria, content, usuario));
                            }

                            // Notificar al adaptador que los datos han cambiado
                            AdaptadorForos.notifyDataSetChanged();
                        } else {
                            // Si ocurre un error al obtener los documentos, se ejecutará el siguiente código

                            // Mostrar un mensaje en el log indicando que ocurrió un error
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }


    // ADAPTADOR Y VIEWHOLDER

    public class AdaptadorForos extends RecyclerView.Adapter<foros.MyViewHolder> {
        // Campos privados para una Lista de objetos Publicacion y un objeto Context
        private java.util.List<Publicacion> List;
        private Context context;
        // Instancia de la clase dentro_foro
        dentro_foro met = new dentro_foro();

        // Constructor para la clase AdaptadorForos que toma una Lista de objetos Publicacion
        public AdaptadorForos(List<Publicacion> List) {
            this.List = List;
            this.context = context;
        }

        @NonNull
        @Override
        // Método para crear un nuevo ViewHolder para el RecyclerView
        public foros.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflar las vistas para los diseños estilo_foros y dentro_foro
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.estilo_foros, parent, false);
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.dentro_foro, parent, false);

            // Encontrar vistas por sus IDs y asignarlas a campos en la clase dentro_foro
            dentro_foro.tittleEdt = rootView.findViewById(R.id.titulo);
            dentro_foro.autorEdt = rootView.findViewById(R.id.autor_foro);
            dentro_foro.nombre_foro = rootView.findViewById(R.id.nombre_foro);
            dentro_foro.contentEdt = rootView.findViewById(R.id.descripcion_foro);

            // Devolver una nueva instancia de la clase foros.MyViewHolder con la vista para el diseño estilo_foros
            return new foros.MyViewHolder(view);
        }

    @Override
        public void onBindViewHolder(@NonNull foros.MyViewHolder holder, int position) {
            // Obtener el elemento actual de la lista
            Publicacion currentItem = List.get(position);

            // Establecer el texto para el título, usuario y categoría
            holder.titulo.setText((CharSequence) currentItem.getTitulo());
            holder.usuario.setText((CharSequence) currentItem.getUsuario());
            holder.categoria.setText((CharSequence) currentItem.getCategoria());

            // Establecer un onClickListener para el botón para entrar al foro
            holder.bEntrarForo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí va el código para abrir una nueva pantalla
                    Intent intent = new Intent(getActivity(), dentro_foro.class);
                    startActivity(intent);
                    met.mostrar(currentItem.getTitulo(),currentItem.getUsuario(),currentItem.getContenido());
                }
            });
        }

        @Override
        public int getItemCount() {
            // Devolver el tamaño de la lista
            return List.size();
        }

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // Declarar TextViews y Botón
        public TextView titulo, categoria, contenido, usuario;
        public TextView content;
        public Button bEntrarForo;

        public MyViewHolder(View view) {
            super(view);
            // Inicializar TextViews y Botón
            titulo = itemView.findViewById(R.id.nombre_foro);
            categoria = itemView.findViewById(R.id.tipo_foro);
            usuario = itemView.findViewById(R.id.autor_foro);
            bEntrarForo = itemView.findViewById(R.id.bEntrarForo);
        }
    }

}

