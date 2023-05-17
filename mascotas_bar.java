package com.bemen.furever;

import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.tabs.TabLayout;

public class mascotas_bar extends AppCompatActivity {
    TabLayout tabLayout;
    FrameLayout frameLayout;
    Fragment fragment = null;
    FragmentManager fragmentManager;
    FragmentTransaction fragmentTransaction;
    ImageButton bBack;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mascotas_bar); // Establece el diseño de la actividad como mascotas_bar.xml

        bBack = findViewById(R.id.backMascota); // Obtiene la referencia del botón "backMascota" desde el layout
        bBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed(); // Configura la acción al hacer clic en el botón para volver atrás
            }
        });

        tabLayout = (TabLayout) findViewById(R.id.tabLayout); // Obtiene la referencia de TabLayout desde el layout
        frameLayout = (FrameLayout) findViewById(R.id.frameLayout); // Obtiene la referencia de FrameLayout desde el layout

        fragment = new infoFragment(); // Crea una instancia del fragmento "infoFragment"
        fragmentManager = getSupportFragmentManager(); // Obtiene el FragmentManager
        fragmentTransaction = fragmentManager.beginTransaction(); // Inicia una transacción de fragmento
        fragmentTransaction.replace(R.id.frameLayout, fragment); // Reemplaza el contenido del FrameLayout con el fragmento "infoFragment"
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); // Establece la transición de fragmento
        fragmentTransaction.commit(); // Aplica los cambios

        infoFragment infoFragment = new infoFragment(); // Crea una instancia del fragmento "infoFragment"
        recorFragment recorFragment = new recorFragment(); // Crea una instancia del fragmento "recorFragment"
        dietFragment dietFragment = new dietFragment(); // Crea una instancia del fragmento "dietFragment"

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Se ejecuta cuando se selecciona una pestaña del TabLayout

                switch (tab.getPosition()) {
                    case 0:
                        fragment = infoFragment; // Asigna el fragmento "infoFragment" cuando se selecciona la primera pestaña
                        break;
                    case 1:
                        fragment = recorFragment; // Asigna el fragmento "recorFragment" cuando se selecciona la segunda pestaña
                        break;
                    case 2:
                        fragment = dietFragment; // Asigna el fragmento "dietFragment" cuando se selecciona la tercera pestaña
                        break;
                    default:
                        fragment = null;
                }

                if (fragment != null) {
                    FragmentManager fm = getSupportFragmentManager(); // Obtiene el FragmentManager
                    FragmentTransaction ft = fm.beginTransaction(); // Inicia una transacción de fragmento
                    ft.replace(R.id.frameLayout, fragment); // Reemplaza el contenido del FrameLayout con el fragmento correspondiente
                    ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN); // Establece la transición de fragmento
                    ft.commit(); // Aplica los cambios de la transacción
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Se ejecuta cuando se deselecciona una pestaña del TabLayout
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Se ejecuta cuando se vuelve a seleccionar una pestaña ya seleccionada del TabLayout
            }
        });
    }
}
