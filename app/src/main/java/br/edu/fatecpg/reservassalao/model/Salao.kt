package br.edu.fatecpg.reservassalao.model

data class Salao(
    override val id: String = "",
    override val nome: String = "",
    override val email: String = "",
    override val tipo: String = "cliente",
    override val telefone: String = "",
    override val endereco:String = "",
    val cnpj:Int,
    val imagemUrl:String = "",
    val horarioFuncioamento: String = "",
    val servicos: List<Servico> = listOf(),
    val agendamentos: List<Agendamento> = listOf()
): Usuario(id,nome,email,tipo,telefone,endereco)
