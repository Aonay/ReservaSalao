package br.edu.fatecpg.reservassalao.repository

import br.edu.fatecpg.reservassalao.model.Cliente
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class ClienteRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

    fun cadastrarCliente(
        nome: String,
        email: String,
        senha: String,
        telefone: String,
        endereco: String,
        cpf: String,
        callback: (Boolean, String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val cliente = Cliente(
                    id = uid,
                    nome = nome,
                    email = email,
                    telefone = telefone,
                    endereco = endereco,
                    cpf = cpf
                )
                db.collection("clientes").document(uid)
                    .set(cliente)
                    .addOnSuccessListener {
                        callback(true, "Cadastro realizado com sucesso!")
                    }
                    .addOnFailureListener {
                        callback(false, "Erro ao salvar no banco: ${it.message}")
                    }
            }
            .addOnFailureListener {
                callback(false, "Erro ao criar usuário: ${it.message}")
            }
    }
}