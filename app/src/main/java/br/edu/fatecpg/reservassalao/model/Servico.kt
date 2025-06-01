package br.edu.fatecpg.reservassalao.model

data class Servico(
    val id: String = "",
    val nome: String = "",
    val valor: Double = 0.0,
    val descricao: String = "",
    val categoria: CategoriaServico = CategoriaServico.OUTROS,
    val idSalao: String = ""
)
