package br.edu.fatecpg.reservassalao.view.salao

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.widget.addTextChangedListener
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityEditarPerfilSalaoBinding
import br.edu.fatecpg.reservassalao.model.Salao
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import coil.load
import com.google.android.material.navigation.NavigationView

class EditarPerfilSalaoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private val binding by lazy {
        ActivityEditarPerfilSalaoBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        carregarNomeSalaoNoHeader()

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        binding.navigationView.setNavigationItemSelectedListener(this)

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("saloes").document(userId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Toast.makeText(this, "Perfil não encontrado.", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }
                val salao = doc.toObject(Salao::class.java)
                if (salao != null) {
                    binding.etNome.setText(salao.nome)
                    binding.etEmail.setText(salao.email)
                    binding.etTelefone.setText(salao.telefone)
                    binding.etEndereco.setText(salao.endereco)
                    binding.etCnpj.setText(salao.cnpj)
                    binding.etHorario.setText(salao.horarioFuncionamento)
                    binding.etImagemUrl.setText(salao.imagemUrl)

                    if (!salao.imagemUrl.isNullOrEmpty()) {
                        binding.imgSalao.load(salao.imagemUrl) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                            error(android.R.drawable.ic_menu_report_image)
                        }
                    } else {
                        binding.imgSalao.setImageResource(android.R.drawable.ic_menu_gallery)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar perfil: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        binding.etImagemUrl.addTextChangedListener { text ->
            val url = text.toString()
            if (url.isNotEmpty()) {
                binding.imgSalao.load(url) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.imgSalao.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }

        binding.btnSalvar.setOnClickListener {
            val nome = binding.etNome.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telefone = binding.etTelefone.text.toString().trim()
            val endereco = binding.etEndereco.text.toString().trim()
            val cnpj = binding.etCnpj.text.toString().trim()
            val horario = binding.etHorario.text.toString().trim()
            val imagemUrl = binding.etImagemUrl.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || endereco.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salaoAtualizado = mapOf(
                "nome" to nome,
                "email" to email,
                "telefone" to telefone,
                "endereco" to endereco,
                "cnpj" to cnpj,
                "horarioFuncionamento" to horario,
                "imagemUrl" to imagemUrl
            )

            binding.btnSalvar.isEnabled = false

            db.collection("saloes").document(userId).update(salaoAtualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar perfil: ${it.message}", Toast.LENGTH_SHORT).show()
                    binding.btnSalvar.isEnabled = true
                }
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