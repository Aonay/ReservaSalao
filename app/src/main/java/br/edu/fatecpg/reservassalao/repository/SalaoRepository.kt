package br.edu.fatecpg.reservassalao.repository

import android.content.Context
import br.edu.fatecpg.reservassalao.model.Salao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class SalaoRepository {
    private val auth = Firebase.auth
    private val db = Firebase.firestore

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
        auth.createUserWithEmailAndPassword(email, senha)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: ""
                val salao = Salao(
                    id = uid,
                    nome = nome,
                    email = email,
                    telefone = telefone,
                    endereco = endereco,
                    cnpj = cnpj,
                    imagemUrl = imagemUrl,
                    tipo = "salao"
                )
                db.collection("saloes").document(uid)
                    .set(salao)
                    .addOnSuccessListener {
                        callback(true, "Cadastro realizado com sucesso!")
                    }
                    .addOnFailureListener {
                        callback(false, "Erro ao salvar no banco: ${it.message}")
                    }
            }
            .addOnFailureListener {
                callback(false, "Erro ao criar cadastro: ${it.message}")
            }
    }
}