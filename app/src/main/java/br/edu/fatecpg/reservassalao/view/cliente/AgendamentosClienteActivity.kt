package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.reservassalao.adapter.AgendamentoSimplesAdapter
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendamentosClienteBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AgendamentosClienteActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAgendamentosClienteBinding.inflate(layoutInflater)
    }

    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        setupMenu()
        carregarAgendamentos()
    }

    private fun setupMenu() {
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()
            true
        }
    }

    private fun carregarAgendamentos() {
        val idCliente = auth.currentUser?.uid ?: return

        db.collection("agendamentos")
            .whereEqualTo("idCliente", idCliente)
            .get()
            .addOnSuccessListener { result ->
                val lista = result.toObjects(Agendamento::class.java)
                binding.recyclerAgendamentos.layoutManager = LinearLayoutManager(this)
                binding.recyclerAgendamentos.adapter = AgendamentoSimplesAdapter(lista)
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar agendamentos", Toast.LENGTH_SHORT).show()
            }
    }

}