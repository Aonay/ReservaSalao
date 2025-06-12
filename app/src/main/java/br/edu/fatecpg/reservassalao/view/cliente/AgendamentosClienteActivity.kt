package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.reservassalao.adapter.AgendamentoSimplesAdapter
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendamentosClienteBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import br.edu.fatecpg.reservassalao.model.AgendamentoComSalao
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
                val agendamentos = result.toObjects(Agendamento::class.java)
                val listaComSalao = mutableListOf<AgendamentoComSalao>()

                if (agendamentos.isEmpty()) {
                    configurarRecycler(listaComSalao) // Lista vazia
                    return@addOnSuccessListener
                }

                for (agendamento in agendamentos) {
                    db.collection("saloes")
                        .document(agendamento.idSalao)
                        .get()
                        .addOnSuccessListener { doc ->
                            val nomeSalao = doc.getString("nome") ?: "Salão desconhecido"
                            val agendamentoComSalao = AgendamentoComSalao(agendamento, nomeSalao)
                            listaComSalao.add(agendamentoComSalao)

                            // Só atualiza o RecyclerView quando todos forem carregados
                            if (listaComSalao.size == agendamentos.size) {
                                configurarRecycler(listaComSalao)
                            }
                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao buscar salão", Toast.LENGTH_SHORT).show()
                        }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar agendamentos", Toast.LENGTH_SHORT).show()
            }
    }

    private fun configurarRecycler(lista: List<AgendamentoComSalao>) {
        binding.recyclerAgendamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerAgendamentos.adapter = AgendamentoSimplesAdapter(lista)
    }
}
