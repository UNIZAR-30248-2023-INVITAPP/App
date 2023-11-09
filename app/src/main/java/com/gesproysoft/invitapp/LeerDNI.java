package com.gesproysoft.invitapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
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
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;
import com.google.common.util.concurrent.ListenableFuture;
import com.googlecode.tesseract.android.TessBaseAPI;

import java.nio.ByteBuffer;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;

public class LeerDNI extends AppCompatActivity {
    private static final int REQUEST_CAMERA_PERMISSION = 100;
    private ListenableFuture<ProcessCameraProvider> cameraProviderFuture;
    PreviewView previewView;

    private HandlerThread backgroundThread;
    private Handler backgroundHandler;
    private TessBaseAPI tessBaseAPI;
    String eNombre;
    TextView TV_Atras,TV_Nombre_Evento;
    RelativeLayout BT_Asistentes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leer_dni);
        cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        previewView = findViewById(R.id.scanner_view);

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

        if (allPermissionsGranted()) {
            startCamera();
            //startCameraWithTimer();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }

        tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(getExternalFilesDir(null).getAbsolutePath(), "spa");

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("IA_Prefs", Context.MODE_PRIVATE);
        eNombre = sharedPreferences.getString("E_NombreEvento", ""); // Recupera el valor almacenado en "U_Nombre"

        TV_Nombre_Evento.setText(eNombre);

    }

    private boolean allPermissionsGranted() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (allPermissionsGranted()) {
                //startCamera();
                //startCameraWithTimer();
            } else {
                // Handle permission denial if needed.
            }
        }
    }

    private void startCamera() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);
            } catch (ExecutionException | InterruptedException e) {
                // No errors need to be handled for this Future.
                // This should never be reached.
            }
        }, ContextCompat.getMainExecutor(this));
    }

    private void bindPreview(@NonNull ProcessCameraProvider cameraProvider) {
        Preview preview = new Preview.Builder().build();

        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                .build();

        preview.setSurfaceProvider(previewView.getSurfaceProvider());

        Camera camera = cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview);
    }

    private void startCameraWithTimer() {
        backgroundThread = new HandlerThread("CameraThread", Process.THREAD_PRIORITY_BACKGROUND);
        backgroundThread.start();
        backgroundHandler = new Handler(backgroundThread.getLooper());

        final int delayMillis = 2000; // Capture every 2 seconds

        Runnable captureRunnable = new Runnable() {
            @Override
            public void run() {
                captureImageAndProcessText();
                backgroundHandler.postDelayed(this, delayMillis);
            }
        };

        backgroundHandler.postDelayed(captureRunnable, delayMillis);
    }

    private void captureImageAndProcessText() {
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();
                bindPreview(cameraProvider);

                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();

                // Utiliza ContextCompat.getMainExecutor(this) para obtener un Executor adecuado
                Executor executor = ContextCompat.getMainExecutor(this);

                imageAnalysis.setAnalyzer(executor, new ImageAnalysis.Analyzer() {
                    @OptIn(markerClass = ExperimentalGetImage.class) @Override
                    public void analyze(@NonNull ImageProxy image) {
                        // Procesa la imagen y extrae texto usando Tesseract

                        // Obtén la imagen de la cámara como un objeto Image
                        Image mediaImage = image.getImage();

                        if (mediaImage != null) {
                            // Convierte la imagen a un formato adecuado para Tesseract
                            ByteBuffer buffer = mediaImage.getPlanes()[0].getBuffer();
                            byte[] bytes = new byte[buffer.remaining()];
                            buffer.get(bytes);

                            // Configura Tesseract con la imagen
                            tessBaseAPI.setImage(bytes, mediaImage.getWidth(), mediaImage.getHeight(), 0, mediaImage.getWidth());

                            // Realiza el reconocimiento de texto
                            String recognizedText = tessBaseAPI.getUTF8Text();

                            // Aquí tienes el texto reconocido, puedes hacer lo que desees con él
                            Log.d("OCR Text", recognizedText);

                            // Asegúrate de liberar los recursos de la imagen
                            mediaImage.close();
                        }

                        // Asegúrate de liberar los recursos de la imagen de la cámara
                        image.close();
                    }
                });

                CameraSelector cameraSelector = new CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                        .build();

                Preview preview = new Preview.Builder().build();

                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, imageAnalysis, preview);
            } catch (ExecutionException | InterruptedException e) {
                // Maneja errores si es necesario.
            }
        }, ContextCompat.getMainExecutor(this));
    }




/*
    @Override
    protected void onDestroy() {
        super.onDestroy();
        tessBaseAPI.end();
        backgroundHandler.removeCallbacksAndMessages(null);
        backgroundThread.quitSafely();
    }*/
}
