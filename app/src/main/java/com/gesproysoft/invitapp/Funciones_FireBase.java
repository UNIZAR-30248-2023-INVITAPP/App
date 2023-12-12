package com.gesproysoft.invitapp;

import android.util.Log;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
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

/*
                Boolean exitosa;
                List<Boolean> respuesta = new ArrayList<>();
                Funciones_FireBase f_FB = new Funciones_FireBase();
                String DNI = "12345678Z", id_evento = "EuhJJf2RwRUAnIjEu9oj";
                exitosa = f_FB.validarInvitacionActualiza(DNI, id_evento, respuesta);
                if (exitosa) {//Se ha establecido la consulta con exito
                    if (respuesta.get(0)) {
                        Log.d("BOTON_LEER", "Hay invitado con ese DNI " + DNI + " en la tabla de Invitados");
                    } else {
                        Log.d("BOTON_LEER", "No existe el invitado con ese DNI " + DNI + " en la tabla de Invitados");
                    }
                } else {
                    Log.d("BOTON_LEER", "Fallo en la comunicacion con valInvActualizada");
                }
*/
    /**
     * Comprueba que el invitado con ese DNI se encuentre invitado en el evento seleccionado, y
     * actualiza el campo "asistido" a true y pone el campo "hora_asistiedo" con el timestamp actual
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

        tarea = collectRef.whereEqualTo("DNI", DNI).get();//Solicitamos la información del evento

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
                    docRef.update(
                            "asistido", true,
                            "hora_asistido", FieldValue.serverTimestamp()
                            );//Actualizamos el campo "asistido" a true del invitado y su hora de asistencia

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

    /*
    Boolean exitosa;
    List<Integer> respuesta = new ArrayList<>();
    Funciones_FireBase f_FB = new Funciones_FireBase();
    String DNI = "12345678", id_evento = "EuhJJf2RwRUAnIjEu9oj";
    exitosa = f_FB.validarInvitacionActualizaReg(DNI, id_evento, respuesta);
                if (exitosa) {//Se ha establecido la consulta con exito
        if (respuesta.get(0) == 0) {
            Log.d("BOTON_LEER", "Hay invitado con ese DNI " + DNI + " en la tabla de Invitados(asistido false)");
        } else if(respuesta.get(0) == 1) {
            Log.d("BOTON_LEER", "Hay invitado con ese DNI " + DNI + " en la tabla de Invitados(asistido true)");
        } else {
            Log.d("BOTON_LEER", "No existe el invitado con ese DNI " + DNI + " en la tabla de Invitados");
        }
    } else {
        Log.d("BOTON_LEER", "Fallo en la comunicacion con valInvActualizadaReg");
    }
    */
    /**
     * Comprueba que el invitado con ese DNI se encuentre invitado en el evento seleccionado y no se ha registrado ya,
     * además actualiza el campo "asistido" a true y pone el campo "hora_asistiedo" con el timestamp actual
     * Argumentos:
     *  id_evento -> pasamos el identificador del evento donde comprobar si esta invitado
     *  resultado -> guarda un List<Boolean> que contiene en su posicion 0, true o false
     *              0   esta en la lista con "asistido" a false y actualizamos el campo "asistido" a true del invitado
     *              1   el invitado existe y "asistido" esta a true
     *              2   no esta en la lista de invitados
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (aunque no haya contenido)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean validarInvitacionActualizaReg(String DNI, String id_evento, List<Integer> resultado) {
        String tag = "F_VALIDAR_INVIT";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectRef = db.collection("Eventos").document(id_evento)
                .collection("Invitados");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de la lista

        tarea = collectRef.whereEqualTo("DNI", DNI).get();//Solicitamos la información del evento

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
                    if(!(Boolean)doc.get("asistido")) {//No esta registrada la asistencia
                        DocumentReference docRef = db.collection("Eventos").document(id_evento)
                                .collection("Invitados").document(doc.getId());
                        docRef.update(
                                "asistido", true,
                                "hora_asistido", FieldValue.serverTimestamp()
                        );//Actualizamos el campo "asistido" a true del invitado y su hora de asistencia

                        resultado.add(0);//Esta invitado
                        Log.d(tag, "Hay invitado con ese DNI y se ha actualizado la asistencia");
                    } else {//Y a se ha registrado la asistencia
                        resultado.add(1);//Esta invitado
                        Log.d(tag, "Hay invitado con ese DNI pero ya se ha registrado la asistencia");
                    }
                } else {
                    resultado.add(2);
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

    /*Ejemplo de uso de la funcion iniciarSesion*/
    /*
    Boolean exitosa;
    List<Boolean> respuesta = new ArrayList<>();
    Funciones_FireBase f_FB = new Funciones_FireBase();
    exitosa = f_FB.iniciarSesion("Y7b82dz6CaxuljLr9ANE", "12345", respuesta);
    if(exitosa) {//Se ha establecido la consulta con exito
        if(respuesta.get(0)){
            Log.d("BOTON_LEER", "Existe el organizador con esa password");
        } else {
            Log.d("BOTON_LEER", "No existe el organizador con esa password");
        }
    } else {
        Log.d("BOTON_LEER", "Fallo en la lectura de los datos de iniciarSesion");
    }
    */

    /**
     * Comprueba que el organizador con ese id y password existe en la tabla de Organizadores, y por lo
     * tanto se encuentra registrado en el sistema.
     * Argumentos:
     *  id_organizador -> pasamos el identificador del organizador a comprobar
     *  password -> pasamos el password del organizador
     *  resultado -> guarda un List<Boolean> que contiene en su posicion 0, true o false
     *              true    hay organizador con ese DNI y password
     *              false   no hay organizador con ese DNI
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (aunque no haya contenido)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean iniciarSesion(String id_organizador, String password, List<Boolean> resultado) {
        String tag = "F_INICIAR_SESION";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Organizadores").document(id_organizador);

        Task<DocumentSnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de la lista

        //Consulta que busca ese documento con el id dado
        tarea = docRef.get();

        //Comprobamos cada 0.20 segundos si ha terminado la tarea (maximo 3 segundos)
        for(int i = 0; i < 15 && !tarea.isComplete(); i++){
            //Log.d(tag, "Intento " + i + "-esimo");
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
                DocumentSnapshot doc = tarea.getResult();
                if (doc.exists()) {//Hay documento con ese id
                    String pass = (String)doc.get("password");
                    if(password.equals(pass)) {//Coincide la constraseña
                        resultado.add(true);//Coincide el password
                        Log.d(tag, "Hay organizador con el id y password suministradas");
                    }else{
                        resultado.add(false);//No cincide el password
                        Log.d(tag, "No coincide la password suministrada");
                    }
                } else {
                    resultado.add(false);
                    Log.d(tag, "No hay organizadores con ese id");
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


    /*Ejemplo de uso de la funcion infoEventosOrganizador*/
    /*
    Boolean exitosa;
    List<DocumentSnapshot> resultado = new ArrayList<>();
    Funciones_FireBase f_FB = new Funciones_FireBase();
    exitosa = f_FB.infoEventosOrganizador("Y7b82dz6CaxuljLr9ANE", resultado);

    if(exitosa){
        if(resultado.size() > 0) {//Hay eventos
            Log.d("BOTON_LEER", "Hay eventos con ese organizador");
            for (DocumentSnapshot d : resultado) {
                Log.d("BOTON_LEER", "Datos de evento " + d.getId() + ": " + d.getData());
            }
         } else{
            Log.d("BOTON_LEER", "No hay eventos con ese organizador");
         }
     } else {
        Log.d("BOTON_LEER", "Fallo en la comunicacion con infoEventos");
     }
    */
    /**
     * Devuelve la información de los eventos que tiene acceso el organizador identificado
     * por el parametro id_organizador.
     * Argumentos:
     *  id_organizador -> el correo electronico del organizador
     *  resultado -> guarda los datos de los eventos como un List<DocumentSnapshot>, si esta
     *               vacio es que no hay eventos
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (incluso si no hay eventos)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean infoEventosOrganizador(String id_organizador, List<DocumentSnapshot> resultado) {
        String tag = "F_INF_EVEN_ORG";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collecRef = db.collection("Eventos");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de salida

        tarea = collecRef.whereEqualTo("organizador", id_organizador).get();//Solicitamos la información

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
                    Log.d(tag, "Hay eventos registrados a ese organizador");
                } else { //No hay eventos
                    Log.d(tag, "No hay eventos registrados a ese organizador");
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

    /*Ejemplo de uso de la funcion infoInvitados*/
    /*
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
    */
    /**
     * Devuelve la información de los invitados del evento identificado por id_evento.
     * Argumentos:
     *  id_evento -> pasamos el identificador del evento
     *  resultado -> guarda los datos de los invitados como un List<DocumentSnapshot>, si esta
     *               vacio es que no hay eventos
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (incluso si no hay invitados)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean infoInvitados(String id_evento, List<DocumentSnapshot> resultado) {
        String tag = "F_INF_INVITADOS";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectRef = db.collection("Eventos").document(id_evento)
                .collection("Invitados");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de salida

        tarea = collectRef.get();//Solicitamos la información de invitados

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

                if (query_docs.size() > 0) {//Hay invitados
                    resultado.addAll(query_docs.getDocuments());//Guardamos los invitados
                    Log.d(tag, "Hay invitados registrados a ese evento");
                } else { //No hay invitados
                    Log.d(tag, "No hay invitados registrados a ese organizador");
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

    public boolean infoInvitadosPrue(String id_evento, List<DocumentSnapshot> resultado) {
        return false;
    }

    /**
     * Añade un invitado al evento id_evento.
     * Argumentos:
     *  id_evento -> pasamos el identificador del evento
     *  ... campos de invitado
     *  res -> lista con id del invitado en primera posicion de la lista
     * Devuelve:
     *  true    la escritura de los datos a sido exitosa
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la escritura)
     * */
    public boolean agnadirInvitado(String id_evento, String DNI, String email, String genero, String nombre,
                                   List<String> res) {
        String tag = "F_AGNADIR_INVT";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        res.clear();//Limpiamos el contenido de salida

        Task<DocumentReference> tarea;

        Map<String, Object> invitado = new HashMap<>();
        invitado.put("nombre", nombre);
        invitado.put("DNI", DNI);
        invitado.put("genero", genero);
        invitado.put("email", email);
        invitado.put("asistido", false);

        CollectionReference collectRef = db.collection("Eventos").document(id_evento).collection("Invitados");

        tarea = collectRef.add(invitado);

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
                DocumentReference doc = tarea.getResult();
                res.add(doc.getId());
                Log.d(tag, "Invitado " + doc.getId() + " añadido");
                return true;//Se ha establecido la comunicacion

            } else {
                Log.d(tag, "Invitado no añadido");
            }
        } else {
            Log.d(tag, "Tarea no completada (a expìrado el timeout)");
        }

        return false;
    }

    /**
     * Añade un evento
     * Argumentos:
     *  ... campos de evento
     *  res -> lista con id del evento en primera posicion de la lista
     * Devuelve:
     *  true    la escritura de los datos a sido exitosa (se añade evento)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la escritura)
     * */
    public boolean agnadirEvento(String nombre, String fecha, String hora, String ubicacion, String organizador,
                                 List<String> res) {
        String tag = "F_AGNADIR_EVENT";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        res.clear();//Limpiamos el contenido de salida

        Task<DocumentReference> tarea;

        Map<String, Object> evento = new HashMap<>();
        evento.put("nombre", nombre);
        evento.put("hora", hora);
        evento.put("fecha", fecha);
        evento.put("ubicacion", ubicacion);
        evento.put("organizador", organizador);

        CollectionReference collectRef = db.collection("Eventos");

        tarea = collectRef.add(evento);

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
                DocumentReference doc = tarea.getResult();
                res.add(doc.getId());//Guardamos id
                Log.d(tag, "Evento " + doc.getId() + " añadido");
                return true;//Se ha establecido la comunicacion

            } else {
                Log.d(tag, "Evento no añadido");
            }
        } else {
            Log.d(tag, "Tarea no completada (a expìrado el timeout)");
        }

        return false;
    }

    /*
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
    */
    /**
     * Comprueba que el invitado con ese DNI se encuentre invitado y no está registrado con campo
     * "asistido" a true
     * Argumentos:
     *  DNI       -> dni del invitado a comprobar si ha asistido
     *  id_evento -> pasamos el identificador del evento donde comprobar si esta invitado
     *  resultado -> guarda un List<Boolean> que contiene en su posicion 0, true o false
     *              0   esta en la lista con "asistido" a false
     *              1   el invitado existe y "asistido" esta a true
     *              2   no esta en la lista de invitados
     * Devuelve:
     *  true    la lectura de los datos a sido exitosa (aunque no haya contenido)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la lectura)
     * */
    public boolean validarInvitacionAsistido(String DNI, String id_evento, List<Integer> resultado) {
        String tag = "F_VALIDAR_INV_ASIS";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference collectRef = db.collection("Eventos").document(id_evento)
                .collection("Invitados");

        Task<QuerySnapshot> tarea;

        resultado.clear();//Limpiamos el contenido de la lista

        tarea = collectRef.whereEqualTo("DNI", DNI).get();

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

                    if(!(Boolean)doc.get("asistido")) {//No esta registrada la asistencia
                        resultado.add(0);//Esta invitado y no registrado
                        Log.d(tag, "Hay invitado con ese DNI y se ha actualizado la asistencia");
                    } else {//Y a se ha registrado la asistencia
                        resultado.add(1);//Esta invitado y registrado
                        Log.d(tag, "Hay invitado con ese DNI pero ya se ha registrado la asistencia");
                    }

                } else {
                    resultado.add(2);
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

    /*
        Boolean exitosa;
        Funciones_FireBase f_FB = new Funciones_FireBase();
        exitosa = f_FB.DesvalidarInvitacionAsistido("hjT9a4zCqU3DoOkGvmVU", "V61HJQ1ylMzbJddvX0lg" );

        if(exitosa){
            Log.d("BOTON_LEER", "Tarea completada, asistido a false y borrada la hora de asistencia");
        } else {
            Log.d("BOTON_LEER", "Fallo en la comunicacion con DesvalidarInvitacionAsistido");
        }
    */
    /**
     * Pone el campo asistido a false, por lo que ya no estará registrada la asistencia y borramos
     * el campo hora_asistido
     * Argumentos:
     *  id_invitado -> pasamos el identificador del invitado
     *  id_evento   -> pasamos el identificador del evento donde comprobar si esta invitado
     * Devuelve:
     *  true    la desvalidacion ha sido exitosa(asistido a false y borrar hora_asistido)
     *  false   en caso de que salga algo mal(ha expirado timeout, no ha sido exitosa la actualizacion)
     * */
    public boolean DesvalidarInvitacionAsistido(String id_invitado, String id_evento) {
        String tag = "F_DESVALIDAR_INV_ASIS";

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        DocumentReference docRef = db.collection("Eventos").document(id_evento)
                .collection("Invitados").document(id_invitado);

        Task<Void> tarea;

        tarea = docRef.update(
                "asistido", false,
                "hora_asistido", FieldValue.delete()
        );//Actualizamos el campo "asistido" a false del invitado y borramos hora de aistencia



        //Comprobamos cada 0.20 segundos si ha terminado la tarea (maximo 3 segundos)
        for(int i = 0; i < 15 && !tarea.isComplete(); i++){
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
                Log.d(tag, "Tarea completada, asistido a false y borrada la hora de asistencia");

                return true;//Se ha establecido la comunicacion
            } else {
                Log.d(tag, "Tarea completada, no existe ese invitado o evento");
            }
        } else {
            Log.d(tag, "Tarea no completada (a expìrado el timeout)");
        }
        return false;
    }

}