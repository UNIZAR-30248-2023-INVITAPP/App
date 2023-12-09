package com.gesproysoft.invitapp;

import android.Manifest;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.developer.kalert.KAlertDialog;
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
import java.util.ArrayList;
import java.util.List;

public class IniciarSesion extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {

    private static final int RC_SIGN_IN = 9001;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton mSignInButton;

    EditText ED_Usuario, ED_Clave;

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


        rl_IniciarSesion = findViewById(R.id.BT_Iniciar_Sesion);
        TV_Iniciar_Sesion = findViewById(R.id.TV_Iniciar_Sesion);
        circulo_cargando = findViewById(R.id.circulo_cargando);
        ED_Clave = findViewById(R.id.ED_Clave);
        ED_Usuario = findViewById(R.id.ED_Usuario);
        circulo_cargando.setAlpha(0);
        rl_IniciarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                circulo_cargando.setAlpha(1);
                TV_Iniciar_Sesion.setAlpha(0);

                Funciones_FireBase db = new Funciones_FireBase();
                List<Boolean> respuesta = new ArrayList<>();
                Boolean exitosa;
                exitosa = db.iniciarSesion(ED_Usuario.getText().toString(),ED_Clave.getText().toString(),respuesta);
                if (exitosa && respuesta.get(0)){
                    SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("IA_Prefs", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("U_Identificador", ED_Usuario.getText().toString());
                    editor.apply();
                    Intent seleccionarEvento = new Intent(IniciarSesion.this,SeleccionarEvento.class);
                    startActivity(seleccionarEvento);
                    finish();
                }
                else{
                    new KAlertDialog(IniciarSesion.this, KAlertDialog.ERROR_TYPE)
                            .setTitleText("¡Error!")
                            .setContentText("Los datos de inicio de sesion son incorrectos")
                            .setConfirmClickListener("Cerrar", null)
                            .show();
                    circulo_cargando.setAlpha(0);
                    TV_Iniciar_Sesion.setAlpha(1);
                }

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






}