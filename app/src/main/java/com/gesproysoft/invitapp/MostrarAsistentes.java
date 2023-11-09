package com.gesproysoft.invitapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MostrarAsistentes extends AppCompatActivity {
    TextView TV_Atras,TV_Nombre_Evento_Cabecera;
    String eNombre;

    TextView TV_Ningun_Asistente;
    SwipeRefreshLayout refrescar_LV_Asistentes;
    ListView LV_Asistentes;
    ImageView IV_Buscar;
    EditText ED_Buscar;
    RelativeLayout RL_Cargando, RL_Asistentes;
    String uNombre, uUsuario, eIdEvento, eNombreEvento, url_GetAsistentesEvento;
    ArrayList<String> nombreAsistente, dniAsistente, horaLectura, estaLeida, leidaPor, nombreEntrada, descripcionEntrada, codigoEntrada;
    List<Map<String, String>> listaInvitados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mostrar_asistentes);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        View root = findViewById(android.R.id.content); // obtén la vista raíz de la actividad
        root.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    // Si se toca fuera de un EditText, ocultar el teclado virtual
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    return true;
                }
                return false;
            }
        });

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("IA_Prefs", Context.MODE_PRIVATE);

        // Recupera la representación JSON de la lista de invitados
        String jsonListaInvitados = sharedPreferences.getString("E_ListaInvitados", null);
        eNombre = sharedPreferences.getString("E_NombreEvento", ""); // Recupera el valor almacenado en "U_Nombre"
        System.out.println("NOMBRE OBTENIDO "+eNombre);


        if (jsonListaInvitados != null) {
            // Convierte el JSON nuevamente a la lista de invitados
            Gson gson = new Gson();
            Type type = new TypeToken<List<Map<String, String>>>() {}.getType();
            listaInvitados = gson.fromJson(jsonListaInvitados, type);

            // Ahora tienes tu lista de invitados lista para usar en el nuevo Intent
        }

        TV_Atras = findViewById(R.id.TV_Atras);
        TV_Atras.setText("<");
        TV_Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        TV_Nombre_Evento_Cabecera = findViewById(R.id.TV_Nombre_Evento_Cabecera);
        TV_Nombre_Evento_Cabecera.setText(eNombre);

        nombreAsistente = new ArrayList<String>();
        dniAsistente = new ArrayList<String>();
        horaLectura = new ArrayList<String>();
        estaLeida = new ArrayList<String>();
        leidaPor = new ArrayList<String>();
        nombreEntrada = new ArrayList<String>();
        descripcionEntrada = new ArrayList<String>();
        codigoEntrada = new ArrayList<String>();

        IV_Buscar = findViewById(R.id.IV_Buscar);
        ED_Buscar = findViewById(R.id.ED_Buscar);
        RL_Cargando = findViewById(R.id.RL_Cargando);
        refrescar_LV_Asistentes = findViewById(R.id.refrescar_LV_Asistentes);
        LV_Asistentes = findViewById(R.id.LV_Asistentes);
        RL_Asistentes = findViewById(R.id.RL_Asistentes);
        RL_Asistentes.setPadding(0,getStatusBarHeight(),0,0);
        TV_Ningun_Asistente = findViewById(R.id.TV_Ningun_Asistente);

        ED_Buscar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Este método se llama justo antes de que el texto cambie
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Este método se llama cuando el texto cambia
                // Aquí puedes realizar alguna acción, como guardar el cambio en una variable
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Este método se llama después de que el texto ha cambiado
                obtenerAsistentes(ED_Buscar.getText().toString());
            }
        });

        refrescar_LV_Asistentes.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refrescar_LV_Asistentes.setRefreshing(true);
                obtenerAsistentes(ED_Buscar.getText().toString());

            }
        });

        obtenerAsistentes("");

    }

    public void obtenerAsistentes(String buscar){
        IV_Buscar.setVisibility(View.INVISIBLE);
        RL_Cargando.setAlpha(1);

        nombreAsistente.clear();
        dniAsistente.clear();
        horaLectura.clear();
        estaLeida.clear();
        leidaPor.clear();
        nombreEntrada.clear();
        descripcionEntrada.clear();
        codigoEntrada.clear();

        // Itera sobre la lista de invitados
        for (Map<String, String> invitado : listaInvitados) {
            // Obtén el nombre del invitado
            String nombre = invitado.get("nombre");
            if (nombre.contains(buscar)){
                nombreAsistente.add(nombre);

                // Obtén el DNI del invitado
                String dni = invitado.get("DNI");
                dniAsistente.add(dni);

                // Obtén el email del invitado
                String email = invitado.get("email");

                estaLeida.add("no");
                nombreEntrada.add(eNombre);
                horaLectura.add(" ");
                leidaPor.add("Prueba");
                descripcionEntrada.add("Entrada de invitación, no incluye consumiciones");
                codigoEntrada.add(" ");
            }
        }
        actualizarAdaptadorSeleccion();



    }

    void actualizarAdaptadorSeleccion(){
        asistentesAdaptador adaptador = new asistentesAdaptador(MostrarAsistentes.this, nombreAsistente, dniAsistente, horaLectura, estaLeida, leidaPor, nombreEntrada, descripcionEntrada, codigoEntrada, uUsuario,eIdEvento);
        LV_Asistentes.setAdapter(adaptador);
        adaptador.notifyDataSetChanged();
        refrescar_LV_Asistentes.setRefreshing(false);
        IV_Buscar.setVisibility(View.VISIBLE);
        RL_Cargando.setAlpha(0);

        if (!nombreAsistente.isEmpty()){
            TV_Ningun_Asistente.setAlpha(0);
        }
        else{
            TV_Ningun_Asistente.setAlpha(1);
        }
    }

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
