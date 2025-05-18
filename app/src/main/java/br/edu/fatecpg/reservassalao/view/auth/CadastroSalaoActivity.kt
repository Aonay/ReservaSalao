package br.edu.fatecpg.reservassalao.view.auth

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityCadastroSalaoBinding
import br.edu.fatecpg.reservassalao.viewmodel.CadastroSalaoViewModel
import coil.load
import coil.request.CachePolicy

class CadastroSalaoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroSalaoBinding.inflate(layoutInflater)
    }

    private val viewModel: CadastroSalaoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        binding.edtImagemUrl.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val url = binding.edtImagemUrl.text.toString()
                if (url.isNotEmpty()) {
                    binding.imgPreview.load(url) {
                        crossfade(true)
                        placeholder(android.R.drawable.ic_menu_gallery)
                        error(android.R.drawable.ic_menu_report_image)
                        memoryCachePolicy(CachePolicy.ENABLED)
                        diskCachePolicy(CachePolicy.ENABLED)
                    }
                }
                true
            } else {
                false
            }
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()
            val telefone = binding.edtTelefone.text.toString()
            val endereco = binding.edtEndereco.text.toString()
            val cnpj = binding.edtCnpj.text.toString()
            val imagemUrl = binding.edtImagemUrl.text.toString()

            if (imagemUrl.isEmpty()) {
                Toast.makeText(this, "Por favor, insira a URL da imagem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.cadastrarSalao(nome, email, senha, telefone, endereco, cnpj, imagemUrl,){ sucesso, mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                if(sucesso) finish()
            }
        }
    }
}