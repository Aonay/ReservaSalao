package br.edu.fatecpg.reservassalao.model

import java.sql.Time
import java.util.Date

data class Agendamento(
    val idServico:String,
    val idSalao:String,
    val idCliente:String,
    val data:Date,
    val hora:Time,
    val status:String

)
