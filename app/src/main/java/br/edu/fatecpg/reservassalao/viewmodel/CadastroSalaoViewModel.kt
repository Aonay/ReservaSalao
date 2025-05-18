package br.edu.fatecpg.reservassalao.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.fatecpg.reservassalao.repository.SalaoRepository

class CadastroSalaoViewModel : ViewModel() {
    private val repository = SalaoRepository()

    fun cadastrarSalao(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        endereco: String,
        cnpj: String,
        imagemUrl: String,
        callback: (Boolean, String) -> Unit
    ) {
        repository.cadastrarSalao(nome, email, senha, telefone, endereco, cnpj, imagemUrl, callback)
    }
}