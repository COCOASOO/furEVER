package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class dietFragment extends Fragment {
    View rootView;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    // Obtiene la instancia del usuario actual de Firebase

    public EditText tamEdt;
    public static EditText edadEdt;
    public static EditText pesoEdt;
    private TextView cantText, actText;
    private Button bBuscar;
    private String tamaño, Resultado, Resultado2, selectedItem2;
    private Spinner spinner;
    private int edad, peso;

    private double cantidadDeComida;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Obtiene la instancia de FirebaseFirestore

    public dietFragment() {
        // Constructor vacío requerido
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.diet_fragment, container, false);
        bBuscar = rootView.findViewById(R.id.bBuscar);
        // Busca y asigna el botón "Buscar" en la interfaz de usuario

        spinner = rootView.findViewById(R.id.input_tamaño);
        // Busca y asigna el spinner de tamaño en la interfaz de usuario
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
                R.array.options_tamaño, android.R.layout.simple_spinner_item);
        // Crea un ArrayAdapter para el spinner con los valores definidos en el archivo de recursos
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Configura el diseño de la lista desplegable
        spinner.setAdapter(adapter);
        // Asigna el adaptador al spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem2 = parent.getItemAtPosition(position).toString();
                // Obtiene el elemento seleccionado del spinner y lo asigna a la variable selectedItem2
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(getActivity(), "Seleccione un tipo",
                        Toast.LENGTH_SHORT).show();
                // Muestra un mensaje si no se selecciona ningún elemento del spinner
            }
        });

        lectura();
        // Llama al método lectura para inicializar las vistas

        bBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tamaño = selectedItem2;
                edad = Integer.parseInt(edadEdt.getText().toString().trim());
                peso = Integer.parseInt(pesoEdt.getText().toString().trim());
                // Obtiene los valores de las vistas y los asigna a las variables correspondientes

                calculadora(tamaño, edad, peso);
                // Llama al método calculadora para realizar el cálculo de la dieta
                calcularActividad(edad, peso, tamaño);
                // Llama al método calcularActividad para calcular la actividad
                mostar();
                // Llama al método mostrar para mostrar los resultados en las vistas correspondientes
            }
        });

        return rootView;
        // Retorna la vista inflada
    }

    public void lectura() {
        Spinner tamEdt = rootView.findViewById(R.id.input_tamaño);
        edadEdt = rootView.findViewById(R.id.input_edad);
        pesoEdt = rootView.findViewById(R.id.input_peso);
        cantText = rootView.findViewById(R.id.var_result);
        actText = rootView.findViewById(R.id.act_resul);
        cantidadDeComida = 0.0;
        // Inicializa las vistas correspondientes
    }

    public void calculadora(String tamañoC, int edadC, int pesoC) {
        if (tamañoC.equalsIgnoreCase("pequeño")) {
            cantidadDeComida = ((pesoC * 30) + (edadC * 5) + 20) / 10;
            Resultado = cantidadDeComida + " gr";
            // Realiza el cálculo de la cantidad de comida para un perro pequeño
        } else if (tamañoC.equalsIgnoreCase("mediano")) {
            cantidadDeComida = ((pesoC * 50) + (edadC * 10) + 30) / 10;
            Resultado = cantidadDeComida + "gr";
            // Realiza el cálculo de la cantidad de comida para un perro mediano
        } else if (tamañoC.equalsIgnoreCase("grande")) {
            cantidadDeComida = ((pesoC * 70) + (edadC * 15) + 40) / 10;
            Resultado = cantidadDeComida + "gr";
            // Realiza el cálculo de la cantidad de comida para un perro grande
        } else {
            System.out.println("Tamaño inválido");
            System.exit(0);
            // Muestra un mensaje de tamaño inválido y sale del programa si el tamaño no es válido
        }
    }

    public void calcularActividad(int edad, int peso, String tamaño) {
        double nivelActividad = 60;

        // Ajustar el nivel de actividad en función de la edad, el peso y el tamaño del perro
        if (edad < 1) {
            nivelActividad *= 1.5; // Cachorro
        } else if (edad > 7) {
            nivelActividad *= 0.75; // Perro mayor
        }

        if (peso > 20.0) {
            nivelActividad *= 1.25; // Perro grande
        } else if (peso < 5.0) {
            nivelActividad *= 0.75; // Perro pequeño
        }

        if (tamaño.equalsIgnoreCase("pequeño")) {
            nivelActividad *= 0.75; // Perro pequeño
        } else if (tamaño.equalsIgnoreCase("mediano")) {
            nivelActividad *= 1.0; // Perro mediano
        } else if (tamaño.equalsIgnoreCase("grande")) {
            nivelActividad *= 1.25; // Perro grande
        }

        // Redondear el resultado a 2 decimales
        double re = Math.round(nivelActividad);
        Resultado2 = re + " min/dia";
        // Realiza el cálculo del nivel de actividad y lo asigna a la variable Resultado2
    }

    public void mostar() {
        actText.setText(Resultado2);
        cantText.setText(Resultado);
        // Muestra los resultados en las vistas correspondientes
    }


}