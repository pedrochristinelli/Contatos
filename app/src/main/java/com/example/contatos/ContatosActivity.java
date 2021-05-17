package com.example.contatos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;
import com.example.contatos.Model.Contato;
import com.example.contatos.databinding.ActivityContatosBinding;
import java.util.ArrayList;

public class ContatosActivity extends AppCompatActivity {
    private ActivityContatosBinding activityContatosBinding;
    private ArrayList<Contato> contatosList;
    private ContatosAdapter contatosAdapter;
    Contato contato = new Contato();

    private final int NOVO_CONTATO_REQUEST_CODE = 0;
    private final int EDITAR_CONTATO_REQUEST_CODE = 1;
    private final int CALL_PHONE_PERMISSION_REQUEST_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityContatosBinding = ActivityContatosBinding.inflate(getLayoutInflater());
        setContentView(activityContatosBinding.getRoot());

        contatosList = new ArrayList<>();

        contatosAdapter = new ContatosAdapter(this, android.R.layout.simple_list_item_1, contatosList);

        activityContatosBinding.contatosListView.setAdapter(contatosAdapter);

        registerForContextMenu(activityContatosBinding.contatosListView);

        activityContatosBinding.contatosListView.setOnItemClickListener((parent, view, position, id) -> {
            contato = contatosList.get(position);
            Intent detalhesIntent = new Intent(this, ContatoActivity.class);
            detalhesIntent.putExtra(Intent.EXTRA_USER, contato);
            startActivity(detalhesIntent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        unregisterForContextMenu(activityContatosBinding.contatosListView);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_contatos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.novoContatoMenuItem){
            Intent novoContatoIntent = new Intent(this, ContatoActivity.class);
            startActivityForResult(novoContatoIntent, NOVO_CONTATO_REQUEST_CODE);

            return true;
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == NOVO_CONTATO_REQUEST_CODE && resultCode == RESULT_OK){
            Contato contato = (Contato) data.getSerializableExtra(Intent.EXTRA_USER);
            if (contato != null){
                contatosList.add(contato);
                contatosAdapter.notifyDataSetChanged();
            }
        } else {
            if (requestCode == EDITAR_CONTATO_REQUEST_CODE && resultCode == RESULT_OK){
                Contato contato = (Contato) data.getSerializableExtra(Intent.EXTRA_USER);
                int posicao = data.getIntExtra(Intent.EXTRA_INDEX, -1);
                if (contato != null && posicao != -1){
                    contatosList.remove(posicao);
                    contatosList.add(posicao, contato);
                    contatosAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.context_menu_contato, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        AdapterView.AdapterContextMenuInfo menuInfo = ((AdapterView.AdapterContextMenuInfo) item.getMenuInfo());
        Contato contato = contatosList.get(menuInfo.position);

        switch (item.getItemId()) {
            case R.id.enviarEmailMenuItem:
                if (!contato.getEmail().equals("")) {
                    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                            "mailto", contato.getEmail(), null));
                    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Hello "+contato.getNome()+"!");
                    emailIntent.putExtra(Intent.EXTRA_TEXT, contato.toString());

                    startActivity(emailIntent);
                } else {
                    Toast.makeText(this, "O campo que contêm essa informação está vazio!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.ligarMenuItem:
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
                return true;
            case R.id.acessarSiteMenuItem:
                if (!contato.getSitePessoal().equals("")) {
                    Intent abrirNavegadorIntent = new Intent(Intent.ACTION_VIEW);
                    abrirNavegadorIntent.setData(Uri.parse(contato.getSitePessoal()));
                    startActivity(abrirNavegadorIntent);
                } else {
                    Toast.makeText(this, "O campo que contêm essa informação está vazio!", Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.editarContatoMenuItem:
                Intent editarContatoIntent = new Intent(this, ContatoActivity.class);
                editarContatoIntent.putExtra(Intent.EXTRA_USER, contato);
                editarContatoIntent.putExtra(Intent.EXTRA_INDEX, menuInfo.position);
                startActivityForResult(editarContatoIntent, EDITAR_CONTATO_REQUEST_CODE);
                return true;
            case R.id.removerContatoMenuItem:
                excluirContato(menuInfo.position);
                return true;
            default:
                return false;
        }
    }

    private void excluirContato(int position){
        contatosList.remove(position);
        contatosAdapter.notifyDataSetChanged();
    }
}