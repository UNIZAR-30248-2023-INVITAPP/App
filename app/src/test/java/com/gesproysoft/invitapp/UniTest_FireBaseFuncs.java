package com.gesproysoft.invitapp;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class UniTest_FireBaseFuncs {
    @Test
    public void funciones_firebase_app() {
        List<DocumentSnapshot> resultado = new ArrayList<>();
        List<Integer> res = new ArrayList<>();
        Funciones_FireBase f_FB = new Funciones_FireBase();

        assertEquals(false, f_FB.infoInvitados("YybJNIeSZMVaXWK9Yoqo", resultado));//35 líneas

        assertEquals(false, f_FB.validarInvitacionActualizaReg("12345678Z","YybJNIeSZMVaXWK9Yoqo", "23", res));//46 líneas

        assertEquals(false, f_FB.infoEventosOrganizador("YybJNIeSZMVaXWK9Yoqo", resultado));//34 líneas

        assertEquals(false, f_FB.validarInvitacionAsistido("12345678Z","YybJNIeSZMVaXWK9Yoqo", res));//42 líneas

        assertEquals(false, f_FB.DesvalidarInvitacionAsistido("YybJNIeSZMVaXWK9Yoqo","YybJNIeSZMVaXWK9Yoqo"));//27 líneas

    }
}