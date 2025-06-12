package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendamentosClienteBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AgendamentosClienteActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAgendamentosClienteBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore
    private lateinit var adapter: AgendamentoAdapter
    private val listaAgendamentos = mutableListOf<Agendamento>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth
        val idUsuario = auth.currentUser?.uid ?: return

        // Inicializa RecyclerView
        adapter = AgendamentoAdapter(listaAgendamentos)
        binding.recyclerAgendamentos.layoutManager = LinearLayoutManager(this)
        binding.recyclerAgendamentos.adapter = adapter

        // Abertura do menu lateral
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            binding.drawerLayout.closeDrawers()
            true
        }

        // Buscar agendamentos do Firebase
        db.collection("agendamentos")
            .whereEqualTo("idCliente", idUsuario)
            .get()
            .addOnSuccessListener { result ->
                listaAgendamentos.clear()
                for (document in result) {
                    val agendamento = document.toObject(Agendamento::class.java)
                    listaAgendamentos.add(agendamento)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Log.e("AgendamentosCliente", "Erro ao buscar agendamentos", e)
            }
    }
}
