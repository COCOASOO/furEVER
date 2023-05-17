package com.bemen.furever;

import android.content.Intent;
import android.os.Bundle;

import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class main_bar extends AppCompatActivity {

    FirebaseAuth mAuth;
    ImageButton salir;
    DrawerLayout dLayout;
    ImageButton bBack;
    public Fragment defaultFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_bar); // Establece el diseño de la actividad como main_bar.xml
        mAuth = FirebaseAuth.getInstance(); // Inicializa la instancia de FirebaseAuth
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Obtiene el usuario actualmente autenticado
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar); // Obtiene la referencia de la Toolbar desde el layout
        setSupportActionBar(toolbar); // Configura la Toolbar como ActionBar
        // Implementa el evento setNavigationOnClickListener
        salir = findViewById(R.id.logout); // Obtiene la referencia del botón "salir" desde el layout
        salir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut(); // Cierra la sesión del usuario actual
                Intent intent = new Intent(main_bar.this, login.class); // Crea un Intent para ir a la actividad de login
                startActivity(intent); // Inicia la actividad de login
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dLayout.openDrawer(Gravity.LEFT); // Abre el drawer en el lado izquierdo
            }
        });
        setNavigationDrawer(); // Llama al método para configurar el Navigation Drawer
        defaultFragment = new tus_mascotas(); // Crea una instancia del fragmento por defecto (tus_mascotas)
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame, defaultFragment); // Reemplaza un Fragmento en el FrameLayout con el fragmento por defecto
        transaction.commit(); // Aplica los cambios
    }

    private void setNavigationDrawer() {
        dLayout = (DrawerLayout) findViewById(R.id.drawer_layout); // Inicializa el DrawerLayout obteniendo su referencia desde el layout
        NavigationView navView = (NavigationView) findViewById(R.id.navigation); // Inicializa el NavigationView obteniendo su referencia desde el layout
        // Implementa el evento setNavigationItemSelectedListener en el NavigationView
        navView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                Fragment frag = null; // Crea un objeto Fragment
                FragmentActivity fragmentActivity = null;
                int itemId = menuItem.getItemId(); // Obtiene el ID del elemento de menú seleccionado
                // Verifica el ID del elemento de menú seleccionado y reemplaza el Fragmento correspondientemente
                if (itemId == R.id.uno) {
                    frag = new ver_perfil();
                } else if (itemId == R.id.dos) {
                    frag = new tus_mascotas();
                } else if (itemId == R.id.tres) {
                    frag = new ThirdFragment();
                } else if (itemId == R.id.cuatro) {
                    frag = new foros();
                } else if (itemId == R.id.cinco) {
                    fragmentActivity = new mapa_interactivo();
                }
                // Muestra un mensaje Toast con el título del elemento de menú
                Toast.makeText(getApplicationContext(), menuItem.getTitle(), Toast.LENGTH_SHORT).show();
                if (frag != null) {
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.frame, frag); // Reemplaza un Fragmento en el FrameLayout
                    transaction.commit(); // Aplica los cambios
                    dLayout.closeDrawers(); // Cierra todos los Drawer Views abiertos
                    return true;
                }
                return false;
            }
        });
    }

    public void onStart() {
        super.onStart();

        // Verifica si el usuario ha iniciado sesión (no es nulo) y actualiza la interfaz de usuario en consecuencia.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            reload(); // Llama al método para recargar la interfaz de usuario
        }
    }

    private void reload() {
        // Implementa la lógica para recargar la interfaz de usuario
        // Este método debe ser completado con la lógica necesaria para actualizar la interfaz de usuario cuando el usuario ha iniciado sesión
    }
}
