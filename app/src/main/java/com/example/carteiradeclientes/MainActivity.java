package com.example.carteiradeclientes;

import android.content.Intent;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.carteiradeclientes.database.DadosOpenHelper;
import com.example.carteiradeclientes.dominio.entidades.Cliente;
import com.example.carteiradeclientes.dominio.repositorio.ClienteRepositorio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RecyclerView listaDados;
    private FloatingActionButton fab;
    private ConstraintLayout layoutContentMain;

    private SQLiteDatabase conexao;

    private DadosOpenHelper dadosOpenHelper;

    private ClienteRepositorio clienteRepositorio;

    private ClienteAdapter clienteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = findViewById(R.id.fab);
        listaDados = (RecyclerView)findViewById(R.id.listaDados);

        layoutContentMain = (ConstraintLayout)findViewById(R.id.layoutContentMain);

        criarConexao();

        listaDados.setHasFixedSize(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listaDados.setLayoutManager(linearLayoutManager);

        clienteRepositorio = new ClienteRepositorio(conexao);

        List<Cliente> dados = clienteRepositorio.buscarTodos();

        clienteAdapter = new ClienteAdapter(dados);

        listaDados.setAdapter(clienteAdapter);

    }

    private void criarConexao(){

        try{

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            Snackbar.make(layoutContentMain, getString(R.string.message_conexao_criada_sucesso)
                    ,Snackbar.LENGTH_SHORT).setAction(R.string.action_ok, null).show();

        }catch(SQLException ex){

            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(getString(R.string.title_erro_banco));
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton(getString(R.string.action_ok), null);
            dlg.show();

        }

    }

    public void cadastrar (View view){

        Intent it = new Intent(MainActivity.this, ActCadCliente.class);
        startActivityForResult(it, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(requestCode == 0) {

            List<Cliente> dados = clienteRepositorio.buscarTodos();
            clienteAdapter = new ClienteAdapter(dados);
            listaDados.setAdapter(clienteAdapter);
        }

    }
}
