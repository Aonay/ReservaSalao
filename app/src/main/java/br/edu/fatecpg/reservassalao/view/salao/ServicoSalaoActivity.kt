package br.edu.fatecpg.reservassalao.view.salao

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityServicoSalaoBinding
import br.edu.fatecpg.reservassalao.model.CategoriaServico
import br.edu.fatecpg.reservassalao.model.Servico
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ServicoSalaoActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityServicoSalaoBinding
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()
    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicoSalaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        drawerLayout = findViewById(R.id.drawerLayout)
        binding.navigationView.setNavigationItemSelectedListener(this)

        setupCategoriaSpinner()
        setupSalvarButton()

        binding.btnMenu.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
    }

    private fun setupCategoriaSpinner() {
        val categorias = CategoriaServico.values().map { it.name.capitalize() }
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, categorias)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategoria.adapter = adapter
    }

    private fun setupSalvarButton() {
        binding.btnSalvar.setOnClickListener {
            val nome = binding.edtNome.text.toString().trim()
            val valorText = binding.edtValor.text.toString().trim()
            val descricao = binding.edtDescricao.text.toString().trim()
            val categoriaSelecionada = binding.spinnerCategoria.selectedItem.toString()

            if (nome.isEmpty() || valorText.isEmpty() || descricao.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val valor = valorText.toDoubleOrNull()
            if (valor == null || valor < 0.0) {
                Toast.makeText(this, "Valor inválido.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idServico = UUID.randomUUID().toString()
            val idSalao = auth.currentUser?.uid ?: ""

            val servico = Servico(
                id = idServico,
                nome = nome,
                valor = valor,
                descricao = descricao,
                categoria = CategoriaServico.valueOf(categoriaSelecionada.uppercase()),
                idSalao = idSalao
            )

            val salaoRef = db.collection("saloes").document(idSalao).collection("servicos").document(idServico)
            val servicoGlobalRef = db.collection("servicos").document(idServico)

            salaoRef.set(servico).continueWithTask {
                servicoGlobalRef.set(servico)
            }.addOnSuccessListener {
                Toast.makeText(this, "Serviço cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                Toast.makeText(this, "Erro ao salvar serviço: ${it.message}", Toast.LENGTH_SHORT).show()
            }
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