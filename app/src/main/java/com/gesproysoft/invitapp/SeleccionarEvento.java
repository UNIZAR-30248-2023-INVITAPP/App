package com.gesproysoft.invitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SeleccionarEvento extends AppCompatActivity {

    String uNombre;
    String uUsuario;
    String url_GetEventosParaUsuario, url_GetDatosEvento;
    ListView LV_Eventos;
    SwipeRefreshLayout refrescar_LV_Eventos;
    TextView TV_EventosDisponibles;

    ArrayList<String> nombreEvento, fechaEvento, imagenEvento, lugarEvento,  idEvento;
    List<List<Map<String, String>>> invitadosEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_evento);

        LV_Eventos = findViewById(R.id.LV_Eventos);
        refrescar_LV_Eventos = findViewById(R.id.refrescar_LV_Eventos);
        TV_EventosDisponibles = findViewById(R.id.TV_Eventos_Disponibles);
        nombreEvento = new ArrayList<String>();
        fechaEvento = new ArrayList<String>();
        imagenEvento = new ArrayList<String>();
        lugarEvento = new ArrayList<String>();
        idEvento = new ArrayList<String>();
        invitadosEvento = new ArrayList<>();


        refrescar_LV_Eventos.setRefreshing(true);
        obtenerEventos();

        LV_Eventos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("IA_Prefs", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();

                // Convierte la lista de invitados en JSON
                Gson gson = new Gson();
                String jsonListaInvitados = gson.toJson(invitadosEvento.get(position));

                // Guarda la representación JSON en SharedPreferences
                editor.putString("E_ListaInvitados", jsonListaInvitados);
                editor.putString("E_NombreEvento", nombreEvento.get(position));

                editor.apply();

                SpinKitView circulo_cargando = view.findViewById(R.id.circulo_cargando);
                circulo_cargando.setAlpha(1);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Intent leerEntrada = new Intent(SeleccionarEvento.this, LeerDNI.class);
                        startActivity(leerEntrada);
                    }
                }).start();

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SpinKitView circulo_cargando = view.findViewById(R.id.circulo_cargando);
                        circulo_cargando.setAlpha(0);
                    }
                }, 2000);
            }
        });


    }

    public void obtenerEventos(){
        nombreEvento.clear();
        fechaEvento.clear();
        imagenEvento.clear();
        idEvento.clear();
        lugarEvento.clear();

        List<DocumentSnapshot> datos = new ArrayList<>();
        Funciones_FireBase db = new Funciones_FireBase();

        Boolean exitosa;
        exitosa = db.infoEventos(datos);

        if(exitosa){
            for(DocumentSnapshot d: datos){
                System.out.println(d.getData());
                Map<String, Object> data = d.getData();
                if (data != null) {
                    String fecha = (String) data.get("fecha");
                    String ubicacion = (String) data.get("ubicacion");
                    String hora = (String) data.get("hora");
                    List<Map<String, String>> invitados = (List<Map<String, String>>) data.get("invitados");
                    String nombre = (String) data.get("nombre");

                    invitadosEvento.add(invitados);
                    fechaEvento.add(fecha);
                    lugarEvento.add(ubicacion);
                    nombreEvento.add(nombre);
                    imagenEvento.add("https://nyxellpro.s3.amazonaws.com/sitename/2-uv6l63aopecrg4eei7vr1ggu7hcmhsd7n0crg20kd1jdp970nn14otro6av.jpg");
                    // Puedes agregar los demás datos a sus respectivos ArrayList
                }
            }

            actualizarAdaptadorSeleccion();
        } else {
            TV_EventosDisponibles.setAlpha(1);
            refrescar_LV_Eventos.setRefreshing(false);
        }
    }

    void actualizarAdaptadorSeleccion(){
        eventosAdaptador adaptador = new eventosAdaptador(SeleccionarEvento.this, nombreEvento, fechaEvento, imagenEvento, lugarEvento);
        LV_Eventos.setAdapter(adaptador);
        adaptador.notifyDataSetChanged();
        refrescar_LV_Eventos.setRefreshing(false);
        if (nombreEvento.isEmpty()){
            //Poner Mensaje fondo pantalla no hay eventos para leer
            TV_EventosDisponibles.setAlpha(1);
        }
        else{
            TV_EventosDisponibles.setAlpha(0);
        }
    }
}