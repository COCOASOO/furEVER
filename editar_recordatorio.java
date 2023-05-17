package com.bemen.furever;
import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class editar_recordatorio extends AppCompatActivity {
    private ImageButton bBackEditarRecordatorio;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    private EditText asuntoEdt,fechaEdt,horaEdt,nombreEdt;
    String asunto,fecha,hora, tipo, nombre,importancia,selectedItem,selectedItem2;
    private Spinner tipoEdt, imporEdt;

    private Button bGuardar;
    Date fechaF, horaF;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Establecer el layout de la actividad
        setContentView(R.layout.editar_recordatorio);
        // Inicializar las vistas
        nombreEdt = findViewById(R.id.input_nombreMa);
        asuntoEdt = findViewById(R.id.input_asunto);
        fechaEdt = findViewById(R.id.input_fecha);
        horaEdt = findViewById(R.id.input_hora);

        // Establecer un listener para el EditText de la hora
        horaEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al hacer clic en el EditText, mostrar un TimePickerDialog para seleccionar la hora
                final Calendar calendar = Calendar.getInstance();
                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minute = calendar.get(Calendar.MINUTE);

                TimePickerDialog timePickerDialog = new TimePickerDialog(editar_recordatorio.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Al seleccionar una hora, formatearla y mostrarla en el EditText
                        String formattedHour = String.format("%02d", hourOfDay);
                        String formattedMinute = String.format("%02d", minute);
                        horaEdt.setText(formattedHour + ":" + formattedMinute);
                    }
                }, hour, minute, true);

                timePickerDialog.show();
            }
        });

        tipoEdt = findViewById(R.id.input_tipo);
        // Crear un ArrayAdapter para el Spinner del tipo de recordatorio
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.options_tipo, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoEdt.setAdapter(adapter);

        imporEdt = findViewById(R.id.input_importancia);
        // Crear un ArrayAdapter para el Spinner de la importancia del recordatorio
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.options_importancia, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        imporEdt.setAdapter(adapter2);

        // Establecer un OnClickListener en el objeto fechaEdt
        fechaEdt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al hacer clic en el objeto fechaEdt, se ejecutará el siguiente código

                // Obtener una instancia del calendario
                final Calendar c = Calendar.getInstance();

                // Obtener el año, mes y día del calendario
                int year = c.get(Calendar.YEAR);
                int month = c.get(Calendar.MONTH);
                int day = c.get(Calendar.DAY_OF_MONTH);

                // Crear un objeto DatePickerDialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        // Pasar el contexto
                        editar_recordatorio.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                // Al seleccionar una fecha en el DatePickerDialog, se ejecutará el siguiente código

                                // Establecer la fecha seleccionada en el objeto fechaEdt
                                fechaEdt.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                            }
                        },
                        // Pasar el año, mes y día para la fecha seleccionada en el DatePickerDialog
                        year, month, day);

                // Mostrar el DatePickerDialog
                datePickerDialog.show();
            }
        });

        // Establecer un OnItemSelectedListener en el objeto tipoEdt
        tipoEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Al seleccionar un elemento en el objeto tipoEdt, se ejecutará el siguiente código

                // Obtener el elemento seleccionado y asignarlo a la variable selectedItem
                selectedItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Si no se selecciona ningún elemento en el objeto tipoEdt, se ejecutará el siguiente código

                // Mostrar un mensaje Toast indicando que se debe seleccionar un tipo
                Toast.makeText(editar_recordatorio.this, "Seleccione un tipo",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Establecer un OnItemSelectedListener en el objeto imporEdt
        imporEdt.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Al seleccionar un elemento en el objeto imporEdt, se ejecutará el siguiente código

                // Obtener el elemento seleccionado y asignarlo a la variable selectedItem2
                selectedItem2 = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Si no se selecciona ningún elemento en el objeto imporEdt, se ejecutará el siguiente código

                // Mostrar un mensaje Toast indicando que se debe seleccionar un tipo
                Toast.makeText(editar_recordatorio.this, "Seleccione un tipo",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Obtener el objeto Button bGuardar por su ID
        bGuardar = (Button) findViewById(R.id.button_crear);

// Establecer un OnClickListener en el objeto bGuardar
        bGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al hacer clic en el objeto bGuardar, se ejecutará el siguiente código

                // Obtener el texto de los objetos asuntoEdt, fechaEdt, horaEdt y nombreEdt y asignarlos a las variables asunto, fecha, hora y nombre respectivamente
                asunto = asuntoEdt.getText().toString().trim();
                fecha = fechaEdt.getText().toString().trim();
                hora = horaEdt.getText().toString().trim();
                nombre = nombreEdt.getText().toString().trim();

                // Crear objetos SimpleDateFormat para dar formato a la fecha y hora
                SimpleDateFormat fechaFormato = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                SimpleDateFormat horaFormato = new SimpleDateFormat("HH:mm", Locale.getDefault());

                try {
                    // Intentar convertir las cadenas de texto fecha y hora a objetos Date
                    fechaF = fechaFormato.parse(fecha);
                    horaF = horaFormato.parse(hora);
                } catch (ParseException e) {
                    // Si ocurre un error al convertir las cadenas de texto a objetos Date, lanzar una excepción
                    throw new RuntimeException(e);
                }

                // Asignar los valores de las variables selectedItem y selectedItem2 a las variables tipo e importancia respectivamente
                tipo = selectedItem;
                importancia = selectedItem2;

                // Llamar a los métodos eliminar, noti y upFirebase con los valores obtenidos
                eliminar(fecha,hora);
                noti(tipo,asunto,horaF,fechaF,importancia,nombre);
                upFirebase(asunto, fecha, hora, tipo, importancia,nombre);
            }
        });

        // Obtener el objeto Button bBackEditarRecordatorio por su ID
        bBackEditarRecordatorio = findViewById(R.id.backEditarRecordatorio);
        // Establecer un OnClickListener en el objeto bBackEditarRecordatorio
        bBackEditarRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Al hacer clic en el objeto bBackEditarRecordatorio, se ejecutará el siguiente código

                // Llamar al método onBackPressed para volver a la actividad anterior
                onBackPressed();
            }
        });
    }

    // Método eliminar que recibe dos parámetros: f y h
    public void eliminar(String f,String h){
        // Eliminar el documento con el ID f + "-" + h de la colección "/usuarios/" + user.getEmail() + "/Recordatorios"
        db.collection("/usuarios/" + user.getEmail() + "/Recordatorios").document(f+"-"+h).delete();
    }

    // Método upFirebase que recibe seis parámetros: asunto, fecha, hora, tipo, importancia y nombre
    private void upFirebase(String asunto, String fecha, String hora, String tipo, String importancia,String nombre){
        // Crear un objeto Recordatorios con los valores recibidos como parámetros
        Recordatorios recordatorio = new Recordatorios(1, asunto, fecha, hora, tipo, importancia,nombre);

        // Guardar el objeto recordatorio en la colección "/usuarios/" + user.getEmail() + "/Recordatorios" con el ID fecha + "-" + hora
        db.collection("/usuarios/" + user.getEmail() + "/Recordatorios").document(fecha+"-"+hora)
                .set(recordatorio)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Si se guarda el objeto recordatorio correctamente, se ejecutará el siguiente código

                        // Mostrar un mensaje en el log indicando que se guardó correctamente
                        Log.d(TAG, "DocumentSnapshot successfully written!");

                        // Mostrar un mensaje Toast indicando que se guardó correctamente
                        Toast.makeText(editar_recordatorio.this, "Mascota guardada",
                                Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Si ocurre un error al guardar el objeto recordatorio, se ejecutará el siguiente código

                        // Mostrar un mensaje en el log indicando que ocurrió un error
                        Log.w(TAG, "Error writing document", e);
                    }
                });
    }

    private void noti(String titulo, String asunto, Date hora, Date fecha, String importancia, String nombre) {
        // Obtener el servicio del sistema AlarmManager
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // Crea un Intent para el PendingIntent
        Intent intent = new Intent(this, MyNotificationReceiver.class);
        intent.putExtra("titulo", nombre + " necesita: " + titulo);
        intent.putExtra("asunto", asunto + " | Nivel de importancia: " + importancia);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        // Configurar el calendario con la fecha y hora deseadas
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, hora.getHours());
        calendar.set(Calendar.MINUTE, hora.getMinutes());

        // Establecer la alarma en el tiempo especificado
        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}