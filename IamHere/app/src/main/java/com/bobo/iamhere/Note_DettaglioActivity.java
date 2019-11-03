package com.bobo.iamhere;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.EditText;

import com.bobo.iamhere.db.DatabaseManager;
import com.bobo.iamhere.db.NotaDao;

public class Note_DettaglioActivity extends AppCompatActivity {

    EditText testoNotaView;
    EditText titoloNotaView;

    NotaDao notaOriginale;
    long idNotaOriginale;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note__dettaglio);

        //Cambio il titolo all'activity
        setTitle(getString(R.string.title_activity_note));

        //Inizializzo la view
        testoNotaView = findViewById(R.id.editNotaTesto);
        titoloNotaView = findViewById(R.id.editNotaTitolo);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Recupero l'eventuale testo della nota
        Intent intent = getIntent();
        idNotaOriginale = intent.getLongExtra("idNota", -1); //Se -1, è una nota nuova

        if(idNotaOriginale >= 0)
        {
            notaOriginale = DatabaseManager.getNota(MainActivity.database, idNotaOriginale);

            testoNotaView.setText(notaOriginale.getTestoNota());
            titoloNotaView.setText(notaOriginale.getTitolo());
        }
    }

    @Override
    public void onBackPressed(){

        String testoNota = testoNotaView.getText().toString();
        String titoloNota = titoloNotaView.getText().toString();

        NotaDao notaDaSalvare = new NotaDao(titoloNota, testoNota);

        if(testoNota != null && testoNota.trim().length() > 0)
        {
            if(idNotaOriginale == -1)
                DatabaseManager.insertNota(MainActivity.database, notaDaSalvare);
            else if(!notaDaSalvare.equals(notaOriginale))
                DatabaseManager.updateNota(MainActivity.database, notaDaSalvare);
        }

        super.onBackPressed();
    }
}
