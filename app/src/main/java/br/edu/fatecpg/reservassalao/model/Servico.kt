package br.edu.fatecpg.reservassalao.model

data class Servico(
    val id:String,
    val nome:String,
    val valor:Double,
    val descricao:String,
    val categoria:CategoriaServico,
    val idSalao:String
)
