package br.edu.fatecpg.reservassalao.model

import java.io.Serializable

data class Servico(
    val id: String = "",
    val nome: String = "",
    val valor: Double = 0.0,
    val descricao: String = "",
    val categoria: CategoriaServico = CategoriaServico.OUTROS,
    val idSalao: String = ""
) : Serializable

