package com.example.contatos;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.contatos.Model.Contato;
import com.example.contatos.databinding.ViewContatoBinding;

import java.util.ArrayList;

public class ContatosAdapter extends ArrayAdapter<Contato> {
    ContatoViewHolder contatoViewHolder;

    public ContatosAdapter(Context context, int layout, ArrayList<Contato> contatos){
        super(context, layout, contatos);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewContatoBinding viewContatoBinding;

        if (convertView ==null){
            viewContatoBinding = ViewContatoBinding.inflate(LayoutInflater.from(getContext()));

            convertView = viewContatoBinding.getRoot();

            contatoViewHolder = new ContatoViewHolder();
            contatoViewHolder.nomeContatoTextView = convertView.findViewById(R.id.nomeContatoTextView);
            contatoViewHolder.emailContatoTextView = convertView.findViewById(R.id.emailContatoTextView);

            convertView.setTag(contatoViewHolder);
        }
        contatoViewHolder = (ContatoViewHolder)convertView.getTag();

        Contato contato = getItem(position);
        contatoViewHolder.nomeContatoTextView.setText(contato.getNome());
        contatoViewHolder.emailContatoTextView.setText(contato.getEmail());

        return convertView;
    }

    private class ContatoViewHolder{
        public TextView nomeContatoTextView;
        public TextView emailContatoTextView;
    }
}
