package com.bemen.furever;
import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

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

public class ThirdFragment extends Fragment {

    // Vista raíz del fragmento
    View rootView;

    // Instancia de FirebaseFirestore para acceder a la base de datos
    private FirebaseFirestore db;

    // Obtiene el usuario actualmente autenticado
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Adaptador para los recordatorios en la vista principal
    private ThirdFragment.AdaptadorRecordatoriosMain adaptadorRecordatoriosMain;

    // Lista de recordatorios
    private List<Recordatorios> List;

    // RecyclerView para mostrar los recordatorios
    private RecyclerView recyclerView;

    // Contexto
    Context context2;

    // Botón flotante para crear un nuevo recordatorio
    public FloatingActionButton bCrearRecordatorio;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflar el diseño del fragmento
        rootView = inflater.inflate(R.layout.recordatorios, container, false);

        // Obtener el RecyclerView del diseño
        recyclerView = rootView.findViewById(R.id.mlista);
        recyclerView.setHasFixedSize(true);

        // Establecer el administrador de diseño para el RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(context2));

        // Crear una nueva lista de recordatorios
        List = new ArrayList<>();

        // Crear el adaptador para los recordatorios y establecerlo en el RecyclerView
        adaptadorRecordatoriosMain = new ThirdFragment.AdaptadorRecordatoriosMain(List, getContext());
        recyclerView.setAdapter(adaptadorRecordatoriosMain);

        // Obtener la instancia de FirebaseFirestore
        db = FirebaseFirestore.getInstance();

        // Obtener la lista de recordatorios
        lista();

        // Devolver la vista raíz del fragmento
        return rootView;
    }


    public void lista() {
        // Obtener la colección de recordatorios del usuario actual
        db.collection("/usuarios/" + user.getEmail() + "/Recordatorios")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            // Iterar sobre los documentos de la consulta
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                // Obtener los datos del documento
                                String title = document.getString("asunto");
                                String fecha = document.getString("fecha");
                                String hora = document.getString("hora");
                                String tipo = document.getString("tipo");
                                String importancia = document.getString("importancia");
                                String nombre = document.getString("nombre");

                                // Agregar un nuevo recordatorio a la lista
                                List.add(new Recordatorios(1, title, fecha, hora, tipo, importancia, nombre));
                            }
                            // Notificar al adaptador que los datos han cambiado
                            adaptadorRecordatoriosMain.notifyDataSetChanged();
                        } else {
                            Log.d("Firestore", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void eliminarRecor(String fecha, String hora) {
        // Eliminar el documento correspondiente al recordatorio
        db.collection("/usuarios/" + user.getEmail() + "/Recordatorios").document(fecha + "-" + hora)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Registro de éxito de eliminación
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Registro de error al eliminar el documento
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }

    // ADAPTADOR Y VIEWHOLDER

    public class AdaptadorRecordatoriosMain extends RecyclerView.Adapter<ThirdFragment.MyViewHolder> {
        private java.util.List<Recordatorios> myList;
        private Context context;

        public AdaptadorRecordatoriosMain(List<Recordatorios> myList, Context context) {
            this.myList = myList;
            this.context = context;
        }

        @NonNull
        @Override
        public ThirdFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // Inflar el diseño de cada elemento de la lista
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.estilo_recor_mascota, parent, false);
            return new ThirdFragment.MyViewHolder(view);
        }


        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
            // Obtener el recordatorio actual en la posición dada
            Recordatorios currentItem = List.get(position);

            // Asignar los valores del recordatorio a los elementos de la vista correspondientes
            holder.asunto.setText((CharSequence) currentItem.getAsunto());
            holder.impor.setText((CharSequence) currentItem.getImportancia());
            holder.fecha.setText((CharSequence) currentItem.getFecha());
            holder.hora.setText((CharSequence) currentItem.getHora());
            holder.tipo.setText((CharSequence) currentItem.getTipo());

            // Establecer el listener de clic para el botón de editar recordatorio
            holder.bEditarRecordatorioFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí va el código para abrir la nueva pantalla de edición de recordatorio
                    Intent intent = new Intent(getActivity(), editar_recordatorio.class);
                    startActivity(intent);
                }
            });

            // Establecer el listener de clic para el botón de eliminar recordatorio
            holder.bEliminarRecordatorioFrag.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Aquí va el código para eliminar el recordatorio actual
                    eliminarRecor(currentItem.getFecha(), currentItem.getHora());
                }
            });
        }

        @Override
        public int getItemCount() {
            // Devuelve el tamaño de la lista de recordatorios
            return myList.size();
        }

    }
    // Clase MyViewHolder que extiende RecyclerView.ViewHolder
    public class MyViewHolder extends RecyclerView.ViewHolder {

        // Elementos de la vista correspondientes a cada recordatorio
        public TextView asunto, tipo, fecha, hora, impor;
        public Button bEditarRecordatorioFrag;
        public Button bEliminarRecordatorioFrag;

        public MyViewHolder(View view) {
            super(view);

            // Asignar los elementos de la vista a las variables
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