package com.example.carteiradeclientes;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import com.example.carteiradeclientes.database.DadosOpenHelper;
import com.example.carteiradeclientes.dominio.entidades.Cliente;
import com.example.carteiradeclientes.dominio.repositorio.ClienteRepositorio;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.text.TextUtils;
import android.util.Patterns;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.w3c.dom.Text;

public class ActCadCliente extends AppCompatActivity {

    private FloatingActionButton fab;
    private EditText edtNome;
    private EditText edtEndereco;
    private EditText edtEmail;
    private EditText edtTelefone;

    private ClienteRepositorio clienteRepositorio;

    private DadosOpenHelper dadosOpenHelper;

    private SQLiteDatabase conexao;

    private ConstraintLayout layoutContentActCadCliente;

    private Cliente cliente;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_cliente);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        edtNome     = (EditText)findViewById(R.id.edtNome);
        edtEndereco = (EditText)findViewById(R.id.edtEndereco);
        edtEmail    = (EditText)findViewById(R.id.edtEmail);
        edtTelefone = (EditText)findViewById(R.id.edtTelefone);

        layoutContentActCadCliente = (ConstraintLayout)findViewById(R.id.layoutContentActCadCliente);

        criarConexao();
        verificaParametro();

        }

        private void verificaParametro(){

        Bundle bundle = getIntent().getExtras();

        cliente = new Cliente();

        if((bundle != null) && (bundle.containsKey("CLIENTE"))){

            cliente = (Cliente)bundle.getSerializable("CLIENTE");
            edtNome.setText(cliente.nome);
            edtTelefone.setText(cliente.telefone);
            edtEmail.setText(cliente.email);
            edtEndereco.setText(cliente.endereco);

        }

        }

    private void criarConexao(){

        try{

            dadosOpenHelper = new DadosOpenHelper(this);

            conexao = dadosOpenHelper.getWritableDatabase();

            Snackbar.make(layoutContentActCadCliente, getString(R.string.message_conexao_criada_sucesso)
                    ,Snackbar.LENGTH_SHORT).setAction(R.string.action_ok, null).show();

            clienteRepositorio = new ClienteRepositorio(conexao);

        }catch(SQLException ex){

            AlertDialog.Builder dlg = new AlertDialog.Builder(this);
            dlg.setTitle(getString(R.string.title_erro_banco));
            dlg.setMessage(ex.getMessage());
            dlg.setNeutralButton(getString(R.string.action_ok), null);
            dlg.show();

        }

    }

    private void confirmar() {

        if (validaCampos() == false) {

            try {

                if(cliente.codigo == 0) {

                    clienteRepositorio.inserir(cliente);

                }else{

                    clienteRepositorio.alterar(cliente);

                }

                finish();

            } catch (SQLException ex) {

                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle(getString(R.string.title_erro_banco));
                dlg.setMessage(ex.getMessage());
                dlg.setNeutralButton(getString(R.string.action_ok), null);
                dlg.show();

            }

        }
    }

    private boolean validaCampos(){

        boolean res = false;

        String nome     = edtNome.getText().toString();
        String endereco = edtEndereco.getText().toString();
        String email    = edtEmail.getText().toString();
        String telefone = edtTelefone.getText().toString();

        cliente.nome = nome;
        cliente.endereco = endereco;
        cliente.email = email;
        cliente.telefone = telefone;

        if (res = isCampoVazio(nome)){
            edtNome.requestFocus();
        }else{
            if(res = isCampoVazio(endereco)){
                edtEndereco.requestFocus();
            }else{
                if(res = !isEmailValido(email)){
                    edtEmail.requestFocus();
                }else{
                    if(res = isCampoVazio(telefone)){
                        edtTelefone.requestFocus();
                    }
                }
            }

            if(res){

                AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                dlg.setTitle("Aviso");
                dlg.setMessage("Existem campos inválidos ou em branco");
                dlg.setNeutralButton("OK", null);
                dlg.show();

            } else{
                if (!res){
                    AlertDialog.Builder dlg = new AlertDialog.Builder(this);
                    dlg.setMessage("Cliente cadastrado!");
                    dlg.show();
                }
            }

        }

        return res;

    }

    private boolean isCampoVazio(String valor){

        boolean resultado = (TextUtils.isEmpty(valor) || valor.trim().isEmpty());
        return resultado;

    }

    private boolean isEmailValido(String email){

        boolean resultado = (!isCampoVazio(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches());
        return resultado;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_cad_cliente, menu);

        return super.onCreateOptionsMenu(menu);

        }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int id = item.getItemId();

        switch (id){

            case android.R.id.home:
                finish();
                break;

            case R.id.action_ok:

                confirmar();
                //Toast.makeText(this, "Botão OK Selecionado", Toast.LENGTH_SHORT).show();

                break;

            case R.id.action_excluir:

                clienteRepositorio.excluir(cliente.codigo);
                AlertDialog.Builder dlg1 = new AlertDialog.Builder(this);
                dlg1.setMessage("Cliente exluido!");
                dlg1.setNeutralButton("OK", null);
                dlg1.show();

                break;

        }

        return super.onOptionsItemSelected(item);

    }
}

