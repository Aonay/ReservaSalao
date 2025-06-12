package br.edu.fatecpg.reservassalao.view.cliente

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.GridLayoutManager
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityClienteHomeBinding
import br.edu.fatecpg.reservassalao.model.Salao
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClienteHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityClienteHomeBinding
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    private val saloesList = mutableListOf<Salao>()
    private lateinit var adapter: SalaoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityClienteHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setNavigationItemSelectedListener(this)

        auth = Firebase.auth

        if (auth.currentUser == null) {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.btnMenu.setOnClickListener {
            Toast.makeText(this, "Menu clicado", Toast.LENGTH_SHORT).show()
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // RecyclerView
        adapter = SalaoAdapter(saloesList) { salao ->
            val intent = Intent(this, AgendarActivity::class.java)
            intent.putExtra("salao", salao)
            startActivity(intent)
            Toast.makeText(this, "Selecionou salão: ${salao.nome}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerSaloes.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerSaloes.adapter = adapter

        carregarNomeCliente()
        carregarSaloes()


        // Buscar salões
        binding.edtBuscarSalao.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val termo = binding.edtBuscarSalao.text.toString().trim()
                if (termo.isNotEmpty()) {
                    buscarSaloes(termo)
                } else {
                    carregarSaloes()
                }
                true
            } else {
                false
            }
        }
    }

    private fun carregarNomeCliente() {
        val idCliente = auth.currentUser?.uid ?: return

        db.collection("clientes").document(idCliente)
            .get()
            .addOnSuccessListener {
                val nome = it.getString("nome") ?: "Cliente"
                binding.txtOlaCliente.text = "Olá, $nome!"

                // Também exibe no cabeçalho do menu lateral, se quiser
                val headerView = binding.navigationView.getHeaderView(0)
                val txtNomeHeader = headerView.findViewById<android.widget.TextView>(R.id.txtNomeHeader)
                txtNomeHeader.text = nome
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar nome do cliente", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarSaloes() {
        db.collection("saloes").get()
            .addOnSuccessListener { result ->
                saloesList.clear()
                for (document in result) {
                    val salao = document.toObject(Salao::class.java).copy(id = document.id)
                    saloesList.add(salao)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar salões", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarSaloes(termo: String) {
        db.collection("saloes")
            .orderBy("nome")
            .startAt(termo)
            .endAt(termo + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                saloesList.clear()
                for (document in result) {
                    val salao = document.toObject(Salao::class.java).copy(id = document.id)
                    saloesList.add(salao)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro na busca", Toast.LENGTH_SHORT).show()
            }
    }

        override fun onNavigationItemSelected(item: MenuItem): Boolean {
            when (item.itemId) {
                R.id.menu_home_salao -> {
                    startActivity(Intent(this, ClienteHomeActivity::class.java))
                }
                R.id.menu_agendamentos_salao -> {
                    startActivity(Intent(this, AgendamentosClienteActivity::class.java))
                }
                R.id.menu_sair -> {
                    auth.signOut()
                    Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show()
                    finish()
                }
            }
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }

}
