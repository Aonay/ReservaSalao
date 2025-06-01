package br.edu.fatecpg.reservassalao.model

import java.io.Serializable

open class Usuario(
    open val id: String = "",
    open val nome: String = "",
    open val email: String = "",
    open val tipo: String = "",
    open val telefone: String = "",
    open val endereco: String = ""
) : Serializable