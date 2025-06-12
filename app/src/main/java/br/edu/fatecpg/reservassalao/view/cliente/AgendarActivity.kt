package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendarBinding
import br.edu.fatecpg.reservassalao.model.Salao
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FieldValue
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
        val userId = Firebase.auth.currentUser?.uid
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
                "userId" to userId,
                "servico" to servico,
                "data" to data,
                "horario" to horario,
                "status" to "pendente"
            )

            val userId = Firebase.auth.currentUser?.uid
            if (userId == null) {
                Toast.makeText(this, "Usuário não autenticado", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verifica se já existe um agendamento para o mesmo salão, data, horário e serviço
            db.collection("agendamentos")
                .whereEqualTo("salaoId", salao.id)
                .whereEqualTo("userId", userId)
                .whereEqualTo("data", data)
                .whereEqualTo("horario", horario)
                .whereEqualTo("servico", servico)
                .whereEqualTo("status", "pendente")
                .get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        Toast.makeText(this, "Esse horário já está ocupado para o serviço escolhido.", Toast.LENGTH_LONG).show()
                    } else {
                        // Prossegue com o agendamento se estiver livre
            db.collection("agendamentos")
                .add(agendamento)
                .addOnSuccessListener { agendamentoRef ->
                 val agendamentoId = agendamentoRef.id

            db.collection("clientes").document(userId)
                 .update("agendamentos", FieldValue.arrayUnion(agendamentoId))
                 .addOnSuccessListener {

            db.collection("saloes").document(salao.id)
                 .collection("agendamentos").document(agendamentoId)
                 .set(agendamento)
                 .addOnSuccessListener {
                        Toast.makeText(this, "Agendado com sucesso!", Toast.LENGTH_SHORT).show()
                  finish()
                   }

                  .addOnFailureListener {
                        Toast.makeText(this, "Erro ao salvar no salão", Toast.LENGTH_SHORT).show()
                   }
               }
                  .addOnFailureListener {
                        Toast.makeText(this, "Erro ao salvar no cliente", Toast.LENGTH_SHORT).show()
                   }
               }
                  .addOnFailureListener {
                        Toast.makeText(this, "Erro ao agendar", Toast.LENGTH_SHORT).show()
                   }
            }
        }
                .addOnFailureListener {
                        Toast.makeText(this, "Erro ao verificar disponibilidade", Toast.LENGTH_SHORT).show()
                }

        }
    }

}
