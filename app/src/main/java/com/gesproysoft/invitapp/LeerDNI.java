package com.gesproysoft.invitapp;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.Result;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.nio.ByteBuffer;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class LeerDNI extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;
    private CodeScanner mCodeScanner;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private TessBaseAPI tessBaseAPI;
    CardView CV_Nombre, CV_Dni, CV_Ppal;
    String eNombre,eId;
    TextView TV_Atras,TV_Nombre_Evento;
    RelativeLayout BT_Asistentes;

    TextView TV_Porcentaje;
    View V_Barra_Porcentaje, V_Barra_Vacia;
    RelativeLayout RL_Cargando, RL_Informacion;
    Boolean puedeLeer = true;
    String url_Get_Info;
    TextView TV_Descripcion_Entrada, TV_Nombre_Asistente, TV_DNI_Asistente, TV_Estado_Entrada;

    private Handler handler,handler2;
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_dni);

        CV_Dni = findViewById(R.id.CV_DNI);
        CV_Nombre = findViewById(R.id.CV_Nombre);
        CV_Ppal = findViewById(R.id.card_view);
        TV_Estado_Entrada = findViewById(R.id.TV_Estado_Entrada);
        TV_DNI_Asistente = findViewById(R.id.TV_DNI_Asistente);
        TV_Nombre_Asistente = findViewById(R.id.TV_Nombre_Asistente);
        TV_Descripcion_Entrada = findViewById(R.id.TV_Descripcion_Entrada);
        TV_Porcentaje = findViewById(R.id.TV_Porcentaje);
        V_Barra_Porcentaje = findViewById(R.id.V_Barra_Porcentaje);
        V_Barra_Vacia = findViewById(R.id.V_Barra_Vacia);
        RL_Informacion = findViewById(R.id.RL_Informacion);
        RL_Cargando = findViewById(R.id.RL_Cargando);
        RL_Cargando.setAlpha(0f);
        RL_Informacion.setAlpha(0f);

        BT_Asistentes = findViewById(R.id.BT_Asistentes);
        BT_Asistentes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent asistentes = new Intent(LeerDNI.this, MostrarAsistentes.class);
                startActivity(asistentes);
            }
        });

        TV_Nombre_Evento = findViewById(R.id.TV_Nombre_Entrada);
        TV_Atras = findViewById(R.id.TV_Atras);
        TV_Atras.setText("<");
        TV_Atras.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA}, 200);
        }

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("IA_Prefs", Context.MODE_PRIVATE);
        eNombre = sharedPreferences.getString("E_NombreEvento", ""); // Recupera el valor almacenado en "U_Nombre"
        eId = sharedPreferences.getString("E_IdEvento", "");

        TV_Nombre_Evento.setText(eNombre);


        handler2 = new Handler(Looper.getMainLooper());

        handler2.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Código que se ejecutará después de 1 segundo
                ObjectAnimator animator = ObjectAnimator.ofFloat(CV_Ppal, "alpha", 0f, 1f);
                animator.setDuration(200); // Duración de la animación en milisegundos (0.2 segundos)

                // Iniciar la animación
                animator.start();
            }
        }, 500);

        CodeScannerView scannerView = findViewById(R.id.scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mCodeScanner.startPreview();
                        if(puedeLeer){
                            puedeLeer=false;

                            RL_Informacion.setAlpha(0f);
                            RL_Cargando.setAlpha(1f);

                            Funciones_FireBase db = new Funciones_FireBase();
                            Boolean exitosa;
                            List<Boolean> respuesta = new ArrayList<>();
                            exitosa = db.validarInvitacionActualiza(result.getText(), eId, respuesta);
                        }


                    }
                });
            }
        });

    }

    public void permitirLeer(int milisegundos){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Código que se ejecutará después de x segundos en el hilo de fondo
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Código que se ejecutará en el hilo de la interfaz de usuario
                        puedeLeer = true;
                    }
                });
            }
        }, milisegundos);
    }

    public void actualizarPorcentaje(){}

    @Override
    protected void onResume() {
        super.onResume();
        mCodeScanner.startPreview();
        handler.postDelayed(runnable, 0);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCodeScanner.releaseResources();
        handler.removeCallbacks(runnable);
    }








}
