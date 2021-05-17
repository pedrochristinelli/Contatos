package com.example.contatos;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.contatos.Model.Contato;
import com.example.contatos.databinding.ActivityContatoBinding;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;

public class ContatoActivity extends AppCompatActivity {
    private ActivityContatoBinding activityContatoBinding;
    private Contato contato;
    private int posicao = -1;

    private final int PERMISSAO_ESCRITA_ARMAZENAMENTO_EXTERNO_REQUEST_CODE = 0;

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

        contato = (Contato) getIntent().getSerializableExtra(Intent.EXTRA_USER);
        if (contato != null){
            posicao = getIntent().getIntExtra(Intent.EXTRA_INDEX, -1);

            if (posicao==-1){
                getSupportActionBar().setSubtitle("Detalhes de contato");
                alterarAtivacaoViews(false);
                activityContatoBinding.buttonSave.setEnabled(true);
                activityContatoBinding.buttonPdf.setEnabled(false);
                alterarAtivacaoButtons(false);
            } else {
                getSupportActionBar().setSubtitle("Edição de contato");
                alterarAtivacaoViews(true);
                alterarAtivacaoButtons(true);
            }

            activityContatoBinding.editName.setText(contato.getNome());
            activityContatoBinding.editEmail.setText(contato.getEmail());
            activityContatoBinding.editTelefone.setText(contato.getTelefone());
            activityContatoBinding.editCelular.setText(contato.getCelular());
            activityContatoBinding.switch1.setChecked(contato.getTefoneComercial());
            activityContatoBinding.editSite.setText(contato.getSitePessoal());
        } else{
            getSupportActionBar().setSubtitle("Novo contato");
        }
    }

    private void alterarAtivacaoButtons(Boolean isView){
        activityContatoBinding.buttonSave.setEnabled(isView);
        activityContatoBinding.buttonPdf.setEnabled(!isView);
    }

    private void alterarAtivacaoViews(Boolean ativado){
        activityContatoBinding.editName.setEnabled(ativado);
        activityContatoBinding.editEmail.setEnabled(ativado);
        activityContatoBinding.editTelefone.setEnabled(ativado);
        activityContatoBinding.editCelular.setEnabled(ativado);
        activityContatoBinding.switch1.setEnabled(ativado);
        activityContatoBinding.editSite.setEnabled(ativado);
    }

    public void onClickPdfButton(View view) {
        verificaPermissaoEscritaArmazenamentoExterno();
    }

    public void onClickSaveButton(View view) {
        Contato contato = getContatoAtual();
        Intent retornoIntent = new Intent();
        retornoIntent.putExtra(Intent.EXTRA_USER, contato);
        retornoIntent.putExtra(Intent.EXTRA_INDEX, posicao);
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

    private void verificaPermissaoEscritaArmazenamentoExterno(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (PackageManager.PERMISSION_GRANTED == checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                gerarDocumentoPdf();
            } else {
                Toast.makeText(this, "Sem permissão para isso.", Toast.LENGTH_SHORT).show();
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSAO_ESCRITA_ARMAZENAMENTO_EXTERNO_REQUEST_CODE);
            }
        } else {
            gerarDocumentoPdf();
        }
    }

    private void gerarDocumentoPdf(){
        View conteudo = activityContatoBinding.getRoot();
        View conteudoNome = activityContatoBinding.editName;

        int largura = conteudo.getWidth();
        int altura = conteudo.getHeight();

        PdfDocument documentoPdf = new PdfDocument();
        PdfDocument.PageInfo configuracaoPagina = new PdfDocument.PageInfo.Builder(largura, altura, 1).create();
        PdfDocument.Page page = documentoPdf.startPage(configuracaoPagina);

        Paint paint = new Paint();
        paint.setColor(Color.BLACK);

        page.getCanvas().drawText(contato.getNome(),10, 50, paint);
        page.getCanvas().drawText(contato.getEmail(),10, 100, paint);
        page.getCanvas().drawText(contato.getTelefone(),((float) conteudo.getWidth())/2, 100, paint);

        documentoPdf.finishPage(page);

        File diretorio = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath());
        try {
            File documento = new File(diretorio, contato.getNome().replace(" ", "_")+".pdf");
            documento.createNewFile();
            documentoPdf.writeTo(new FileOutputStream(documento));
            documentoPdf.close();
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == PERMISSAO_ESCRITA_ARMAZENAMENTO_EXTERNO_REQUEST_CODE){
            verificaPermissaoEscritaArmazenamentoExterno();
        }
    }
}