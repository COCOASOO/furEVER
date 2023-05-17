package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class editar_mascota extends AppCompatActivity {
    private ImageButton bBackEditarMascota, bImagen; // Declaración de variables ImageButton

    private int PICK_IMAGE_REQUEST = 1; // Variable para almacenar la solicitud de selección de imagen
    private Uri mImageUri; // URI para almacenar la ubicación de la imagen seleccionada
    FirebaseStorage storage = FirebaseStorage.getInstance(); // Instancia de FirebaseStorage
    StorageReference mStorageRef = storage.getReference(); // Referencia al almacenamiento de Firebase
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Instancia de FirebaseFirestore
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Obtención del usuario actual de Firebase
    Map<String, Object> perroData = new HashMap<>(); // Mapa para almacenar datos de perro

    private EditText nombreAntEdt, nombreEdt, edadEdt, pesoEdt, razaEdt, dateEdt; // Declaración de variables EditText

    private Button bGuardar; // Botón de guardar
    private Spinner sexoEdt, esterEdt; // Selectores de sexo y esterilización

    String nombre1, nombre, edad, raza, date, sexo, peso, esteril, imageUrl, selectedItem, selectedItem2; // Variables para almacenar información de la mascota


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.editar_mascota);

        // Obtención de referencias a los elementos de la interfaz de usuario
        nombreAntEdt = findViewById(R.id.input_nombrePerroAntiguo);
        nombreEdt = findViewById(R.id.input_nombrePerro);
        pesoEdt = findViewById(R.id.input_PesoPerro);
        razaEdt = findViewById(R.id.input_razaPerro);
        esterEdt = findViewById(R.id.input_esterilPerro);
        sexoEdt = findViewById(R.id.input_sexoPerro);

        // Configuración del adaptador y del selector de sexo
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_sexo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoEdt.setAdapter(adapter);

        // Configuración del adaptador y del selector de esterilización
        esterEdt = findViewById(R.id.input_esterilPerro);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.options_esteril, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        esterEdt.setAdapter(adapter2);

        // Configuración del selector de fecha
        dateEdt = findViewById(R.id.input_fecha);
        dateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtención de la instancia del calendario
                final Calendar c = Calendar.getInstance();

                // Obtención del día, mes y año actuales
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Creación del cuadro de diálogo de selección de fecha
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        editar_mascota.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Establecimiento de la fecha seleccionada en el campo de texto
                                dateEdt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        },
                        year, month, day);

                // Mostrar el cuadro de diálogo de selección de fecha
                datePickerDialog.show();
            }
        });

        sexoEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtención del elemento seleccionado en el selector de sexo
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Mostrar un mensaje de advertencia si no se selecciona ningún elemento en el selector de sexo
                Toast.makeText(editar_mascota.this, "Seleccione sexo",
                        Toast.LENGTH_SHORT).show();
            }
        });

        esterEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtención del elemento seleccionado en el selector de esterilización
                selectedItem2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Mostrar un mensaje de advertencia si no se selecciona ningún elemento en el selector de esterilización
                Toast.makeText(editar_mascota.this, "Seleccione una opción de esterilización",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bGuardar = (Button) findViewById(R.id.button_editarPerro);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtención de los valores ingresados por el usuario
                nombre1 = nombreAntEdt.getText().toString().trim();
                nombre = nombreEdt.getText().toString().trim();
                peso = pesoEdt.getText().toString().trim();
                raza = razaEdt.getText().toString().trim();
                date = dateEdt.getText().toString().trim();
                sexo = selectedItem;
                esteril = selectedItem2;

                // Llamada a métodos para eliminar, actualizar y guardar la imagen
                eliminar(nombre1);
                upFirebase(nombre, nombre + "_cod", peso, esteril, raza, date, sexo, imageUrl);
                guardarlink(imageUrl, nombre);
            }
        });

        bBackEditarMascota = findViewById(R.id.backEditarMascota);
        bBackEditarMascota.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Volver a la actividad anterior cuando se hace clic en el botón de retroceso
                onBackPressed();
            }
        });

        ImageButton bFotoperfil = findViewById(R.id.bFotoMasEditar);
        bFotoperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Subir un archivo cuando se hace clic en el botón de foto de perfil
                subir_archivo();
            }
        });
    }
    public void subir_archivo(){
        // Crear una referencia a la imagen que se va a subir
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode , int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Comprobar si se seleccionó una imagen correctamente
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            // Subir la imagen al Firebase Storage
            StorageReference fileReference = mStorageRef.child("imagenes/" + + (Math.random()*1+9999999) +  ".jpg");
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // La imagen se ha subido correctamente
                    Toast.makeText(editar_mascota.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();

                    // Obtener la URL de la imagen subida
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();

                            // Clase interna para descargar la imagen y mostrarla en la interfaz de usuario
                            class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {

                                @Override
                                protected Bitmap doInBackground(String... imageUrl) {
                                    Bitmap bm = null;
                                    try {
                                        URL url = new URL(imageUrl[0]);
                                        URLConnection con = url.openConnection();
                                        con.connect();
                                        InputStream is = con.getInputStream();
                                        BufferedInputStream bis = new BufferedInputStream(is);
                                        bm = BitmapFactory.decodeStream(bis);
                                        bis.close();
                                        is.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    return bm;
                                }

                                @Override
                                protected void onPostExecute(Bitmap result) {
                                    // Actualizar la interfaz de usuario con la imagen descargada
                                    bImagen = findViewById(R.id.bFotoMasEditar);
                                    bImagen.setImageBitmap(result);
                                }
                            }

                            // Crear una instancia de la clase DownloadImageTask y ejecutarla para descargar la imagen
                            new DownloadImageTask().execute(imageUrl);
                        }
                    });
                }
            });
        }
    }

    public void guardarlink(String imageUrl, String n) {
        // Se agrega la URL de la foto de perfil al mapa de datos del perro
        perroData.put("urlfotoperfil", imageUrl);

        // Se inserta el mapa de datos en Firestore
        db.collection("/usuarios/" + user.getEmail() + "/Perros").document(n).update(perroData);
    }

    private void eliminar(String n) {
        // Se elimina el documento del perro en Firestore
        db.collection("/usuarios/" + user.getEmail() + "/Perros").document(n).delete();
    }

    private void upFirebase(String n, String id, String p, String e, String r, String d, String s, String url) {
        // Se crea un objeto Perros con los datos del perro
        Perros perro = new Perros(n, n + "_cod", "12", e, d, p, r, s, url);

        // Se guarda el perro en Firestore
        db.collection("/usuarios/" + user.getEmail() + "/Perros").document(n)
                .set(perro)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(editar_mascota.this, "Mascota guardada",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

}