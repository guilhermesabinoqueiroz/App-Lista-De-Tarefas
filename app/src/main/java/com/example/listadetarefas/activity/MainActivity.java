package com.example.listadetarefas.activity;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import com.example.listadetarefas.R;
import com.example.listadetarefas.adapter.AdapterListaTarefas;
import com.example.listadetarefas.helper.DbHelper;
import com.example.listadetarefas.helper.RecyclerItemClickListener;
import com.example.listadetarefas.helper.TarefaDAO;
import com.example.listadetarefas.model.ListaTarefas;


import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.listadetarefas.databinding.ActivityMainBinding;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    private AppBarConfiguration appBarConfiguration;
    private RecyclerView recyclerView;
    private ListaTarefas tarefaSelecionada;
    private List<ListaTarefas>  listaTarefas = new ArrayList<>();
    private AdapterListaTarefas adapterListaTarefas;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        try
        {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            Toolbar toolbar = findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);

            //Configurar recycler
            recyclerView = findViewById(R.id.recyclerView);

            //Adicionar evento de clique
            recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(
                            getApplicationContext(),
                            recyclerView,
                            new RecyclerItemClickListener.OnItemClickListener()
                            {
                                @Override
                                public void onItemClick(View view, int position)
                                {
                                    //Recuperar tarefa para edicao
                                    ListaTarefas tarefaSelecionada = listaTarefas.get(position);

                                    //Enviar tarefa para tela adicionar tarefa
                                    Intent intent = new Intent(MainActivity.this, AdicionarTarefaActivity.class);
                                    intent.putExtra("tarefaSelecionada", tarefaSelecionada);
                                    startActivity(intent);
                                }

                                @Override
                                public void onLongItemClick(View view, int position)
                                {
                                    // Recuperar tarefa para deletar
                                    tarefaSelecionada = listaTarefas.get(position);

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                                    //Configurar mensagem
                                    dialog.setTitle("Confimar exclusão");
                                    dialog.setMessage("Deseja excluir a tarefa: " + tarefaSelecionada.getNomeTarefa() + "?");

                                    dialog.setPositiveButton("Sim", new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
                                            if (tarefaDAO.deletar(tarefaSelecionada))
                                            {
                                                carregarListaTarefas();
                                                Toast.makeText(getApplicationContext(), "Sucesso ao excluir tarefa!", Toast.LENGTH_SHORT).show();
                                            }
                                            else
                                            {
                                                Toast.makeText(getApplicationContext(), "Erro ao excluir tarefa!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("Não",null);

                                    dialog.create();
                                    dialog.show();
                                }

                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                                {

                                }
                            }
                    )
            );
            FloatingActionButton fab = findViewById(R.id.fabAdicionar);
            fab.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    Intent intent = new Intent(getApplicationContext(),AdicionarTarefaActivity.class);
                    startActivity(intent);
                }
            });
        }
        catch (Exception e)
        {
            Log.i("INFO", "ERRO");
        }
    }

    public void carregarListaTarefas()
    {
        //Listar tarefas
        TarefaDAO tarefaDAO = new TarefaDAO(getApplicationContext());
        listaTarefas = tarefaDAO.listar();

        /* Exibi lista de tarefas no RecyclerView*/

        //Configurar um adapter
        adapterListaTarefas = new AdapterListaTarefas(listaTarefas);


        //Configurar RecyclerView
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), LinearLayout.VERTICAL));
        recyclerView.setAdapter(adapterListaTarefas);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        carregarListaTarefas();
    }
}