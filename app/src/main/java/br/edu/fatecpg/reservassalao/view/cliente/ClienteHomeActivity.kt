package br.edu.fatecpg.reservassalao.view.cliente

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.reservassalao.databinding.ActivityClienteHomeBinding
import br.edu.fatecpg.reservassalao.model.Salao
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClienteHomeActivity : AppCompatActivity() {

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

        auth = Firebase.auth

        adapter = SalaoAdapter(saloesList) { salao ->
            val intent = Intent(this, AgendarActivity::class.java)
            intent.putExtra("salao", salao)
            Toast.makeText(this, "Selecionou salão: ${salao.nome}", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerSaloes.layoutManager = LinearLayoutManager(this)
        binding.recyclerSaloes.adapter = adapter

        carregarNomeCliente()
        carregarSaloes()

        binding.btnLogout.setOnClickListener {
            auth.signOut()
            finish()
        }

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
                    val salao = document.toObject(Salao::class.java)
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
                    val salao = document.toObject(Salao::class.java)
                    saloesList.add(salao)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro na busca", Toast.LENGTH_SHORT).show()
            }
    }
}
