package com.gesproysoft.invitapp;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Rect;
import android.text.InputFilter;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.angads25.toggle.interfaces.OnToggledListener;
import com.github.angads25.toggle.model.ToggleableView;
import com.github.angads25.toggle.widget.LabeledSwitch;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import androidx.cardview.widget.CardView;

public class asistentesAdaptador extends ArrayAdapter<String> {

    private final Activity context;
    LabeledSwitch toogleLeer;
    ArrayList<String> nombreAsistente, dniAsistente, horaLectura, estaLeida, leidaPor, nombreEntrada, descripcionEntrada, codigoEntrada, idEvento;
    String uUsuario,eIdEvento;
    RelativeLayout RL_Cargando;

    public asistentesAdaptador(Activity context, ArrayList<String>  nombreAsistente, ArrayList<String>  dniAsistente, ArrayList<String>  horaLectura, ArrayList<String>  estaLeida, ArrayList<String>  leidaPor, ArrayList<String>  nombreEntrada, ArrayList<String>  descripcionEntrada, ArrayList<String>  codigoEntrada, String usuarioLector, String idEvento) {
        super(context, R.layout.lista_eventos, nombreAsistente);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.nombreAsistente = nombreAsistente;
        this.dniAsistente = dniAsistente;
        this.horaLectura = horaLectura;
        this.estaLeida = estaLeida;
        this.leidaPor = leidaPor;
        this.nombreEntrada = nombreEntrada;
        this.descripcionEntrada = descripcionEntrada;
        this.codigoEntrada = codigoEntrada;
        this.uUsuario = usuarioLector;
        this.eIdEvento = idEvento;



    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.lista_asistentes, null,true);

        TextView nombre_entrada = (TextView) rowView.findViewById(R.id.TV_Nombre_Entrada);
        TextView descripcion_entrada= (TextView) rowView.findViewById(R.id.TV_Descripcion_Entrada);
        TextView nombre_asistente = (TextView) rowView.findViewById(R.id.TV_Nombre_Asistente);
        TextView dni_asistente = (TextView) rowView.findViewById(R.id.TV_DNI_Asistente);
        TextView genero_asistente = (TextView) rowView.findViewById(R.id.TV_GENERO);
        TextView estado_entrada = (TextView) rowView.findViewById(R.id.TV_Estado_Entrada);
        RelativeLayout general = (RelativeLayout) rowView.findViewById(R.id.RL_Informacion);
        LabeledSwitch toogleLeer = (LabeledSwitch) rowView.findViewById(R.id.Toggle_Leer);
        RelativeLayout RL_Cargando = (RelativeLayout) rowView.findViewById(R.id.RL_Cargando);

        /*String nombre = nombreAsistente.get(position);
        if (nombre.length() > 25) {
            nombre = nombre.substring(0, 25 - 3) + "...";
        }*/

        String nombre = nombreAsistente.get(position);
        int maxLength = 9; // Longitud máxima deseada para el texto


        float density2 = Resources.getSystem().getDisplayMetrics().density;
        int maxLengthDp = (int) (maxLength * density2);

        if (nombre.length() > maxLengthDp) {
            nombre = nombre.substring(0, maxLengthDp - 3) + "...";
        }





        nombre_entrada.setText(nombreEntrada.get(position));
        nombre_asistente.setText(nombre);
        dni_asistente.setText(dniAsistente.get(position));
        if (nombre.contains("Masculino")){
            genero_asistente.setText("♂");
        }
        else if (nombre.contains("Femenino")){
            genero_asistente.setText("♀");
        }

        if (estaLeida.get(position).matches("si")){
            estado_entrada.setText("Leida");
            estado_entrada.setBackgroundResource(R.drawable.verde_esquinas_redondas);
            toogleLeer.setOn(true);


            descripcion_entrada.setText("Esta entrada ya ha sido leida anteriormente por un lector de InvitApp");
        }
        else{
            estado_entrada.setText("No\nLeida");
            estado_entrada.setBackgroundResource(R.drawable.gris_esquinas_redondas);
            descripcion_entrada.setText(descripcionEntrada.get(position));
        }

        int desiredHeightInDp = 130;
        float density = Resources.getSystem().getDisplayMetrics().density;
        int desiredHeightInPixels = (int) (desiredHeightInDp * density + 0.5f);
        ViewGroup.LayoutParams layoutParams = general.getLayoutParams();
        layoutParams.height = desiredHeightInPixels;
        general.setLayoutParams(layoutParams);

        toogleLeer.setOnToggledListener(new OnToggledListener() {
            @Override
            public void onSwitched(ToggleableView toggleableView, boolean isOn) {
                if (isOn){
                    RL_Cargando.setBackgroundResource(R.drawable.gris_esquinas_redondas);
                    RL_Cargando.setAlpha(1);
                    /*if (resultado.contains("Entrada leida correctamente")){
                        estado_entrada.setText("Leida");
                        estado_entrada.setBackgroundResource(R.drawable.verde_esquinas_redondas);

                        SimpleDateFormat dateFormatter = new SimpleDateFormat("EEEE d 'de' MMMM 'de' yyyy 'a las' HH:mm", new Locale("es", "ES"));
                        Date fechaActual = new Date(System.currentTimeMillis());

                        String fechaFormateada = dateFormatter.format(fechaActual);
                        descripcion_entrada.setText("Leida el " + fechaFormateada + " por " + uUsuario);

                    } // Es necesario hacer un caso en el que ya se haya leido la entrada? Tiene sentido?
                    else {
                        toogleLeer.setOn(false);

                    }
                    RL_Cargando.setAlpha(0f);*/
                }
                else{
                    //Des-leer entrada
                    RL_Cargando.setBackgroundResource(R.drawable.verde_esquinas_redondas);
                    RL_Cargando.setAlpha(1);
                    /*if (resultado.contains("Entrada desleida correctamente")){
                        estado_entrada.setText("No\nLeida");
                        estado_entrada.setBackgroundResource(R.drawable.gris_esquinas_redondas);
                        String[] datos_asistente = resultado.split(" = ");
                        descripcion_entrada.setText(datos_asistente[3]);


                    } // Es necesario hacer un caso en el que ya se haya des-leido la entrada? Tiene sentido?
                                                    else {
                        toogleLeer.setOn(true);

                    }
                    RL_Cargando.setAlpha(0f);*/
                }
            }
        });

        return rowView;

    };
}
