package br.edu.fatecpg.reservassalao.view.cliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
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


        binding.edtBuscarSalao.addTextChangedListener(object : android.text.TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val termo = s.toString().trim()
                if (termo.isEmpty()) {
                    carregarSaloes()
                } else {
                    buscarSaloes(termo)
                }
            }

            override fun afterTextChanged(s: android.text.Editable?) {

            }
        })
    }

    private fun carregarNomeCliente() {
        val idCliente = auth.currentUser?.uid ?: return

        db.collection("clientes").document(idCliente)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val nome = documentSnapshot.getString("nome") ?: "Cliente"
                binding.txtOlaCliente.text = "Olá, $nome!"
                val headerView = binding.navigationView.getHeaderView(0)
                val txtNomeHeader =
                    headerView.findViewById<android.widget.TextView>(R.id.txtNomeHeader)
                txtNomeHeader.text = nome
            }
            .addOnFailureListener { e ->
                Log.e("ClienteHomeActivity", "Erro ao carregar nome do cliente: ${e.message}", e)
                Toast.makeText(this, "Erro ao carregar nome do cliente", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarSaloes() {
        db.collection("saloes").get()
            .addOnSuccessListener { result ->
                saloesList.clear()
                for (document in result) {
                    val salao = document.toObject(Salao::class.java).copy(id = document.id)
                    saloesList.add(salao) // Add to the list
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("ClienteHomeActivity", "Erro ao carregar salões: ${e.message}", e)
                Toast.makeText(this, "Erro ao carregar salões", Toast.LENGTH_SHORT).show()
            }
    }

    private fun buscarSaloes(termo: String) {
        Log.d("DEBUG_BUSCA", "Buscando por: $termo")
        val termoLower = termo.trim().lowercase()
        db.collection("saloes")
            .orderBy("nome_lowercase")
            .startAt(termoLower)
            .endAt(termoLower + "\uf8ff")
            .get()
            .addOnSuccessListener { result ->
                saloesList.clear()
                Log.d("DEBUG_BUSCA", "Encontrados ${result.size()} salões")
                for (document in result) {
                    val salao = document.toObject(Salao::class.java).copy(id = document.id)
                    saloesList.add(salao)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("DEBUG_BUSCA", "Erro na busca: ${e.message}", e)
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
                val intent = Intent(this, LoginActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}


