package com.gesproysoft.invitapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PruebaFireBase extends AppCompatActivity {
    private static final String TAG = "P_CONEXION_FB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.prueba_firebase);

        setTitle("Menu");

        Button myButton1 = (Button) findViewById(R.id.your_id1);
        myButton1.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View view) {
                Boolean exitosa;
                List<DocumentSnapshot> resultado = new ArrayList<>();
                Funciones_FireBase f_FB = new Funciones_FireBase();
                exitosa = f_FB.infoInvitados("YybJNIeSZMVaXWK9Yoqo", resultado);

                if(exitosa){
                    if(resultado.size() > 0) {//Hay eventos
                        Log.d("BOTON_LEER", "Hay invitados con ese id_evento");
                        for (DocumentSnapshot d : resultado) {
                            Log.d("BOTON_LEER", "Datos de invitado " + d.getId() + ": " + d.get("nombre") + " "
                            + d.get("genero") + " " + d.get("email") + " " + d.get("DNI"));
                        }
                    } else{
                        Log.d("BOTON_LEER", "No hay invitados en ese evento");
                    }
                } else {
                    Log.d("BOTON_LEER", "Fallo en la comunicacion con infoEventos");
                }

            }
        });

        Button myButton2 = (Button) findViewById(R.id.your_id2);
        myButton2.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View view){
                //prubasInfoEventos();
                prubasValidarInvitacionActualiza("23344556Z","hVWftX1RrgJgHuKwjucI");
            }
        });

    }
    //Ejemplo para añadir un documento de tipo "Invitados"
    private void agnadirDocumento() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Create a new user with a first and last name
        Map<String, Object> nue_inv = new HashMap<>();
        nue_inv.put("nombre", "Lucia");
        nue_inv.put("apellido", "Desmon");
        nue_inv.put("dni", "66666777d");
        nue_inv.put("correo electrónico", "prueba4@gmail.com");

        // Add a new document with a generated ID
        db.collection("Invitados")
                .add(nue_inv)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding document", e);
                    }
                });
    }

    //Saca todos los documentos de la coleccion "Invitados"
    private void leerDocumento() {
        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("Invitados")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    /**
     * Pruebas sobre la funcion InfoEventos
     */
    private void prubasInfoEventos() {
        String log;
        int pruebas = 2;//Numero de pruebas
        int p_correcta = 0;
        List<DocumentSnapshot> datos = new ArrayList<>();
        Funciones_FireBase db = new Funciones_FireBase();

        //Prueba 1: Se ha podido establecer la conexion con la coleccion eventos
        log = "P_1_InfoEventos";
        Log.d(log, "Prueba de conexion con coleccion de Eventos");
        Boolean exitosa;
        exitosa = db.infoEventos(datos);

        if(exitosa){
            Log.d(log, "CORRECTO---Conexion establecida");
            p_correcta++;
            for(DocumentSnapshot d: datos){
                Log.d(log, "Datos de evento: " + d.getData());
            }
        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con infoEventos");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 2: Hay eventos creados en la BD
        log = "P_2_InfoEventos";
        Log.d(log, "Prueba de comprobar si hay eventos definidos");
        exitosa = db.infoEventos(datos);

        if(exitosa){
            if(datos.size() > 0) {
                Log.d(log, "CORRECTO---Conexion establecida");
                p_correcta++;
                for (DocumentSnapshot d : datos) {
                    Log.d(log, "Datos de evento: " + d.getData());
                }
            } else {
                Log.d(log, "INCORRECTO---No hay eventos definidos");
            }
        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con infoEventos");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Verificacion de que pasa todas las pruebas
        log = "P_FINAL_InfoEventos";
        if(p_correcta == pruebas){
            Log.d(log, "¡¡¡TESTS CORRECTOS!!!");
        } else {
            Log.i(log, "¡¡¡FALLO EN LAS PRUEBAS!!!");
        }
    }

    /**
     * Pruebas sobre la funcion InfoEventos:
     * Ha esta función se le pasa el DNI de un invitado existente
     * en el evento del id_evento dado
     */
    private void prubasValidarInvitacionActualiza(String DNI, String id_evento) {
        String log;
        int pruebas = 4;//Numero de pruebas
        int p_correcta = 0;
        Funciones_FireBase db = new Funciones_FireBase();

        //Prueba 1: Se ha podido establecer la conexion con la coleccion de Invitados
        log = "P_1_ValInvAct";
        Log.d(log, "Prueba de conexion con coleccion de invitados");

        Boolean exitosa;
        List<Boolean> respuesta = new ArrayList<>();
        exitosa = db.validarInvitacionActualiza("456g", id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            Log.d(log, "CORRECTO---Conexion establecida");
            p_correcta++;
        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 2: El DNI pertenece a un usuario que esta invitado en el evento
        log = "P_2_ValInvAct";
        Log.d(log, "Prueba de validacion de un usuario que esta invitado");

        exitosa = db.validarInvitacionActualiza(DNI, id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.get(0)){
                Log.d(log, "CORRECTO---Hay invitado con ese DNI en la tabla de Invitados");
                p_correcta++;

            } else {
                Log.d(log, "INCORRECTO---No existe el invitado con ese DNI en la tabla de Invitados");
            }

        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 3: El DNI no pertenece a un usuario que esta invitado en el evento
        log = "P_3_ValInvAct";
        Log.d(log, "Prueba de validacion de un usuario que no esta invitado");

        exitosa = db.validarInvitacionActualiza("456g", id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.get(0)){
                Log.d(log, "INCORRECTO---Hay invitado con ese DNI en la tabla de Invitados");
            } else {
                Log.d(log, "CORRECTO---No existe el invitado con ese DNI en la tabla de Invitados");
                p_correcta++;
            }

        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 4: No existen eventos con ese id
        log = "P_4_ValInvAct";
        Log.d(log, "Prueba de comportamiento ante id de un evento no existente");

        exitosa = db.validarInvitacionActualiza(DNI, "abcdef", respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.get(0)){
                Log.d(log, "INCORRECTO---Existe esa tabla de eventos");
            } else {
                Log.d(log, "CORRECTO---No existen eventos con ese identificador");
                p_correcta++;
            }

        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Verificacion de que pasa todas las pruebas
        log = "P_FINAL_InfoEventos";
        if (p_correcta == pruebas) {
            Log.d(log, "¡¡¡TESTS CORRECTOS!!!");
        } else {
            Log.i(log, "¡¡¡FALLO EN LAS PRUEBAS!!!");
        }
    }

}
