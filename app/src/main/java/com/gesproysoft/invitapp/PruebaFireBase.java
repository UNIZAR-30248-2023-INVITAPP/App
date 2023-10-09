package com.gesproysoft.invitapp;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
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
            public void onClick(View view){
                agnadirDocumento();
            }
        });

        Button myButton2 = (Button) findViewById(R.id.your_id2);
        myButton2.setOnClickListener(new View.OnClickListener(){
            // When the button is pressed/clicked, it will run the code below
            @Override
            public void onClick(View view){
                leerDocumento();
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

}
