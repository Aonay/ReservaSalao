package br.edu.fatecpg.reservassalao.view.salao

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.reservassalao.adapter.AgendamentoAdapter
import br.edu.fatecpg.reservassalao.databinding.ActivityHistoricoSalaoBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HistoricoSalaoActivity : AppCompatActivity() {

    private val binding by lazy { ActivityHistoricoSalaoBinding.inflate(layoutInflater) }
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userId = auth.currentUser?.uid ?: return

        binding.recyclerAgendamentos.layoutManager = LinearLayoutManager(this)

        db.collection("agendamentos")
            .whereEqualTo("idSalao", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = mutableListOf<Triple<Agendamento, String, String>>()

                val docs = snapshot.documents
                if (docs.isEmpty()) {
                    Toast.makeText(this, "Nenhum agendamento encontrado", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                var carregados = 0
                for (doc in docs) {
                    val agendamento = doc.toObject(Agendamento::class.java)
                    if (agendamento != null) {
                        val idCliente = agendamento.idCliente
                        val idServico = agendamento.idServico

                        db.collection("clientes").document(idCliente).get()
                            .addOnSuccessListener { clienteDoc ->
                                val nomeCliente = clienteDoc.getString("nome") ?: "Cliente"

                                db.collection("servicos").document(idServico).get()
                                    .addOnSuccessListener { servicoDoc ->
                                        val nomeServico = servicoDoc.getString("nome") ?: "Serviço"

                                        lista.add(Triple(agendamento, nomeCliente, nomeServico))
                                        carregados++

                                        if (carregados == docs.size) {
                                            lista.sortByDescending { it.first.data }
                                            binding.recyclerAgendamentos.adapter = AgendamentoAdapter(lista)
                                        }
                                    }
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar agendamentos: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
