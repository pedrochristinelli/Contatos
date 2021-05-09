package com.example.contatos;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.contatos.Model.Contato;
import com.example.contatos.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding activityMainBinding;

    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(activityMainBinding.getRoot());


        CheckBox celChkBx = ( CheckBox ) activityMainBinding.checkBoxCelular;
        celChkBx.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){
                if ( isChecked ){
                    activityMainBinding.editCelular.setVisibility(View.VISIBLE);
                } else {
                    activityMainBinding.editCelular.setVisibility(View.GONE);
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

            startActivity(emailIntent);
        } else {
            Toast.makeText(this, "O campo que contêm essa informação está vazio!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSaveButton(View view) {
        Contato contato = getContatoAtual();

        Toast.makeText(this, contato.toString(), Toast.LENGTH_SHORT).show();
    }

    private Contato getContatoAtual(){
        Contato contato = new Contato();
        contato.setNome(activityMainBinding.editName.getText().toString() != null? activityMainBinding.editName.getText().toString() : "");
        contato.setEmail(activityMainBinding.editEmail.getText().toString() != null? activityMainBinding.editEmail.getText().toString() : "");
        contato.setCelular(activityMainBinding.editCelular.getText().toString() != null? activityMainBinding.editCelular.getText().toString() : "");
        contato.setSitePessoal(activityMainBinding.editSite.getText().toString() != null? activityMainBinding.editSite.getText().toString() : "");
        contato.setTelefone(activityMainBinding.editTelefone.getText().toString() != null? activityMainBinding.editTelefone.getText().toString() : "");
        contato.setTefoneComercial(activityMainBinding.switch1.isChecked());

        if (!(contato.getSitePessoal().contains("http://") || contato.getSitePessoal().contains("https://"))){
            contato.setSitePessoal("https://" + contato.getSitePessoal());
        }

        return contato;
    }
}