package br.edu.fatecpg.reservassalao.view.salao

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivitySalaoHomeBinding
import br.edu.fatecpg.reservassalao.model.Salao
import br.edu.fatecpg.reservassalao.model.Servico
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import coil.load
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SalaoHomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener{
    private val binding by lazy {
        ActivitySalaoHomeBinding.inflate(layoutInflater)
    }

    private lateinit var drawerLayout: DrawerLayout
    private val auth: FirebaseAuth by lazy { Firebase.auth }
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        // Atualizar o nome do cabeçalho
        val headerView = navigationView.getHeaderView(0)
        val txtNomeHeader = headerView.findViewById<TextView>(R.id.txtNomeHeader)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("saloes").document(userId).get()
                .addOnSuccessListener { doc ->
                    val salao = doc.toObject(Salao::class.java)

                    if (salao != null) {
                        binding.tvNomeSalao.text = salao.nome
                        binding.tvHorarioFuncionamento.text =
                            "Horário: ${salao.horarioFuncionamento ?: "Não informado"}"

                        if (!salao.imagemUrl.isNullOrEmpty()) {
                            binding.imgSalao.load(salao.imagemUrl)
                        }

                        carregarServicos(userId)
                        carregarAgendamentos(userId)
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao carregar salão: ${it.message}", Toast.LENGTH_SHORT).show()
                }
        }

        binding.btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.btnVerTodosAgendamentos.setOnClickListener {
            startActivity(Intent(this, HistoricoSalaoActivity::class.java)) // ou outra tela com todos agendamentos
        }

    }

    private fun carregarServicos(idSalao: String) {
        db.collection("servicos")
            .whereEqualTo("idSalao", idSalao)
            .get()
            .addOnSuccessListener { servicosSnapshot ->
                binding.layoutServicos.removeAllViews()

                val servicos = servicosSnapshot.documents.mapNotNull {
                    it.toObject(Servico::class.java)
                }

                val ultimosServicos = servicos.takeLast(3).reversed()

                for (servico in ultimosServicos) {
                    val tv = TextView(this)
                    tv.text = "• ${servico.nome} - R$ ${"%.2f".format(servico.valor)}"
                    binding.layoutServicos.addView(tv)
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar serviços: ${it.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun carregarAgendamentos(idSalao: String) {
        db.collection("agendamentos")
            .whereEqualTo("Salaoid", idSalao)
            .orderBy("data", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(3)
            .get()
            .addOnSuccessListener { snapshot ->
                binding.layoutAgendamentos.removeAllViews()

                for (doc in snapshot.documents) {
                    val nomeServico = doc.getString("nomeServico") ?: "Serviço"
                    val idCliente = doc.getString("idCliente")
                    val data = doc.getString("data") ?: "Data"
                    val hora = doc.getString("hora") ?: "Hora"

                    if (idCliente != null) {
                        db.collection("clientes").document(idCliente).get()
                            .addOnSuccessListener { clienteDoc ->
                                val nomeCliente = clienteDoc.getString("nome") ?: "Cliente"

                                val tv = TextView(this)
                                tv.text = "• $nomeServico com $nomeCliente em $data às $hora"
                                binding.layoutAgendamentos.addView(tv)
                            }
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar agendamentos: ${it.message}", Toast.LENGTH_SHORT).show()
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