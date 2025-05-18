package br.edu.fatecpg.reservassalao.viewmodel

import androidx.lifecycle.ViewModel
import br.edu.fatecpg.reservassalao.repository.ClienteRepository

class CadastroClienteViewModel : ViewModel(){
    private val repository = ClienteRepository()

    fun cadastrarCliente(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        endereco: String,
        cpf: String,
        callback: (Boolean, String) -> Unit
    ){
        repository.cadastrarCliente(nome, email, senha, telefone, endereco, cpf, callback)
    }
}