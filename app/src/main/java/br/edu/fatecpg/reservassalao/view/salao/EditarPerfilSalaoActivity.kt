package br.edu.fatecpg.reservassalao.view.salao

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import br.edu.fatecpg.reservassalao.databinding.ActivityEditarPerfilSalaoBinding
import br.edu.fatecpg.reservassalao.model.Salao
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import coil.load

class EditarPerfilSalaoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityEditarPerfilSalaoBinding.inflate(layoutInflater)
    }

    private val auth = Firebase.auth
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val userId = auth.currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "Usuário não está logado.", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        db.collection("saloes").document(userId).get()
            .addOnSuccessListener { doc ->
                if (!doc.exists()) {
                    Toast.makeText(this, "Perfil não encontrado.", Toast.LENGTH_SHORT).show()
                    finish()
                    return@addOnSuccessListener
                }
                val salao = doc.toObject(Salao::class.java)
                if (salao != null) {
                    binding.etNome.setText(salao.nome)
                    binding.etEmail.setText(salao.email)
                    binding.etTelefone.setText(salao.telefone)
                    binding.etEndereco.setText(salao.endereco)
                    binding.etCnpj.setText(salao.cnpj)
                    binding.etHorario.setText(salao.horarioFuncionamento)
                    binding.etImagemUrl.setText(salao.imagemUrl)

                    if (!salao.imagemUrl.isNullOrEmpty()) {
                        binding.imgPreview.load(salao.imagemUrl) {
                            crossfade(true)
                            placeholder(android.R.drawable.ic_menu_gallery)
                            error(android.R.drawable.ic_menu_report_image)
                        }
                    } else {
                        binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
                    }
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar perfil: ${it.message}", Toast.LENGTH_SHORT).show()
            }

        binding.etImagemUrl.addTextChangedListener { text ->
            val url = text.toString()
            if (url.isNotEmpty()) {
                binding.imgPreview.load(url) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                }
            } else {
                binding.imgPreview.setImageResource(android.R.drawable.ic_menu_gallery)
            }
        }

        binding.btnSalvar.setOnClickListener {
            val nome = binding.etNome.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val telefone = binding.etTelefone.text.toString().trim()
            val endereco = binding.etEndereco.text.toString().trim()
            val cnpj = binding.etCnpj.text.toString().trim()
            val horario = binding.etHorario.text.toString().trim()
            val imagemUrl = binding.etImagemUrl.text.toString().trim()

            if (nome.isEmpty() || email.isEmpty() || telefone.isEmpty() || endereco.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos obrigatórios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val salaoAtualizado = mapOf(
                "nome" to nome,
                "email" to email,
                "telefone" to telefone,
                "endereco" to endereco,
                "cnpj" to cnpj,
                "horarioFuncionamento" to horario,
                "imagemUrl" to imagemUrl
            )

            binding.btnSalvar.isEnabled = false

            db.collection("saloes").document(userId).update(salaoAtualizado)
                .addOnSuccessListener {
                    Toast.makeText(this, "Perfil atualizado com sucesso!", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Erro ao atualizar perfil: ${it.message}", Toast.LENGTH_SHORT).show()
                    binding.btnSalvar.isEnabled = true
                }
        }
    }
}
