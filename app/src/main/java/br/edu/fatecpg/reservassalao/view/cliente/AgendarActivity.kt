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
import br.edu.fatecpg.reservassalao.model.Agendamento
import br.edu.fatecpg.reservassalao.model.Salao
import com.bumptech.glide.Glide
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.navigation.NavigationView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class AgendarActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityAgendarBinding
    private val db = Firebase.firestore
    private val auth = Firebase.auth
    private lateinit var salao: Salao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgendarBinding.inflate(layoutInflater)
        setContentView(binding.root)
        carregarNomeCliente()

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
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.timeInMillis = selection

                val localCalendar = Calendar.getInstance()
                localCalendar.set(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )

                val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                formatoData.timeZone = TimeZone.getDefault()

                val dataFormatada = formatoData.format(localCalendar.time)
                binding.edtData.setText(dataFormatada)
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
    private fun carregarNomeCliente() {
        val idCliente = auth.currentUser?.uid ?: return

        db.collection("clientes").document(idCliente)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val nome = documentSnapshot.getString("nome") ?: "Cliente"

                val headerView = binding.navigationView.getHeaderView(0)
                val txtNomeHeader = headerView.findViewById<android.widget.TextView>(R.id.txtNomeHeader)
                txtNomeHeader.text = nome
            }
            .addOnFailureListener { e ->
                Log.e("ClienteHomeActivity", "Erro ao carregar nome do cliente: ${e.message}", e)
                Toast.makeText(this, "Erro ao carregar nome do cliente", Toast.LENGTH_SHORT).show()
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
                val adapter =
                    ArrayAdapter(this, android.R.layout.simple_spinner_item, nomesServicos)
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

            val delimitador = if (salao.horarioFuncionamento.contains(" às ")) " às "
            else if (salao.horarioFuncionamento.contains(" - ")) " - "
            else null

            if (delimitador == null) {
                Toast.makeText(this, "Horário de funcionamento inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val partesHorario = salao.horarioFuncionamento.split(delimitador)
            if (partesHorario.size != 2) {
                Toast.makeText(this, "Horário de funcionamento mal formatado", Toast.LENGTH_SHORT)
                    .show()
                return@setOnClickListener
            }

            val horaAberturaStr = partesHorario[0].trim()
            val horaFechamentoStr = partesHorario[1].trim()
            val formatoHora = SimpleDateFormat("HH:mm", Locale.getDefault())

            try {
                val horaSelecionada = formatoHora.parse(horarioTexto)
                val horaAbertura = formatoHora.parse(horaAberturaStr)
                val horaFechamento = formatoHora.parse(horaFechamentoStr)

                if (horaSelecionada.before(horaAbertura) || horaSelecionada.after(horaFechamento)) {
                    Toast.makeText(
                        this,
                        "Horário fora do funcionamento do salão ($horaAberturaStr às $horaFechamentoStr)",
                        Toast.LENGTH_LONG
                    ).show()
                    return@setOnClickListener
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Formato de horário inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }


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

                    db.collection("saloes").document(salao.id)
                        .collection("servicos")
                        .whereEqualTo("nome", nomeServico)
                        .get()
                        .addOnSuccessListener { result ->
                            val idServico = result.documents.firstOrNull()?.id
                            if (idServico == null) {
                                Toast.makeText(
                                    this,
                                    "Erro ao localizar serviço",
                                    Toast.LENGTH_SHORT
                                ).show()
                                return@addOnSuccessListener
                            }

                            // Verificar conflito com idSalao, data, hora e idServico
                            db.collection("agendamentos")
                                .whereEqualTo("idSalao", salao.id)
                                .whereEqualTo("data", dataConvertida)
                                .whereEqualTo("hora", horarioTexto)
                                .whereEqualTo("idServico", idServico)
                                .get()
                                .addOnSuccessListener { agendamentos ->
                                    if (!agendamentos.isEmpty) {
                                        Toast.makeText(
                                            this,
                                            "Já existe um agendamento com esse serviço nesse salão, data e hora.",
                                            Toast.LENGTH_LONG
                                        ).show()
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
                                            Toast.makeText(
                                                this,
                                                "Agendado com sucesso!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                            finish()
                                        }
                                        .addOnFailureListener {
                                            Toast.makeText(
                                                this,
                                                "Erro ao agendar",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                }
                                .addOnFailureListener {
                                    Toast.makeText(
                                        this,
                                        "Erro ao buscar serviço",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }

                        }
                        .addOnFailureListener {
                            Toast.makeText(this, "Erro ao verificar conflitos", Toast.LENGTH_SHORT)
                                .show()
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

