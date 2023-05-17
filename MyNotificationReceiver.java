package com.bemen.furever;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MyNotificationReceiver extends BroadcastReceiver {
    MyNotificationReceiver context = this; // Instancia de MyNotificationReceiver
    private final static String CHANNEL_ID = "notificacion"; // ID del canal de notificación

    @SuppressLint("ResourceAsColor")
    @Override
    public void onReceive(Context context, Intent intent) {
        // Obtiene los datos de la notificación del Intent
        String titulo = intent.getStringExtra("titulo"); // Título de la notificación
        String asunto = intent.getStringExtra("asunto"); // Asunto de la notificación

        // Crea la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID);
        builder.setSmallIcon(R.drawable.logo); // Icono pequeño de la notificación
        builder.setContentTitle("Tu mascota: " + titulo); // Título de la notificación
        builder.setContentText(asunto); // Contenido de la notificación
        builder.setColor(R.color.black); // Color de la notificación
        builder.setPriority(NotificationCompat.PRIORITY_MAX); // Prioridad máxima de la notificación
        builder.setLights(Color.GREEN, 1000, 1000); // Luces de la notificación (verde)
        builder.setVibrate(new long[]{1000, 1000, 1000, 1000, 1000}); // Vibración de la notificación
        builder.setDefaults(Notification.DEFAULT_SOUND); // Sonido de la notificación (por defecto)

        // Muestra la notificación
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling ActivityCompat#requestPermissions here to request the missing permissions
            //  and then overriding public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //  int[] grantResults) to handle the case where the user grants the permission.
            //  See the documentation for ActivityCompat#requestPermissions for more details.
            return;
        }

        notificationManager.notify(0, builder.build()); // Muestra la notificación
    }
}
