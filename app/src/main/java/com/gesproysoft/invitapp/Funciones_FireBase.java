package com.gesproysoft.invitapp;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Funciones_FireBase {
    /*Ejemplo de uso de la funcion validarInvitacion*/
    /*
    Boolean exitosa;
    List<Boolean> respuesta = new ArrayList<>();
    exitosa = validarInvitacion("232425678K", "TgBJQQ37XKA6SkrIMQo6", respuesta);
    if(exitosa) {//Se ha establecido la consulta con exito
        if(respuesta.get(0)){
            Log.d("BOTON_ESC", "Esta invitado el usuario");
        } else {
            Log.d("BOTON_ESC", "No esta invitado el usuario");
        }
    } else {
        Log.d("BOTON_ESC", "Fallo en la lectura de los datos de validarInvitacion");
    }
    */
    /**
     * Comprueba que el invitado con ese DNI se encuentre invitado en el evento seleccionado
     * Argumentos:
     *  id_evento -> pasamos el identificador del evento donde comprobar si esta invitado
     *  resultado -> guarda un List<Boolean> que contiene en su posicion 0, true o false
     *              true    esta en la lista
     *              false   no esta en la lista
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (aunque no haya contenido)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean validarInvitacion(String DNI, String id_evento, List<Boolean> resultado) {
        String tag = "F_VALIDAR_INVIT";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectRef = db.collection("Eventos").document(id_evento)
                .collection("Invitados");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de la lista

        tarea = collectRef.whereEqualTo("dni", DNI).get();//Solicitamos la información del evento

        //Comprobamos cada 0.20 segundos si ha terminado la tarea (maximo 3 segundos)
        for(int i = 0; i < 15 && !tarea.isComplete(); i++){
            //Log.d("F_INF_EVENTO", "Intento " + i + "-esimo");
            try{
                Thread.sleep(200);
            } catch (InterruptedException e){
                Log.d("EXCEPCION","Excepcion: " + e);
            }
        }

        if(tarea.isComplete()){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                Log.d("EXCEPCION",e.toString());
            }
            if(tarea.isSuccessful()){
                QuerySnapshot query_docs = tarea.getResult();
                if (query_docs.size() > 0) {
                    resultado.add(true);//Esta invitado
                    Log.d(tag, "Hay invitado con ese DNI");
                } else {
                    resultado.add(false);
                    Log.d(tag, "No hay invitados con ese DNI");
                }
                return true;//Se ha establecido la comunicacion
            } else {
                Log.d(tag, "Lectura no exitosa");
            }
        } else {
            Log.d(tag, "Tarea no completada (a expìrado el timeout)");
        }
        return false;
    }


    /*Ejemplo de uso de la funcion infoEventos*/
    /*
    List<DocumentSnapshot> datos = new ArrayList<>();

    Boolean exitosa;
    exitosa = infoEventos(datos);

    if(exitosa){
        for(DocumentSnapshot d: datos){
            Log.d("BOTON_LEER", "Datos de evento: " + d.getData());
        }
    } else {
        Log.d("BOTON_LEER", "Fallo en la comunicacion con infoEventos");
    }
    */

    /**
     * Argumentos:
     *  resultado -> guarda los datos de los eventos como un List<DocumentSnapshot>, si esta
     *               vacio es que no hay eventos
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (incluso si no hay eventos)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean infoEventos(List<DocumentSnapshot> resultado) {
        String tag = "F_INF_EVENTOS";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collecRef = db.collection("Eventos");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de salida

        tarea = collecRef.get();//Solicitamos la información del evento

        //Comprobamos cada 0.20 segundos si ha terminado la tarea (maximo 3 segundos)
        for(int i = 0; i < 15 && !tarea.isComplete(); i++){
            //Log.d("F_INF_EVENTO", "Intento " + i + "-esimo");
            try{
                Thread.sleep(200);
            } catch (InterruptedException e){
                Log.d("EXCEPCION","Excepcion: " + e);
            }
        }

        if(tarea.isComplete()){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                Log.d("EXCEPCION",e.toString());
            }
            if(tarea.isSuccessful()){
                QuerySnapshot query_docs = tarea.getResult();

                if (query_docs.size() > 0) {//Hay eventos
                    resultado.addAll(query_docs.getDocuments());//Guardamos los eventos
                    Log.d(tag, "Hay eventos registrados");
                } else { //No hay eventos
                    Log.d(tag, "No hay eventos registrados");
                }
                return true;//Se ha establecido la comunicacion

            } else {
                Log.d(tag, "Lectura no exitosa");
            }
        } else {
            Log.d(tag, "Tarea no completada (a expìrado el timeout)");
        }

        return false;
    }


    /**
     * Comprueba que el invitado con ese DNI se encuentre invitado en el evento seleccionado
     * Argumentos:
     *  id_evento -> pasamos el identificador del evento donde comprobar si esta invitado
     *  resultado -> guarda un List<Boolean> que contiene en su posicion 0, true o false
     *              true    esta en la lista y actualizamos el campo "asistido" a true del invitado
     *              false   no esta en la lista
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (aunque no haya contenido)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean validarInvitacionActualiza(String DNI, String id_evento, List<Boolean> resultado) {
        String tag = "F_VALIDAR_INVIT";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectRef = db.collection("Eventos").document(id_evento)
                .collection("Invitados");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de la lista

        tarea = collectRef.whereEqualTo("dni", DNI).get();//Solicitamos la información del evento

        //Comprobamos cada 0.20 segundos si ha terminado la tarea (maximo 3 segundos)
        for(int i = 0; i < 15 && !tarea.isComplete(); i++){
            //Log.d("F_INF_EVENTO", "Intento " + i + "-esimo");
            try{
                Thread.sleep(200);
            } catch (InterruptedException e){
                Log.d("EXCEPCION","Excepcion: " + e);
            }
        }

        if(tarea.isComplete()){
            try{
                Thread.sleep(1000);
            } catch (InterruptedException e){
                Log.d("EXCEPCION",e.toString());
            }
            if(tarea.isSuccessful()){
                QuerySnapshot query_docs = tarea.getResult();
                if (query_docs.size() > 0) {
                    DocumentSnapshot doc = query_docs.getDocuments().get(0);//Obtenemos el invitado que coincide
                    DocumentReference docRef = db.collection("Eventos").document(id_evento)
                            .collection("Invitados").document(doc.getId());
                    docRef.update("asistido", true);//Actualizamos el campo "asistido" a true del invitado

                    resultado.add(true);//Esta invitado
                    Log.d(tag, "Hay invitado con ese DNI y se ha actualizado la asistencia");
                } else {
                    resultado.add(false);
                    Log.d(tag, "No hay invitados con ese DNI");
                }
                return true;//Se ha establecido la comunicacion
            } else {
                Log.d(tag, "Lectura no exitosa");
            }
        } else {
            Log.d(tag, "Tarea no completada (a expìrado el timeout)");
        }
        return false;
    }
}