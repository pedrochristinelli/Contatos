package com.example.contatos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.contatos.Model.Contato;
import com.example.contatos.databinding.ActivityContatoBinding;

import java.io.Serializable;

public class ContatoActivity extends AppCompatActivity {
    private ActivityContatoBinding activityContatoBinding;

    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContatoBinding = ActivityContatoBinding.inflate(getLayoutInflater());
        setContentView(activityContatoBinding.getRoot());


        CheckBox celChkBx = ( CheckBox ) activityContatoBinding.checkBoxCelular;
        celChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    activityContatoBinding.editCelular.setVisibility(View.VISIBLE);
                } else {
                    activityContatoBinding.editCelular.setVisibility(View.GONE);
                }
            }
        });
    }

    public void onClickPdfButton(View view) {
        Contato contato = getContatoAtual();
    }

    public void onClicWebsiteButton(View view) {
        Contato contato = getContatoAtual();

        if (!contato.getSitePessoal().equals("")) {
            Intent abrirNavegadorIntent = new Intent(Intent.ACTION_VIEW);
            abrirNavegadorIntent.setData(Uri.parse(contato.getSitePessoal()));
            startActivity(abrirNavegadorIntent);
        } else {
            Toast.makeText(this, "O campo que contêm essa informação está vazio!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickTelefoneButton(View view) {
        Contato contato = getContatoAtual();

        if (!contato.getTelefone().equals("")) {
            Intent ligarIntent = new Intent(Intent.ACTION_CALL);
            ligarIntent.setData(Uri.parse("tel:" + contato.getTelefone()));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.CALL_PHONE)) {
                    startActivity(ligarIntent);
                } else {
                    Toast.makeText(this, "Sem permissão para efetuar ligações", Toast.LENGTH_SHORT).show();
                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, CALL_PHONE_PERMISSION_REQUEST_CODE);
                }
            } else {
                startActivity(ligarIntent);
            }
        } else {
            Toast.makeText(this, "O campo que contêm essa informação está vazio!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickEmailButton(View view) {
        Contato contato = getContatoAtual();

        if (!contato.getEmail().equals("")) {
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", contato.getEmail(), null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello "+contato.getNome()+"!");
            emailIntent.putExtra(Intent.EXTRA_TEXT, contato.toString());

            startActivity(emailIntent);
        } else {
            Toast.makeText(this, "O campo que contêm essa informação está vazio!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSaveButton(View view) {
        Contato contato = getContatoAtual();
        Intent retornoIntent = new Intent();
        retornoIntent.putExtra(Intent.EXTRA_USER, contato);
        setResult(RESULT_OK, retornoIntent);
        finish();
    }

    private Contato getContatoAtual(){
        Contato contato = new Contato();
        contato.setNome(activityContatoBinding.editName.getText().toString() != null? activityContatoBinding.editName.getText().toString() : "");
        contato.setEmail(activityContatoBinding.editEmail.getText().toString() != null? activityContatoBinding.editEmail.getText().toString() : "");
        contato.setCelular(activityContatoBinding.editCelular.getText().toString() != null? activityContatoBinding.editCelular.getText().toString() : "");
        contato.setSitePessoal(activityContatoBinding.editSite.getText().toString() != null? activityContatoBinding.editSite.getText().toString() : "");
        contato.setTelefone(activityContatoBinding.editTelefone.getText().toString() != null? activityContatoBinding.editTelefone.getText().toString() : "");
        contato.setTefoneComercial(activityContatoBinding.switch1.isChecked());

        if (!(contato.getSitePessoal().contains("http://") || contato.getSitePessoal().contains("https://"))){
            contato.setSitePessoal("https://" + contato.getSitePessoal());
        }

        return contato;
    }
}