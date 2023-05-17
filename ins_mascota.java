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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.time.LocalDate;
import java.time.Period;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ins_mascota extends AppCompatActivity {

    private int PICK_IMAGE_REQUEST =1;
    private Uri mImageUri ;
    private ImageButton bImagen;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference mStorageRef = storage.getReference();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    Map<String,Object> data = new HashMap<>();
    Map<String, Object> perroData = new HashMap<>();
    private EditText nombreEdt,edadEdt,pesoEdt,razaEdt,dateEdt;
    private ImageButton bBack;

    private Button bGuardar;

    private Spinner sexoEdt,esterilEdt;
    LocalDate fechaNacimientoEdad;
    String nombre,edad,raza,date,sexo,peso,esteril,url,imageUrl,selectedItem,selectedItem2;
    // Sobrescribe el método onCreate
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Llama al método onCreate de la clase padre
        super.onCreate(savedInstanceState);
        // Establece el diseño de la actividad
        setContentView(R.layout.ins_mascota);

        // Inicializa el EditText para el género del perro
        sexoEdt = findViewById(R.id.input_sexoPerro);
        // Inicializa el EditText para el estado de esterilización del perro
        esterilEdt = findViewById(R.id.input_esterilPerro);

        // Configura el adaptador para el spinner del género del perro
        sexoEdt = sexoEdt.findViewById(R.id.input_sexoPerro);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_sexo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sexoEdt.setAdapter(adapter);

        // Configura el adaptador para el spinner del estado de esterilización del perro
        esterilEdt = esterilEdt.findViewById(R.id.input_esterilPerro);
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.options_esteril, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        esterilEdt.setAdapter(adapter2);

        // Inicializa los EditTexts para el nombre, peso y raza del perro
        nombreEdt = findViewById(R.id.input_nombrePerro);
        pesoEdt = findViewById(R.id.input_PesoPerro);
        razaEdt = findViewById(R.id.input_razaPerro);

        // Inicializa el EditText para la fecha de nacimiento del perro
        dateEdt = findViewById(R.id.input_fecha);

        // Establece un OnClickListener en el EditText de la fecha de nacimiento
        dateEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtiene una instancia del calendario
                final Calendar c = Calendar.getInstance();

                // Obtiene el día, mes y año actual
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Crea una variable para el diálogo de selección de fecha
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // Pasa el contexto
                        ins_mascota.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Establece la fecha seleccionada en el EditText
                                dateEdt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaNacimientoEdad = LocalDate.of(year, monthOfYear, dayOfMonth);
                            }
                        },
                        // Pasa el año, mes y día para la fecha seleccionada en el selector de fecha
                        year, month, day);
                // Muestra el diálogo de selección de fecha
                datePickerDialog.show();
            }
        });


        // Establece un OnItemSelectedListener en el spinner del género del perro
        sexoEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtiene el elemento seleccionado y lo asigna a la variable selectedItem
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Muestra un mensaje si no se selecciona nada
                Toast.makeText(ins_mascota.this, "Seleccione sexo",
                        Toast.LENGTH_SHORT).show();
            }
        });
        // Establece un OnItemSelectedListener en el spinner del estado de esterilización del perro
        esterilEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtiene el elemento seleccionado y lo asigna a la variable selectedItem2
                selectedItem2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Muestra un mensaje si no se selecciona nada
                Toast.makeText(ins_mascota.this, "Seleccione un una opcion de esteril",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Inicializa el botón para guardar los datos del perro
        bGuardar = (Button) findViewById(R.id.button_GuardarPerro);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Calcula la edad del perro a partir de su fecha de nacimiento

                // Obtiene los datos ingresados por el usuario
                nombre = nombreEdt.getText().toString().trim();
                peso = pesoEdt.getText().toString().trim();
                raza = razaEdt.getText().toString().trim();
                date = dateEdt.getText().toString().trim();
                sexo = selectedItem;
                esteril = selectedItem2;
                edad =      calcularEdad(fechaNacimientoEdad);
                // Sube los datos a Firebase
                upFirebase(nombre, nombre + "_cod", peso, esteril, raza, date, sexo, imageUrl, edad);
                // Guarda el enlace de la imagen
                guardarlink(imageUrl, nombre);
                // Vuelve a la actividad anterior
                onBackPressed();
            }
        });
            // Inicializa el botón para subir una foto de perfil del perro
            ImageButton bFotoperfil = findViewById(R.id.bFotoMas);
            bFotoperfil.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Llama al método para subir un archivo
                    subir_archivo();
                }
            });
            // Inicializa el botón para volver a la actividad anterior
            bBack = findViewById(R.id.back);
            bBack.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Vuelve a la actividad anterior
                    onBackPressed();
                }
            });
        }
    // Método para actualizar Firebase con los datos del perro
    private void upFirebase(String n,String id,String p,String e,String r,String d,String s,String url,String edad){
        // Crear un nuevo objeto Perros con los datos proporcionados
        Perros perro = new Perros(n,n+"_cod",edad,e,d,p,r,s,url);
        // Agregar el objeto perro a la colección de perros del usuario en Firebase
        db.collection("/usuarios/"+user.getEmail()+"/Perros").document(n)
                .set(perro)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Si se escribe correctamente en Firebase, mostrar un mensaje de éxito
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(ins_mascota.this, "Mascota guardada",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Si hay un error al escribir en Firebase, mostrar un mensaje de error
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    // Método para subir una imagen a Firebase
    public void subir_archivo(){
        // Crear una referencia a la imagen que se va a subir
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Selecciona una imagen"), PICK_IMAGE_REQUEST);
    }

    // Método para manejar el resultado de la selección de imagen
    @Override
    protected void onActivityResult(int requestCode , int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Verificar que se haya seleccionado una imagen correctamente
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            // Subir la imagen al Firebase Storage
            StorageReference fileReference = mStorageRef.child("imagenes/" + (Math.random()*1+9999999) + ".jpg");
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // La imagen se ha subido correctamente
                    Toast.makeText(ins_mascota.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();

                    // Obtener la URL de la imagen subida
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            imageUrl = uri.toString();

                            // Clase para descargar la imagen en segundo plano
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

                                    bImagen = findViewById(R.id.bFotoMas);
                                    bImagen.setImageBitmap(result);
                                    // Actualiza la UI con la imagen descargada
                                }
                            }
                            new DownloadImageTask().execute(imageUrl);
                        }
                    });
                }
            });
        }
    }

    public  String  calcularEdad(LocalDate naci){



        // Fecha actual
        LocalDate fechaActual = LocalDate.now();

        // Calcular la diferencia entre las dos fechas
        Period periodo = Period.between(naci, fechaActual);

        // Obtener la edad en años
        String re = ""+periodo.getYears();


        return re;
    }

    public void guardarlink(String imageUrl, String n) {
        data.put("urlfotoperfil", imageUrl);
        // Se inserta el Map en Firestore
        db.collection("/usuarios/" + user.getEmail() + "/Perros").document(n).update(data);
    }

}
