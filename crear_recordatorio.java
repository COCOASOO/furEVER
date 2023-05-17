package com.bemen.furever;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
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
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class crear_recordatorio extends AppCompatActivity {
    // Contexto de la actividad
    Context context = this;

    // Variable para PendingIntent
    private PendingIntent pendingIntent;

    // Identificador del canal para las notificaciones
    private final static String CHANNEL_ID = "notificacion";

    // Identificador de la notificación
    private final static int NOTIFICACION_ID = 0;

    // Instancia de FirebaseFirestore
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Obtener el usuario actual de FirebaseAuth
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    // Instancia de FirebaseStorage
    FirebaseStorage storage = FirebaseStorage.getInstance();

    // Referencia a la raíz del almacenamiento de Firebase
    StorageReference storageRef = storage.getReference();

    // Referencia a la imagen específica en el almacenamiento de Firebase
    StorageReference imagesRef = storageRef.child("imagenes/puñetaSI.jpg");

    // Declaración de ImageButton
    private ImageButton bBack;

    // Declaración de EditText
    private EditText asuntoEdt, fechaEdt, horaEdt, nombreEdt;

    // Declaración de Spinner
    private Spinner tipoEdt, imporEdt;

    // Declaración de variables de tipo String
    String asunto, fecha, tipo, importancia, selectedItem, selectedItem2, nombre;

    // Declaración de variable de tipo String
    String hora;

    // Declaración de Button
    private Button bGuardar;

    // Declaración de variables de tipo Date
    Date fechaF, horaF;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.crear_recordatorio);

        // Obtener referencia al Spinner tipoEdt del layout
        tipoEdt = findViewById(R.id.input_tipo);

        // Crear un ArrayAdapter para el Spinner tipoEdt
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_tipo, android.R.layout.simple_spinner_item);

        // Especificar el layout para desplegar las opciones del Spinner
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignar el ArrayAdapter al Spinner tipoEdt
        tipoEdt.setAdapter(adapter);

        // Obtener referencia al Spinner imporEdt del layout
        imporEdt = findViewById(R.id.input_importancia);

        // Crear un ArrayAdapter para el Spinner imporEdt
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.options_importancia, android.R.layout.simple_spinner_item);

        // Especificar el layout para desplegar las opciones del Spinner
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // Asignar el ArrayAdapter al Spinner imporEdt
        imporEdt.setAdapter(adapter2);

        // Obtener referencia al EditText asuntoEdt del layout
        asuntoEdt = findViewById(R.id.input_asunto);

        // Obtener referencia al EditText nombreEdt del layout
        nombreEdt = findViewById(R.id.input_nombreMa);

        // Obtener referencia al EditText horaEdt del layout
        horaEdt = findViewById(R.id.input_hora);

        // Establecer un OnClickListener al EditText horaEdt
        horaEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                // Crear un TimePickerDialog para seleccionar la hora
                TimePickerDialog timePickerDialog = new TimePickerDialog(crear_recordatorio.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Aquí puedes hacer lo que desees con la hora seleccionada por el usuario
                        String formattedHour = String.format("%02d", hourOfDay);
                        String formattedMinute = String.format("%02d", minute);
                        horaEdt.setText(formattedHour + ":" + formattedMinute);
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        fechaEdt = findViewById(R.id.input_fecha);

// Establecer un OnClickListener al EditText fechaEdt
        fechaEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener la instancia del calendario
                final Calendar c = Calendar.getInstance();

                // Obtener el año, mes y día actuales del calendario
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Crear un DatePickerDialog para seleccionar la fecha
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        crear_recordatorio.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Establecer la fecha seleccionada en el EditText fechaEdt
                                fechaEdt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        },
                        // Pasar el año, mes y día actuales al DatePickerDialog
                        year, month, day);

                // Mostrar el DatePickerDialog
                datePickerDialog.show();
            }
        });

        tipoEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el elemento seleccionado del Spinner tipoEdt
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Mostrar un mensaje de aviso cuando no se selecciona nada en el Spinner
                Toast.makeText(crear_recordatorio.this, "Seleccione un tipo",
                        Toast.LENGTH_SHORT).show();
            }
        });

        imporEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Obtener el elemento seleccionado del Spinner imporEdt
                selectedItem2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Mostrar un mensaje de aviso cuando no se selecciona nada en el Spinner
                Toast.makeText(crear_recordatorio.this, "Seleccione un tipo",
                        Toast.LENGTH_SHORT).show();
            }
        });

        bGuardar = (Button) findViewById(R.id.button_crear);
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Obtener los valores de los campos de texto y asignarlos a las variables correspondientes
                asunto = asuntoEdt.getText().toString().trim();
                fecha = fechaEdt.getText().toString().trim();
                hora = horaEdt.getText().toString().trim();
                nombre = nombreEdt.getText().toString().trim();

                // Formatear las fechas y horas utilizando el formato deseado
                SimpleDateFormat fechaFormato = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat horaFormato = new SimpleDateFormat("HH:mm", Locale.getDefault());
                try {
                    fechaF = fechaFormato.parse(fecha);
                    horaF = horaFormato.parse(hora);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

                // Llamar a los métodos correspondientes
                notichannel();
                tipo = selectedItem;
                importancia = selectedItem2;
                noti(tipo, asunto, horaF, fechaF, importancia, nombre);
                upFirebase(asunto, fecha, hora, tipo, importancia, nombre);
                upFirebaseMascota(asunto, fecha, hora, tipo, importancia, nombre);
            }
        });

        bBack = (ImageButton) findViewById(R.id.backCrearRecordatorio);
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        }

    // Método para subir datos a Firebase
    private void upFirebase(String asunto, String fecha, String hora, String tipo, String importancia,String nombre) {
        // Crear un nuevo objeto Recordatorios con los datos proporcionados
        Recordatorios recordatorio = new Recordatorios(1, asunto, fecha, hora, tipo, importancia,nombre);
        // Agregar el objeto a la colección de Recordatorios del usuario en Firebase
        db.collection("/usuarios/" + user.getEmail() + "/Recordatorios").document(fecha+"-"+hora)
                .set(recordatorio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // En caso de éxito, mostrar un mensaje en el registro y en un Toast
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(crear_recordatorio.this, "Recordatorio guardado",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // En caso de fallo, mostrar un mensaje de error en el registro
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    // Método para subir datos de una mascota a Firebase
    private void upFirebaseMascota(String asunto, String fecha, String hora, String tipo, String importancia,String nombre) {
        // Crear un nuevo objeto Recordatorios con los datos proporcionados
        Recordatorios recordatorio = new Recordatorios(1, asunto, fecha, hora, tipo, importancia,nombre);
        // Agregar el objeto a la colección de Recordatorios de la mascota del usuario en Firebase
        db.collection("/usuarios/" + user.getEmail() + "/Perros/"+nombre+"/Recordatorios").document(fecha+"-"+hora)
                .set(recordatorio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // En caso de éxito, mostrar un mensaje en el registro y en un Toast
                        Log.d(TAG, "DocumentSnapshot successfully written!");
                        Toast.makeText(crear_recordatorio.this, "Recordatorio guardado",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // En caso de fallo, mostrar un mensaje de error en el registro
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }


    private void notichannel() {
        // Verificar si la versión del sistema operativo es igual o superior a Android Oreo (8.0)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear el nombre del canal de notificación
            CharSequence name = "Notificacion";
            // Crear el canal de notificación con el ID, nombre y nivel de importancia
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            // Obtener el servicio de gestión de notificaciones
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Crear el canal de notificación en el gestor de notificaciones
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @SuppressLint("ResourceAsColor")
    private void noti(String titulo, String asunto, Date hora,Date fecha,String importancia, String nombre) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        @SuppressLint("UnsafeDynamicallyLoadedCode")
        // Crea un Intent para el PendingIntent
        Intent intent = new Intent(this, MyNotificationReceiver.class);
        intent.putExtra("titulo",nombre +" necesita :"+titulo);
        intent.putExtra("asunto", asunto+ " | nivel de importancia: "+ importancia);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, hora.getHours());
        calendar.set(Calendar.MINUTE, hora.getMinutes());
        // Calcula el tiempo en el que se activará la alarma (en milisegundos)
        // Establece la alarma con el tiempo deseado
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


    }


}