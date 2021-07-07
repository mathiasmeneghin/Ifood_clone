package com.mathias.ifood.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mathias.ifood.R;
import com.mathias.ifood.adapter.AdapterEmpresa;
import com.mathias.ifood.adapter.AdapterProduto;
import com.mathias.ifood.helper.ConfiguracaoFirebase;
import com.mathias.ifood.model.Empresa;
import com.mathias.ifood.model.Produto;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializarComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        autenticacao = ConfiguracaoFirebase.getFirebaseAutenticacao();

        //configuracoes toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

        //Configura RecyclerView
       recyclerEmpresa.setLayoutManager(new LinearLayoutManager(this));
       recyclerEmpresa.setHasFixedSize(true);
       adapterEmpresa = new AdapterEmpresa(empresas);
       recyclerEmpresa.setAdapter(adapterEmpresa);

        //recupera empresas
        recuperaEmpresas();

        //configuracao do Searchview
        searchView.setHint("Pesquisar restaurantes");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return false;
            }
        });
    }

    private void pesquisarEmpresas(String pesquisa) {
        DatabaseReference empresasRef = firebaseRef
                .child("empresas");
        Query query = empresasRef.orderByChild("nome")
                .startAt(pesquisa)
                .endAt(pesquisa + "\uf88ff");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void recuperaEmpresas() {
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear();
                for(DataSnapshot ds: snapshot.getChildren()) {
                    empresas.add(ds.getValue(Empresa.class));
                }
                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario,menu);
        //Configurar botao de pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);

        return super.onCreateOptionsMenu(menu);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuSair:
                deslogarUsuario();
                finish();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    private void inicializarComponentes() {
        searchView = findViewById(R.id.materialSearchView);
        recyclerEmpresa = findViewById(R.id.recyclerEmpresa);
    }

    private void deslogarUsuario() {
        try{
            autenticacao.signOut();
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(HomeActivity.this,ConfiguracoesUsuarioActivity.class));
    }


}