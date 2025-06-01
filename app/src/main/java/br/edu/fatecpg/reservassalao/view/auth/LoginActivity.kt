package br.edu.fatecpg.reservassalao.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityLoginBinding
import br.edu.fatecpg.reservassalao.view.cliente.ClienteHomeActivity
import br.edu.fatecpg.reservassalao.view.salao.SalaoHomeActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth

        setupLoginButton()
    }

    private fun setupLoginButton() {
        binding.btnLogin.setOnClickListener {
            val email = binding.edtEmail.text.toString().trim()
            val senha = binding.edtSenha.text.toString().trim()

            if (validateInputs(email, senha)) {
                loginUser(email, senha)
            }
        }
    }

    private fun validateInputs(email: String, senha: String): Boolean {
        return when {
            email.isEmpty() -> {
                binding.edtEmail.error = "E-mail é obrigatório"
                false
            }
            !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                binding.edtEmail.error = "E-mail inválido"
                false
            }
            senha.isEmpty() -> {
                binding.edtSenha.error = "Senha é obrigatória"
                false
            }
            senha.length < 6 -> {
                binding.edtSenha.error = "Senha deve ter pelo menos 6 caracteres"
                false
            }
            else -> true
        }
    }

    private fun loginUser(email: String, senha: String) {
        auth.signInWithEmailAndPassword(email, senha)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val userId = auth.currentUser?.uid ?: ""
                    checkUserTypeAndRedirect(userId)
                } else {
                    Toast.makeText(
                        this,
                        "Falha no login: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("LoginActivity", "Erro no login", task.exception)
                }
            }
    }

    private fun checkUserTypeAndRedirect(userId: String) {
        // Verifica se é um cliente
        db.collection("clientes").document(userId)
            .get()
            .addOnSuccessListener { clienteDoc ->
                if (clienteDoc.exists()) {
                    redirectToClienteHome()
                } else {
                    // Verifica se é um salão
                    db.collection("saloes").document(userId)
                        .get()
                        .addOnSuccessListener { salaoDoc ->
                            if (salaoDoc.exists()) {
                                redirectToSalaoHome()
                            } else {
                                Toast.makeText(
                                    this,
                                    "Usuário não encontrado no sistema.",
                                    Toast.LENGTH_SHORT
                                ).show()
                                auth.signOut()
                            }
                        }
                        .addOnFailureListener { e ->
                            Log.e("LoginActivity", "Erro ao buscar salão", e)
                            Toast.makeText(
                                this,
                                "Erro ao verificar perfil de salão.",
                                Toast.LENGTH_SHORT
                            ).show()
                            auth.signOut()
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.e("LoginActivity", "Erro ao buscar cliente", e)
                Toast.makeText(
                    this,
                    "Erro ao verificar perfil de cliente.",
                    Toast.LENGTH_SHORT
                ).show()
                auth.signOut()
            }
    }

    private fun redirectToClienteHome() {
        val intent = Intent(this, ClienteHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun redirectToSalaoHome() {
        val intent = Intent(this, SalaoHomeActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}
