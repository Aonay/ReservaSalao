package br.edu.fatecpg.reservassalao.model

import java.io.Serializable

data class Salao(
    override val id: String = "",
    override val nome: String = "",
    override val email: String = "",
    override val tipo: String = "salao",
    override val telefone: String = "",
    override val endereco: String = "",
    val cnpj: String = "",
    val imagemUrl: String = "",
    val horarioFuncionamento: String = "",
    val servicos: List<Servico> = emptyList(),
    val agendamentos: List<String> = emptyList()
) : Usuario(id, nome, email, tipo, telefone, endereco), Serializable