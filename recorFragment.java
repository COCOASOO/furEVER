package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class recorFragment extends Fragment {

    View rootView;

    private FirebaseFirestore db; // Instancia de FirebaseFirestore para acceder a Firestore
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Obtener el usuario actual de FirebaseAuth
    private AdaptadorRecordatorios adaptadorRecordatorios; // Adaptador para la lista de recordatorios
    private List<Recordatorios> myList; // Lista de recordatorios
    private RecyclerView recyclerView; // RecyclerView para mostrar los recordatorios
    Context context2; // Contexto

    public String NombreG; // Variable para almacenar el nombre
    public FloatingActionButton bCrearRecordatorio; // Botón para crear un nuevo recordatorio
    FirebaseAuth mAuth; // Instancia de FirebaseAuth para autenticación
    FirebaseUser currentUser; // Usuario actual

    public recorFragment() {
        // Constructor público vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño de este fragmento
        rootView = inflater.inflate(R.layout.recor_fragment, container, false);

        recyclerView = rootView.findViewById(R.id.mlista); // Obtener el RecyclerView del diseño
        recyclerView.setHasFixedSize(true); // Establecer que el RecyclerView tiene un tamaño fijo
        recyclerView.setLayoutManager(new LinearLayoutManager(context2)); // Establecer el administrador de diseño para el RecyclerView

        myList = new ArrayList<>(); // Inicializar la lista de recordatorios
        adaptadorRecordatorios = new AdaptadorRecordatorios(myList, getContext()); // Crear el adaptador de recordatorios
        recyclerView.setAdapter(adaptadorRecordatorios); // Establecer el adaptador en el RecyclerView

        db = FirebaseFirestore.getInstance(); // Obtener una instancia de FirebaseFirestore para acceder a Firestore
        lista(); // Llamar al método "lista" para obtener los datos de Firestore y actualizar la lista de recordatorios

        bCrearRecordatorio = rootView.findViewById(R.id.bCrearRecordatorio); // Obtener el botón para crear un nuevo recordatorio
        bCrearRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), crear_recordatorio.class); // Crear un Intent para abrir la actividad "crear_recordatorio"
                startActivity(intent); // Iniciar la actividad
            }
        });
        return rootView; // Devolver la vista raíz del fragmento
    }

    public void lista() {
        mAuth = FirebaseAuth.getInstance(); // Obtener una instancia de FirebaseAuth
        currentUser = mAuth.getCurrentUser(); // Obtener el usuario actual

        db.collection("/usuarios/"+user.getEmail()+"/Recordatorios") // Obtener la colección de recordatorios del usuario actual en Firestore
                .get() // Obtener todos los documentos de la colección
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) { // Verificar si la tarea se completó correctamente
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Recorrer todos los documentos obtenidos
                                String title = document.getString("asunto"); // Obtener el valor del campo "asunto" del documento
                                String fecha = document.getString("fecha"); // Obtener el valor del campo "fecha" del documento
                                String hora = document.getString("hora"); // Obtener el valor del campo "hora" del documento
                                String tipo = document.getString("tipo"); // Obtener el valor del campo "tipo" del documento
                                String importancia = document.getString("importancia"); // Obtener el valor del campo "importancia" del documento
                                String nombre = document.getString("nombre"); // Obtener el valor del campo "nombre" del documento

                                myList.add(new Recordatorios(1, title, fecha, hora, tipo, importancia, nombre)); // Agregar un nuevo objeto Recordatorios a la lista con los valores obtenidos
                            }
                            adaptadorRecordatorios.notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado para que actualice la vista
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException()); // Si ocurre un error al obtener los documentos, mostrar el mensaje de error en el registro
                        }
                    }
                });
    }
    public void eliminarRecor(String fecha, String hora) {
        db.collection("/usuarios/"+user.getEmail()+"/Recordatorios").document(fecha+"-"+hora) // Acceder al documento específico en la colección de recordatorios utilizando la fecha y hora proporcionadas
                .delete() // Eliminar el documento
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!"); // Registro de éxito en eliminar el documento
                        Toast.makeText(context2, "Recordatorio eliminado", Toast.LENGTH_SHORT).show(); // Mostrar un mensaje de éxito al usuario mediante un Toast
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e); // Registro de error en caso de que falle la eliminación del documento
                    }
                });
    }


    // ADAPTADOR Y VIEWHOLDER

    public class AdaptadorRecordatorios extends RecyclerView.Adapter<MyViewHolder> {
        private java.util.List<Recordatorios> myList;  // Lista de recordatorios
        private Context context;  // Contexto de la aplicación

        recorFragment met = new recorFragment();  // Instancia de la clase recorFragment
        editar_recordatorio met2 = new editar_recordatorio();  // Instancia de la clase editar_recordatorio

        public AdaptadorRecordatorios(List<Recordatorios> myList, Context context) {
            this.myList = myList;  // Asignar la lista de recordatorios proporcionada
            this.context = context;  // Asignar el contexto proporcionado
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflar el diseño del elemento de la lista a partir del archivo estilo_recor_mascota.xml
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.estilo_recor_mascota, parent, false);
            return new MyViewHolder(view);  // Crear y devolver una nueva instancia de MyViewHolder para contener los elementos de la vista
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // Obtener el elemento actual de la lista en la posición dada
            Recordatorios currentItem = myList.get(position);

            // Establecer el texto del asunto en el elemento de la vista correspondiente
            holder.asunto.setText((CharSequence)currentItem.getAsunto());
            // Establecer el texto de la importancia en el elemento de la vista correspondiente
            holder.impor.setText((CharSequence)currentItem.getImportancia());
            // Establecer el texto de la fecha en el elemento de la vista correspondiente
            holder.fecha.setText((CharSequence)currentItem.getFecha());
            // Establecer el texto de la hora en el elemento de la vista correspondiente
            holder.hora.setText((CharSequence)currentItem.getHora());
            // Establecer el texto del tipo en el elemento de la vista correspondiente
            holder.tipo.setText((CharSequence)currentItem.getTipo());
            // Obtener y asignar el nombre del recordatorio al atributo 'nombre' del holder
            holder.nombre =  currentItem.getNombre();

            // Configurar el OnClickListener para el botón de eliminar recordatorio
            holder.bEliminarRecordatorioFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    eliminarRecor(currentItem.getFecha(), currentItem.getHora());
                }
            });

            // Configurar el OnClickListener para el botón de editar recordatorio
            holder.bEditarRecordatorioFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí va el código para abrir la nueva pantalla
                    Intent intent = new Intent(getActivity(), editar_recordatorio.class);
                    startActivity(intent);
                    // Llamar al método eliminar de la instancia met2 de la clase editar_recordatorio
                    met2.eliminar(currentItem.getFecha(),currentItem.getHora());
                }
            });
        }


        @Override
        public int getItemCount() {
            // Devuelve el tamaño de la lista de elementos
            return myList.size();
        }

    }
    // Clase interna para el ViewHolder personalizado
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public String nombre;
        public TextView asunto, tipo, fecha, hora, impor;
        public TextView content;
        public Button bEditarRecordatorioFrag;
        public Button bEliminarRecordatorioFrag;

        public MyViewHolder(View view) {
            super(view);
            // Inicialización de los elementos de la vista en el ViewHolder
            asunto = itemView.findViewById(R.id.asunto_R);
            tipo = itemView.findViewById(R.id.tipoR);
            fecha = itemView.findViewById(R.id.fechaR);
            hora = itemView.findViewById(R.id.horaR);
            impor = itemView.findViewById(R.id.imporR);

            bEditarRecordatorioFrag = itemView.findViewById(R.id.bEditarRecordatorioFrag);
            bEliminarRecordatorioFrag = itemView.findViewById(R.id.bEliminarRecordatorioFrag);
        }
    }
}