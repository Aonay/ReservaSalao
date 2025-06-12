package br.edu.fatecpg.reservassalao.model

import java.io.Serializable

data class Cliente(
    override val id: String = "",
    override val nome: String = "",
    override val email: String = "",
    override val tipo: String = "cliente",
    override val telefone: String = "",
    override val endereco:String = "",
    val cpf: String = "",
    val agendamentos: List<Agendamento> = emptyList()

) : Usuario(id,nome,email,tipo,telefone,endereco), Serializable
