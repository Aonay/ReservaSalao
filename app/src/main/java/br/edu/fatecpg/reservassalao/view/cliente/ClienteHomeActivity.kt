package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityClienteHomeBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import coil.load

class ClienteHomeActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityClienteHomeBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth
        loadSalao()
        setupLogoutButton()
    }

    private fun loadSalao() {
        // Substitua "ID_DO_SALAO" pelo ID real do salão que você quer exibir
        val salaoId = "MiwN9CDQthOW92JBBFxbbr6xp5z1"

        db.collection("saloes").document(salaoId)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val nome = document.getString("nome") ?: ""
                    val endereco = document.getString("endereco") ?: ""
                    val telefone = document.getString("telefone") ?: ""
                    val horario = document.getString("horarioFuncionamento") ?: "Horário não informado"
                    val imagemUrl = document.getString("imagemUrl") ?: ""

                    binding.txtNome.text = nome
                    binding.txtEndereco.text = endereco
                    binding.txtTelefone.text = telefone
                    binding.txtHorario.text = horario

                    if (imagemUrl.isNotEmpty()) {
                        binding.imgSalao.load(imagemUrl) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                            error(android.R.drawable.ic_menu_report_image)
                        }
                    }
                } else {
                    Toast.makeText(this, "Salão não encontrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    this,
                    "Erro ao carregar salão: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun setupLogoutButton() {
        binding.btnLogout.setOnClickListener {
            auth.signOut()
            finish()
        }
    }
}