package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bemen.furever.Perros;
import com.bemen.furever.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.DocumentViewHolder> {
    public String id;
    private List<Perros> documentList;
    private Context contexto;

    // Definición de la interfaz OnItemClickListener
    private OnItemClickListener mListener;

    // Instancias de los fragmentos (no se requiere comentario)
    infoFragment met = new infoFragment();
    recorFragment met2 = new recorFragment();
    dietFragment met3 = new dietFragment();

    public DocumentAdapter(){

    }
    public DocumentAdapter(List<Perros> documentList, Context contexto) {
        this.documentList = documentList;
        this.contexto = contexto;
    }

    // Definición de la interfaz OnItemClickListener
    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    // Método para establecer el listener del click en los elementos del adaptador
    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    @NonNull
    @Override
    public DocumentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.estilo_mascotas, parent, false);
        // esto no
        View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.info_fragment, parent, false);
        View rootView2 = LayoutInflater.from(parent.getContext()).inflate(R.layout.diet_fragment, parent, false);
        View rootView3 = LayoutInflater.from(parent.getContext()).inflate(R.layout.recor_fragment, parent, false);
        // esto no
        dietFragment.edadEdt = rootView2.findViewById(R.id.input_edad);
        dietFragment.pesoEdt = rootView2.findViewById(R.id.input_peso);
        // esto no
        infoFragment.eNombre = rootView.findViewById(R.id.nom_perro);
        infoFragment.eEdad = rootView.findViewById(R.id.edad_perro);
        infoFragment.eRaza = rootView.findViewById(R.id.Raza_perro);
        infoFragment.eSexo = rootView.findViewById(R.id.sexo);
        infoFragment.eEsteril = rootView.findViewById(R.id.Esteril);
        infoFragment.eFecha = rootView.findViewById(R.id.naci);
        infoFragment.ePeso = rootView.findViewById(R.id.peso);
        infoFragment.imagen = rootView.findViewById(R.id.imagen_perro);
        return new DocumentViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull DocumentViewHolder holder, int position) {
        // Obtener el objeto Perros en la posición actual
        Perros currentItem = documentList.get(position);
        // Establecer el nombre del perro en el TextView correspondiente
        holder.title.setText((CharSequence) currentItem.getNombre());
        // Cargar la imagen del perro con Picasso
        Picasso.get().load(currentItem.geturlImagen()).into(holder.imagen2);

        // Establecer un listener para el botón de la tarjeta de la mascota
        holder.bTarjetaMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al hacer clic en el botón, iniciar la actividad mascotas_bar y mostrar información de la mascota seleccionada

                Intent intent = new Intent(contexto, mascotas_bar.class);
                contexto.startActivity(intent);
                //met2.NombreG = currentItem.getNombre();
                // met2.lista(currentItem.getNombre());
                met.mostrar(currentItem.getNombre(), currentItem.getNombre(), currentItem.getEdad(), currentItem.getRaza(), currentItem.getSexo(), currentItem.getEsterilización(), currentItem.getFecha_nacimiento(), currentItem.getPeso(), currentItem.geturlImagen());
                // met3.mostrar(currentItem.getNombre(), currentItem.getEdad(), currentItem.getPeso());

            }
        });
    }

    @Override
    public int getItemCount() {
        // Devolver el tamaño de la lista de perros
        return documentList.size();
    }

    // Clase interna para representar una vista de elemento en el RecyclerView
    public static class DocumentViewHolder extends RecyclerView.ViewHolder {
        // Declaración de variables y objetos
        public TextView title, eRaza, eEdad, eSexo, eEsteril, eFecha, ePeso;
        public TextView content;
        public ImageView imagen;
        public ImageButton imagen2;
        public Button bTarjetaMascota;

        public DocumentViewHolder(@NonNull View itemView) {
            super(itemView);
            // Inicializar las vistas
            title = itemView.findViewById(R.id.Nombre_Masc);
            imagen2 = itemView.findViewById(R.id.imagen_Masc);
            bTarjetaMascota = itemView.findViewById(R.id.button_mascota);
        }
    }
}
