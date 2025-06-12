package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendarBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import br.edu.fatecpg.reservassalao.model.Salao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        Glide.with(this)
            .load(salao.imagemUrl) // Supondo que `imagemUrl` é um campo em Salao
            .placeholder(R.drawable.ic_launcher_background)
            .into(binding.imgSalaoItem)

        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.edtData.setOnClickListener {
            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Selecione a data")
                .build()
            datePicker.show(supportFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { selection ->
                val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                binding.edtData.setText(sdf.format(Date(selection)))
            }
        }

        binding.edtHorario.setOnClickListener {
            val timePicker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(14)
                .setMinute(0)
                .setTitleText("Selecione o horário")
                .build()
            timePicker.show(supportFragmentManager, "TIME_PICKER")

            timePicker.addOnPositiveButtonClickListener {
                val hour = timePicker.hour.toString().padStart(2, '0')
                val minute = timePicker.minute.toString().padStart(2, '0')
                binding.edtHorario.setText("$hour:$minute")
            }
        }

    }

    private fun carregarServicos() {
        db.collection("saloes").document(salao.id)
            .collection("servicos")
            .get()
            .addOnSuccessListener { result ->
                if (result.isEmpty) {
                    Log.d("AgendarActivity", "Nenhum serviço encontrado para o salão ${salao.id}")
                }
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
            val nomeServico = binding.spinnerServicos.selectedItem?.toString()
            val dataTexto = binding.edtData.text.toString()
            val horarioTexto = binding.edtHorario.text.toString()

            if (nomeServico.isNullOrEmpty() || dataTexto.isEmpty() || horarioTexto.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Converte a data (String) para Date
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val dataConvertida: Date = try {
                sdf.parse(dataTexto) ?: Date()
            } catch (e: Exception) {
                Toast.makeText(this, "Data inválida", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val idCliente = FirebaseAuth.getInstance().currentUser?.uid
            if (idCliente == null) {
                Toast.makeText(this, "Erro ao obter usuário", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Buscar ID do serviço com base no nome
            db.collection("saloes").document(salao.id)
                .collection("servicos")
                .whereEqualTo("nome", nomeServico)
                .get()
                .addOnSuccessListener { result ->
                    val idServico = result.documents.firstOrNull()?.id
                    if (idServico == null) {
                        Toast.makeText(this, "Erro ao localizar serviço", Toast.LENGTH_SHORT).show()
                        return@addOnSuccessListener
                    }

                    val agendamento = Agendamento(
                        idServico = idServico,
                        idSalao = salao.id,
                        idCliente = idCliente,
                        data = dataConvertida,
                        hora = horarioTexto,
                        status = "pendente"
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
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao buscar serviço", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
