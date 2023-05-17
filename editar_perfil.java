package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.safetynet.SafetyNet;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class editar_perfil extends AppCompatActivity {
    private ImageButton bBack, bImagen; // Botones de retroceso e imagen
    private int PICK_IMAGE_REQUEST = 1; // Constante para la solicitud de selección de imagen
    private Uri mImageUri; // Uri para almacenar la imagen seleccionada
    private EditText nombreEdt, emailEdt, passEdt, passEdt2; // Campos de edición para nombre, email y contraseña

    String nombre, email, password, password2; // Variables para almacenar los valores de nombre, email y contraseña
    FirebaseStorage storage = FirebaseStorage.getInstance(); // Instancia de Firebase Storage
    StorageReference mStorageRef = storage.getReference(); // Referencia de almacenamiento de Firebase Storage
    FirebaseFirestore db = FirebaseFirestore.getInstance(); // Instancia de FirebaseFirestore
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser(); // Usuario actual de Firebase Authentication
    Map<String, Object> data = new HashMap<>(); // Mapa para almacenar los datos a editar
    private Button editar; // Botón de edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.editar_perfil);

        // Obtener referencias a los elementos de la interfaz de usuario
        nombreEdt = findViewById(R.id.input_nombreEd);
        emailEdt = findViewById(R.id.input_emailEd);
        passEdt = findViewById(R.id.input_passwordEd);
        passEdt2 = findViewById(R.id.input_passwordEd2);
        editar = findViewById(R.id.button_editar);

        editar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de texto
                nombre = nombreEdt.getText().toString().trim();
                password = passEdt.getText().toString().trim();
                password2 = passEdt2.getText().toString().trim();

                if (password.equals(password2)) {
                    // Los campos de contraseña coinciden
                    editarNombreFirebase(nombre); // Llamar al método para editar el nombre en Firebase
                    editarPass(password); // Llamar al método para editar la contraseña localmente
                    editarPassFirebase(password); // Llamar al método para editar la contraseña en Firebase
                } else {
                    // Las contraseñas no coinciden, mostrar un mensaje de error
                    Toast.makeText(editar_perfil.this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                }
            }
        });

        bBack = findViewById(R.id.backEditarPerfil);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        // Configurar el botón para subir la foto de perfil
        ImageButton bFotoperfil = findViewById(R.id.bFotoPerfil);
        bFotoperfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                subir_archivo();
            }
        });
    }
    // Método para editar el nombre en Firebase
    private void editarNombreFirebase(String n) {
        // Obtener la referencia al documento del usuario en Firestore
        DocumentReference docRef = db.collection("usuarios").document(user.getEmail());
        docRef.update("nombre", n)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
                    }
                });
    }
    // Método para editar la contraseña localmente
    private void editarPass(String p) {
        // Actualizar la contraseña del usuario actual en Firebase Authentication
        user.updatePassword(p)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "User password updated.");
                        }
                    }
                });
    }
    // Método para editar la contraseña en Firebase
    private void editarPassFirebase(String p) {
        // Obtener la referencia al documento del usuario en Firestore
        DocumentReference docRef = db.collection("usuarios").document(user.getEmail());
        docRef.update("contraseña", p)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully updated!");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error updating document", e);
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

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();

            // Subir la imagen al Firebase Storage
            StorageReference fileReference = mStorageRef.child("imagenes/" + FirebaseAuth.getInstance().getCurrentUser().getUid() + ".jpg");
            fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // La imagen se ha subido correctamente
                    Toast.makeText(editar_perfil.this, "Imagen subida correctamente", Toast.LENGTH_SHORT).show();

                    // Obtener la URL de la imagen subida
                    fileReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();

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
                                    guardarlink(imageUrl);
                                    bImagen = findViewById(R.id.bFotoPerfil);
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
    public void guardarlink(String imageUrl){
        data.put("urlfotoperfil", imageUrl);
        // Se inserta el Map en Firestore
        db.collection("/usuarios/").document(user.getEmail()).update(data);
    }
}

