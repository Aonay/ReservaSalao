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
    private lateinit var txtNomeHeader: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)
        val navigationView: NavigationView = findViewById(R.id.navigationView)
        navigationView.setNavigationItemSelectedListener(this)

        val headerView = navigationView.getHeaderView(0)
        txtNomeHeader = headerView.findViewById(R.id.txtNomeHeader)

        val userId = auth.currentUser?.uid

        if (userId != null) {
            db.collection("saloes").document(userId).get()
                .addOnSuccessListener { doc ->
                    val salao = doc.toObject(Salao::class.java)

                    if (salao != null) {

                        carregarNome(salao)
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

    override fun onResume() {
        super.onResume()
        val userId = auth.currentUser?.uid
        if (userId != null) {
            carregarServicos(userId)   }
    }

    private fun carregarNome(salao: Salao) {
        binding.tvNomeSalao.text = salao.nome
        binding.tvHorarioFuncionamento.text =
            "Horário: ${salao.horarioFuncionamento ?: "Não informado"}"

        txtNomeHeader.text = salao.nome


        if (!salao.imagemUrl.isNullOrEmpty()) {
            binding.imgSalao.load(salao.imagemUrl)
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

    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home -> {  }
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