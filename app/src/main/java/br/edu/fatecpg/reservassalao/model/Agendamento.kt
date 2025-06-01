package br.edu.fatecpg.reservassalao.model

import java.util.Date

data class Agendamento(
    val idServico: String = "",
    val idSalao: String = "",
    val idCliente: String = "",
    val data: Date = Date(),
    val hora: String = "",
    val status: String = ""
)
