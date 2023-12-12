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
                List<Integer> respuesta = new ArrayList<>();
                Funciones_FireBase f_FB = new Funciones_FireBase();
                String DNI = "12345678Z", id_evento = "V61HJQ1ylMzbJddvX0lg";
                exitosa = f_FB.validarInvitacionAsistido(DNI, id_evento, respuesta);
                if (exitosa) {//Se ha establecido la consulta con exito
                    if (respuesta.get(0) == 0) {
                        Log.d("BOTON_LEER", "Hay invitado con ese DNI " + DNI + " en la tabla de Invitados(asistido a false)");
                    } else if (respuesta.get(0) == 1) {
                        Log.d("BOTON_LEER", "Hay invitado con ese DNI " + DNI + " en la tabla de Invitados(asistido a true)");
                    } else {
                        Log.d("BOTON_LEER", "No existe el invitado con ese DNI " + DNI + " en la tabla de Invitados");
                    }
                } else {
                    Log.d("BOTON_LEER", "Fallo en la comunicacion con validarInvitacionAsistido");
                }
            }
        });

        Button myButton2 = (Button) findViewById(R.id.your_id2);
        myButton2.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View view){
                //prubasInfoEventos();
                //prubasValidarInvitacionActualiza("23344556Z","hVWftX1RrgJgHuKwjucI");

                //Pruebas de funciones usadas en código
                //prubasInfoEventosOrganizador();
                //prubasValidarInvitacionActualizaReg();
                prubasInfoIvitados();
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

    /**
     * Pruebas sobre la funcion InfoEventosOrganizador:
     * En la función ya se crea el evento que se usa para validar, para
     * ver los resultados de forma facil ir a vista "LogCat" y buscar
     * por el nombre del log de la prueba "P_infoEvenOrg"
     */
    private void prubasInfoEventosOrganizador() {
        String log = "P_infoEvenOrg";
        int pruebas = 4;//Numero de pruebas
        int p_correcta = 0;//Pruebas correctas
        Funciones_FireBase db = new Funciones_FireBase();

        //Paso Previo: añadir evento de prueba
        Boolean exitosa;
        String organizador = "prueba@gmail.com";
        String nombre = "Prueba_InfoEventoOrg";
        String fecha = "2024-12-01";
        String hora = "00:00", ubicacion = "Centro Prueba";
        String id_evento;
        List<String> res = new ArrayList<>();
        exitosa = db.agnadirEvento(nombre, fecha, hora,
                ubicacion, organizador, res);
        if(exitosa) {//Se ha establecido la consulta con exito
            id_evento = res.get(0);
            Log.d(log + "-0", "CORRECTO---Evento añadido");
        } else {
            Log.d(log, "INCORRECTO---Evento no añadido");
        }

        //Prueba 1: Se ha podido establecer la conexion con la coleccion de Eventos
        Log.d(log + "-1", "Prueba de conexion con coleccion de invitados");

        List<DocumentSnapshot> respuesta = new ArrayList<>();
        exitosa = db.infoEventosOrganizador(organizador, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            Log.d(log + "-1", "CORRECTO---Conexion establecida");
            p_correcta++;
        } else {
            Log.d(log + "-1", "INCORRECTO---Fallo en la comunicacion con infoEventosOrganizador");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 2: El correo pertenece a un organizador que tiene eventos creados
        Log.d(log + "-2", "Prueba de validacion de que el organizador tiene eventos");

        exitosa = db.infoEventosOrganizador(organizador, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.size() > 0){
                Log.d(log + "-2", "CORRECTO---Hay eventos creados por ese organizador");
                p_correcta++;

            } else {
                Log.d(log + "-2", "INCORRECTO---No hay eventos creados por ese organizador");
            }

        } else {
            Log.d(log + "-2", "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 3: El correo pertenece a un organizador que no tiene eventos creados
        Log.d(log + "-3", "Prueba de validacion de que el no hay eventos de ese organizador");

        exitosa = db.infoEventosOrganizador("", respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.size() > 0){
                Log.d(log + "-3", "INCORRECTO---Hay eventos creados por ese organizador");

            } else {
                Log.d(log + "-3", "CORRECTO---No hay eventos creados por ese organizador");
                p_correcta++;
            }

        } else {
            Log.d(log, "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 4: La función devuvelve los valores del evento "Prueba_InfoEventoOrg"
        Log.d(log + "-4", "Prueba de comprobación de valores correctos");

        exitosa = db.infoEventosOrganizador(organizador, respuesta);
        Boolean encotrado = false;
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.size() > 0){
                for(int i = 0; i<respuesta.size(); i++){
                    if(respuesta.get(i).get("nombre").toString().equals(nombre) &&
                            respuesta.get(i).get("fecha").toString().equals(fecha) &&
                            respuesta.get(i).get("hora").toString().equals(hora) &&
                            respuesta.get(i).get("ubicacion").toString().equals(ubicacion)) {

                        Log.d(log + "-4", "CORRECTO---Valores del evento devueltos correctamente");
                        encotrado = true;
                        p_correcta++;
                        break;
                    }
                }
                if(!encotrado){
                    Log.d(log + "-4", "INCORRECTO---Valores del evento no encontrados");
                }
            } else {
                Log.d(log + "-4", "INCORRECTO---No hay eventos creados por ese organizador");
            }
        } else {
            Log.d(log + "-4", "INCORRECTO---Fallo en la comunicacion con valInvActualizada");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Verificacion de que pasa todas las pruebas
        if (p_correcta == pruebas) {
            Log.d(log + "-RESULTADO", "¡¡¡TESTS CORRECTOS!!!");
        } else {
            Log.i(log + "-RESULTADO", "¡¡¡FALLO EN LAS PRUEBAS!!!");
        }
    }

    /**
     * Pruebas sobre la funcion ValidarInvitacionActualizaReg:
     * En la función ya se crea el evento e invitado que se usan para validar, para
     * ver los resultados de forma facil ir a vista "LogCat" y buscar
     * por el nombre del log de la prueba "P_valInvActReg"
     */
    private void prubasValidarInvitacionActualizaReg() {
        String log = "P_valInvActReg";
        int pruebas = 4;//Numero de pruebas
        int p_correcta = 0;//Pruebas correctas
        Funciones_FireBase db = new Funciones_FireBase();

        //Paso Previo: añadir evento de prueba
        Boolean exitosa;
        String organizador = "prueba@gmail.com";
        String nombre = "Prueba_ValInvActReg";
        String fecha = "2024-12-01";
        String hora = "00:00", ubicacion = "Centro Prueba";
        String id_evento = "";
        List<String> res = new ArrayList<>();

        exitosa = db.agnadirEvento(nombre, fecha, hora,
                ubicacion, organizador, res);
        if(exitosa) {//Se ha establecido la consulta con exito
            id_evento = res.get(0);
            Log.d(log + "-01", "CORRECTO---Evento añadido");
        } else {
            Log.d(log + "-01", "INCORRECTO---Evento no añadido");
        }

        //Paso Previo: añadir invitado de prueba
        String DNI = "12345678Z";
        String email = "inprub@gmail.com";
        String nom_inv = "Pedro";
        String genero = "Masculino";
        String id_invitado = "";

        exitosa = db.agnadirInvitado(id_evento, DNI, email,
                genero, nom_inv, res);
        if(exitosa) {//Se ha establecido la consulta con exito
            id_invitado = res.get(0);
            Log.d(log + "-02", "CORRECTO---Invitado añadido");
        } else {
            Log.d(log + "-02", "INCORRECTO---Invitado no añadido");
        }

        //Prueba 1: Se ha podido establecer la conexion con la coleccion de Invitados
        Log.d(log + "-1", "Prueba de conexion con coleccion de Invitados");

        List<Integer> respuesta = new ArrayList<>();
        exitosa = db.validarInvitacionActualizaReg("32", id_evento, respuesta);

        if(exitosa) {//Se ha establecido la consulta con exito
            Log.d(log + "-1", "CORRECTO---Conexion establecida");
            p_correcta++;
        } else {
            Log.d(log + "-1", "INCORRECTO---Fallo en la comunicacion con validarInvitacionActualizaReg");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 2: El DNI pertenece a un invitado sin registrar asistencia y la registramos
        Log.d(log + "-2", "Prueba de que no esta registrada la asistencia y la registramos");

        exitosa = db.validarInvitacionActualizaReg(DNI, id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.get(0) == 0) {
                Log.d(log + "-2", "CORRECTO---Asistencia de invitado sin registrar y se registra");
                p_correcta++;
            } else if(respuesta.get(0) == 1){
                Log.d(log + "-2", "INCORRECTO---Asistencia de invitado registrada");
            } else {
                Log.d(log + "-2", "INCORRECTO---No hay invitados con ese DNI");
            }

        } else {
            Log.d(log + "-2", "INCORRECTO---Fallo en la comunicacion con validarInvitacionActualizaReg");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 3: El DNI pertenece a un invitado registrado
        Log.d(log + "-3", "Prueba de que esta registrada la asistencia");

        exitosa = db.validarInvitacionActualizaReg(DNI, id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.get(0) == 0) {
                Log.d(log + "-3", "INCORRECTO---Asistencia de invitado sin registrar y se registra");
            } else if(respuesta.get(0) == 1){
                Log.d(log + "-3", "CORRECTO---Asistencia de invitado registrada");
                p_correcta++;
            } else {
                Log.d(log + "-3", "INCORRECTO---No hay invitados con ese DNI");
            }
        } else {
            Log.d(log + "-3", "INCORRECTO---Fallo en la comunicacion con validarInvitacionActualizaReg");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 4: El DNI no pertenece a un invitado del evento
        Log.d(log + "-4", "Prueba de que no existe el invitado con DNI \"32\"");

        exitosa = db.validarInvitacionActualizaReg("32", id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.get(0) == 0) {
                Log.d(log + "-4", "INCORRECTO---Asistencia de invitado sin registrar y se registra");
            } else if(respuesta.get(0) == 1){
                Log.d(log + "-4", "INCORRECTO---Asistencia de invitado registrada");
            } else {
                Log.d(log + "-4", "CORRECTO---No hay invitados con ese DNI");
                p_correcta++;
            }
        } else {
            Log.d(log + "-4", "INCORRECTO---Fallo en la comunicacion con validarInvitacionActualizaReg");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Verificacion de que pasa todas las pruebas
        if (p_correcta == pruebas) {
            Log.d(log + "-RESULTADO", "¡¡¡TESTS CORRECTOS!!!");
        } else {
            Log.i(log + "-RESULTADO", "¡¡¡FALLO EN LAS PRUEBAS!!!");
        }
    }

    /**
     * Pruebas sobre la funcion InfoIvitados:
     * En la función ya se crea el evento e invitado que se usan para validar, para
     * ver los resultados de forma facil ir a vista "LogCat" y buscar
     * por el nombre del log de la prueba "P_infoIvitados"
     */
    private void prubasInfoIvitados() {
        String log = "P_infoIvitados";
        int pruebas = 3;//Numero de pruebas
        int p_correcta = 0;//Pruebas correctas
        Funciones_FireBase db = new Funciones_FireBase();

        //Paso Previo: añadir evento de prueba
        Boolean exitosa;
        String organizador = "prueba@gmail.com";
        String nombre = "Prueba_InfoInv";
        String fecha = "2024-12-01";
        String hora = "00:00", ubicacion = "Centro Prueba";
        String id_evento = "";
        List<String> res = new ArrayList<>();

        exitosa = db.agnadirEvento(nombre, fecha, hora,
                ubicacion, organizador, res);
        if(exitosa) {//Se ha establecido la consulta con exito
            id_evento = res.get(0);
            Log.d(log + "-01", "CORRECTO---Evento añadido");
        } else {
            Log.d(log + "-01", "INCORRECTO---Evento no añadido");
        }

        //Paso Previo: añadir invitado de prueba
        String DNI = "12345678Z";
        String email = "inprub@gmail.com";
        String nom_inv = "Pedro";
        String genero = "Masculino";
        String id_invitado = "";

        exitosa = db.agnadirInvitado(id_evento, DNI, email,
                genero, nom_inv, res);
        if(exitosa) {//Se ha establecido la consulta con exito
            id_invitado = res.get(0);
            Log.d(log + "-02", "CORRECTO---Invitado añadido");
        } else {
            Log.d(log + "-02", "INCORRECTO---Invitado no añadido");
        }

        //Prueba 1: Se ha podido establecer la conexion con la coleccion de Invitados
        Log.d(log + "-1", "Prueba de conexion con coleccion de Invitados");

        List<DocumentSnapshot> respuesta = new ArrayList<>();
        exitosa = db.infoInvitados(id_evento, respuesta);

        if(exitosa) {//Se ha establecido la consulta con exito
            Log.d(log + "-1", "CORRECTO---Conexion establecida");
            p_correcta++;
        } else {
            Log.d(log + "-1", "INCORRECTO---Fallo en la comunicacion con infoInvitados");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 2: Prueba de que hay invitados en el evento
        Log.d(log + "-2", "Prueba de que hay invitados en el evento");

        exitosa = db.infoInvitados(id_evento, respuesta);
        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.size() > 0) {
                Log.d(log + "-2", "CORRECTO---Hay invitados en el evento");
                p_correcta++;
            } else {
                Log.d(log + "-2", "INCORRECTO---No hay invitados en el evento");
            }

        } else {
            Log.d(log + "-2", "INCORRECTO---Fallo en la comunicacion con infoInvitados");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Prueba 3: Prueba que esta el invitado con DNI 12345678Z
        Log.d(log + "-3", "Prueba de que hay invitado con DNI 12345678Z");

        Boolean encontrado = false;

        exitosa = db.infoInvitados(id_evento, respuesta);

        if(exitosa) {//Se ha establecido la consulta con exito
            if(respuesta.size() > 0) {
                for(int i = 0; i < respuesta.size(); i++){
                    if(respuesta.get(i).get("DNI").toString().equals(DNI)){
                        encontrado = true;
                        Log.d(log + "-3", "CORRECTO---Hay invitado con DNI 12345678Z");
                        p_correcta++;
                    }
                }
                if(!encontrado){
                    Log.d(log + "-3", "INCORRECTO---No hay invitado con DNI 12345678Z");
                }
            } else {
                Log.d(log + "-3", "INCORRECTO---No hay invitados en el evento");
            }

        } else {
            Log.d(log + "-3", "INCORRECTO---Fallo en la comunicacion con infoInvitados");
        }

        Log.d(log, "+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");

        //Verificacion de que pasa todas las pruebas
        if (p_correcta == pruebas) {
            Log.d(log + "-RESULTADO", "¡¡¡TESTS CORRECTOS!!!");
        } else {
            Log.i(log + "-RESULTADO", "¡¡¡FALLO EN LAS PRUEBAS!!!");
        }
    }
}
