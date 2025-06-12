package br.edu.fatecpg.reservassalao.view.salao

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.adapter.AgendamentoAdapter
import br.edu.fatecpg.reservassalao.databinding.ActivityHistoricoSalaoBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import br.edu.fatecpg.reservassalao.model.AgendamentoCompleto
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase


class HistoricoSalaoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var toggle: ActionBarDrawerToggle
    private val binding by lazy { ActivityHistoricoSalaoBinding.inflate(layoutInflater) }
    private val db = Firebase.firestore
    private val auth = Firebase.auth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        carregarNomeSalaoNoHeader()

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.navigationView.setNavigationItemSelectedListener(this)
        binding.recyclerAgendamentos.layoutManager = LinearLayoutManager(this)

        val userId = auth.currentUser?.uid ?: return

        db.collection("agendamentos")
            .whereEqualTo("idSalao", userId)
            .get()
            .addOnSuccessListener { snapshot ->
                val lista = mutableListOf<AgendamentoCompleto>()
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

                                        lista.add(AgendamentoCompleto(agendamento, nomeCliente, nomeServico))
                                        carregados++

                                        if (carregados == docs.size) {
                                            // Ordena por data decrescente
                                            lista.sortByDescending { it.agendamento.data }
                                            binding.recyclerAgendamentos.adapter = AgendamentoAdapter(lista)
                                        }
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(this, "Erro ao buscar serviço", Toast.LENGTH_SHORT).show()
                                    }
                            }
                            .addOnFailureListener {
                                Toast.makeText(this, "Erro ao buscar cliente", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        carregados++
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao buscar agendamentos: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }
    private fun carregarNomeSalaoNoHeader() {
        val idSalao = auth.currentUser?.uid ?: return
        val headerView = binding.navigationView.getHeaderView(0)
        val txtNomeHeader = headerView.findViewById<TextView>(R.id.txtNomeHeader)

        db.collection("saloes").document(idSalao).get()
            .addOnSuccessListener { document ->
                val nomeSalao = document.getString("nome") ?: "Nome do Salão"
                txtNomeHeader.text = nomeSalao
            }
            .addOnFailureListener {
                txtNomeHeader.text = "Erro ao carregar"
            }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> startActivity(Intent(this, SalaoHomeActivity::class.java))
            R.id.menu_servicos -> startActivity(Intent(this, ServicoSalaoActivity::class.java))
            R.id.menu_editar_perfil -> startActivity(Intent(this, EditarPerfilSalaoActivity::class.java))
            R.id.menu_historico -> startActivity(Intent(this, HistoricoSalaoActivity::class.java))
            R.id.menu_sair -> {
                auth.signOut()
                Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
