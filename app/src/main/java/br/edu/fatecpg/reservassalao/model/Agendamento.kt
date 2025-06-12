package br.edu.fatecpg.reservassalao.model
import java.io.Serializable
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Calendar
import java.util.Date

data class Agendamento(
    val idServico: String = "",
    val idSalao: String = "",
    val idCliente: String = "",
    val data: Date = Date(),
    val hora: String = "",
    val status: String = ""
) : Serializable

fun simularAgendamentoPassado() {
    val db = FirebaseFirestore.getInstance()

    val agendamentoSimulado = Agendamento(
        idServico = "123d1b3b-dab6-4a45-bb3b-cd147e984c65",
        idSalao = "fAxcWr9K7OPURaJ2tZn7Y1R7id52",
        idCliente = "zfsgt2Gm2rgjFwwK6B6geomt5qx1",
        data = Calendar.getInstance().apply {
            set(2025, Calendar.JUNE, 11, 14, 0) // 10 de junho de 2025 às 14:00
        }.time,
        hora = "18:00",
        status = "pendente"
    )

    db.collection("agendamentos")
        .add(agendamentoSimulado)
        .addOnSuccessListener {
            println("✅ Agendamento simulado criado com sucesso!")
        }
        .addOnFailureListener {
            println("❌ Erro ao criar agendamento: ${it.message}")
        }
}

