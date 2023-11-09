package com.gesproysoft.invitapp;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOutlineProvider;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import eightbitlab.com.blurview.BlurView;
import eightbitlab.com.blurview.RenderScriptBlur;

public class eventosAdaptador extends ArrayAdapter<String> {

    private final Activity context;
    ArrayList<String> nombreEvento, fechaEvento, imagenEvento, lugarEvento;

    public eventosAdaptador(Activity context, ArrayList<String>  nombreEvento, ArrayList<String>  fechaEvento, ArrayList<String>  imagenEvento, ArrayList<String>  lugarEvento) {
        super(context, R.layout.lista_eventos, nombreEvento);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.nombreEvento = nombreEvento;
        this.fechaEvento = fechaEvento;
        this.imagenEvento = imagenEvento;
        this.lugarEvento = lugarEvento;


    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();
        View rowView=inflater.inflate(R.layout.lista_eventos, null,true);

        TextView nombre_evento = (TextView) rowView.findViewById(R.id.TV_Nombre_Evento);
        TextView fecha_evento = (TextView) rowView.findViewById(R.id.TV_Fecha_Evento);
        TextView lugar_evento = (TextView) rowView.findViewById(R.id.TV_Lugar_Evento);
        ImageView imagen_evento = (ImageView) rowView.findViewById(R.id.IV_Fondo_Evento);
        BlurView blur = rowView.findViewById(R.id.blurView);

        nombre_evento.setText(nombreEvento.get(position));
        fecha_evento.setText(fechaEvento.get(position));
        lugar_evento.setText(lugarEvento.get(position));

        String url_imagen_fondo = imagenEvento.get(position);
        int desiredHeightInDp = 160;
        float density = Resources.getSystem().getDisplayMetrics().density;
        int desiredHeightInPixels = (int) (desiredHeightInDp * density + 0.5f);

        // Establecer la altura deseada del ImageView
        ViewGroup.LayoutParams layoutParams = imagen_evento.getLayoutParams();
        layoutParams.height = desiredHeightInPixels;
        imagen_evento.setLayoutParams(layoutParams);
        Picasso.get().load(url_imagen_fondo).fit().centerCrop().into(imagen_evento);

        float radius = 5f;

        View decorView = context.getWindow().getDecorView();
        // ViewGroup you want to start blur from. Choose root as close to BlurView in hierarchy as possible.
        ViewGroup rootView = (ViewGroup) decorView.findViewById(android.R.id.content);

        // Optional:
        // Set drawable to draw in the beginning of each blurred frame.
        // Can be used in case your layout has a lot of transparent space and your content
        // gets a too low alpha value after blur is applied.
        Drawable windowBackground = decorView.getBackground();

        blur.setupWith(rootView, new RenderScriptBlur(context)) // or RenderEffectBlur
                .setFrameClearDrawable(windowBackground) // Optional
                .setBlurRadius(radius);
        blur.setOutlineProvider(ViewOutlineProvider.BACKGROUND);
        blur.setClipToOutline(true);



        return rowView;

    };
}