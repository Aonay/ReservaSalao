package br.edu.fatecpg.reservassalao.view.salao

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityServicoSalaoBinding
import br.edu.fatecpg.reservassalao.model.CategoriaServico
import br.edu.fatecpg.reservassalao.model.Servico
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.util.*

class ServicoSalaoActivity : AppCompatActivity() {

    private lateinit var binding: ActivityServicoSalaoBinding
    private val db = Firebase.firestore
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityServicoSalaoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupCategoriaSpinner()
        setupSalvarButton()
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

            db.collection("servicos").document(idServico)
                .set(servico)
                .addOnSuccessListener {
                    Toast.makeText(this, "Serviço cadastrado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish() // volta pra tela anterior
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao salvar serviço.", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
