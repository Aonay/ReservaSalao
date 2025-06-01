package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendarBinding
import br.edu.fatecpg.reservassalao.model.Salao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class AgendarActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAgendarBinding
    private val db = Firebase.firestore
    private lateinit var salao: Salao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        salao = intent.getSerializableExtra("salao") as Salao
        binding.txtNomeSalao.text = salao.nome

        carregarServicos()
        setupBotaoAgendar()

        binding.btnVoltar.setOnClickListener {
            finish()
        }

    }

    private fun carregarServicos() {
        db.collection("saloes").document(salao.id)
            .collection("servicos")
            .get()
            .addOnSuccessListener { result ->
                val nomesServicos = result.map { it.getString("nome") ?: "Serviço" }
                val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, nomesServicos)
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                binding.spinnerServicos.adapter = adapter
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar serviços", Toast.LENGTH_SHORT).show()
            }
    }

    private fun setupBotaoAgendar() {
        binding.btnAgendar.setOnClickListener {
            val servico = binding.spinnerServicos.selectedItem?.toString()
            val data = binding.edtData.text.toString()
            val horario = binding.edtHorario.text.toString()

            if (servico.isNullOrEmpty() || data.isEmpty() || horario.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val agendamento = hashMapOf(
                "salaoId" to salao.id,
                "servico" to servico,
                "data" to data,
                "horario" to horario
            )

            db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener {
                    Toast.makeText(this, "Agendado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao agendar", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
