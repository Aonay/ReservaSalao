package br.edu.fatecpg.reservassalao.view.auth

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import br.edu.fatecpg.reservassalao.MainActivity
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityCadastroSalaoBinding
import br.edu.fatecpg.reservassalao.viewmodel.CadastroSalaoViewModel
import coil.load
import coil.request.CachePolicy
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CadastroSalaoActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityCadastroSalaoBinding.inflate(layoutInflater)
    }

    private lateinit var viewModel: CadastroSalaoViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        viewModel = ViewModelProvider(this)[CadastroSalaoViewModel::class.java]
        val fabSair = findViewById<FloatingActionButton>(R.id.fab_sair)
        fabSair.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.edtImagemUrl.addTextChangedListener {
            val url = it.toString()
            if (url.isNotEmpty()) {
                binding.imgPreview.load(url) {
                    crossfade(true)
                    placeholder(android.R.drawable.ic_menu_gallery)
                    error(android.R.drawable.ic_menu_report_image)
                    memoryCachePolicy(CachePolicy.ENABLED)
                    diskCachePolicy(CachePolicy.ENABLED)
                }
            }
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()
            val confirmSenha = binding.edtConfirmSenha.text.toString()
            val telefone = binding.edtTelefone.text.toString()
            val endereco = binding.edtEndereco.text.toString()
            val cnpj = binding.edtCnpj.text.toString()
            val imagemUrl = binding.edtImagemUrl.text.toString()

            if (imagemUrl.isEmpty()) {
                Toast.makeText(this, "Por favor, insira a URL da imagem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (senha != confirmSenha) {
                Toast.makeText(this, "As senhas não conferem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            viewModel.cadastrarSalao(nome, email, senha, telefone, endereco, cnpj, imagemUrl,){ sucesso, mensagem ->
                Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                if(sucesso) finish()
            }
        }
    }
}