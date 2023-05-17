package com.bemen.furever;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inicio);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Comprueba si la versión de Android es igual o superior a Oreo (API nivel 26)
            // Crea un canal de notificación para versiones de Android igual o superiores a Oreo
            CharSequence name = getString(R.string.default_channel_name);
            String description = getString(R.string.default_channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("default_channel_id", name, importance);
            channel.setDescription(description);

            // Registrar canal de notificación en el sistema
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }
    public void inicio_a_login(View view) {
        // Método para cambiar de la actividad de inicio a la actividad de login cuando se hace clic en un botón
        Intent intent = new Intent(this, login.class); // Crea un Intent para ir a la actividad de login
        startActivity(intent); // Inicia la actividad de login
    }
}