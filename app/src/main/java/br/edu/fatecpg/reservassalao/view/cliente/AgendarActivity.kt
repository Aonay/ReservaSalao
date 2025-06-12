package br.edu.fatecpg.reservassalao.view.cliente

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendarBinding
import br.edu.fatecpg.reservassalao.model.Salao
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.io.Serializable

class AgendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityAgendarBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth // ✅ Instanciando o Firebase Auth
    private lateinit var salao: Salao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendarBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.navigationView.setNavigationItemSelectedListener(this)

        salao = intent.getSerializableExtra("salao") as Salao
        binding.txtNomeSalao.text = salao.nome

        carregarServicos()
        setupBotaoAgendar()

        Glide.with(this)
            .load(salao.imagemUrl)
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

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_home_salao -> {
                startActivity(Intent(this, ClienteHomeActivity::class.java))
            }
            R.id.menu_agendamentos_salao -> {
                startActivity(Intent(this, AgendamentosClienteActivity::class.java))
            }
            R.id.menu_sair -> {
                auth.signOut()
                Toast.makeText(this, "Sessão encerrada", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}
