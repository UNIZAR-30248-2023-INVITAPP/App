package com.gesproysoft.invitapp;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.SignInButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class IniciarSesion extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;

    RelativeLayout rl_IniciarSesion;
    SpinKitView circulo_cargando;
    TextView TV_Iniciar_Sesion;
    private static final int REQUEST_WRITE_EXTERNAL_STORAGE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iniciar_sesion);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        copyLanguageDataToStorage();

        rl_IniciarSesion = findViewById(R.id.BT_Iniciar_Sesion);
        TV_Iniciar_Sesion = findViewById(R.id.TV_Iniciar_Sesion);
        circulo_cargando = findViewById(R.id.circulo_cargando);
        circulo_cargando.setAlpha(0);
        rl_IniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circulo_cargando.setAlpha(1);
                TV_Iniciar_Sesion.setAlpha(0);
                Intent seleccionarEvento = new Intent(IniciarSesion.this,SeleccionarEvento.class);
                startActivity(seleccionarEvento);
                finish();
            }
        });

        mSignInButton = findViewById(R.id.sign_in_button);
        mSignInButton.setSize(SignInButton.SIZE_STANDARD);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

    }


    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            GoogleSignInAccount account = result.getSignInAccount();
            Intent seleccionarEvento = new Intent(IniciarSesion.this,SeleccionarEvento.class);
            startActivity(seleccionarEvento);
            finish();
            // Aquí puedes obtener información del usuario como el nombre, correo electrónico, etc.
        } else {
            // El inicio de sesión ha fallado, maneja el error aquí.
        }
    }

    private void copyLanguageDataToStorage() {
        String languageCode = "spa";  // Código de idioma
        String dataPath = getExternalFilesDir(null).getAbsolutePath() + File.separator + "tessdata";
        File languageFile = new File(dataPath, languageCode + ".traineddata");

        // Verifica si el directorio tessdata no existe y créalo si es necesario
        File tessdataDir = new File(dataPath);
        if (!tessdataDir.exists()) {
            tessdataDir.mkdirs(); // Crea el directorio tessdata
        }

        if (!languageFile.exists()) {
            try {
                InputStream inputStream = getResources().openRawResource(R.raw.spa);
                OutputStream outputStream = new FileOutputStream(languageFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) > 0) {
                    outputStream.write(buffer, 0, length);
                }
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }





}