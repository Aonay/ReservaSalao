package br.edu.fatecpg.reservassalao.view.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.MainActivity
import br.edu.fatecpg.reservassalao.R
import br.edu.fatecpg.reservassalao.databinding.ActivityCadastroClienteBinding
import br.edu.fatecpg.reservassalao.viewmodel.CadastroClienteViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CadastroClienteActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityCadastroClienteBinding.inflate(layoutInflater)
    }

    private val viewModel: CadastroClienteViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        val fabSair = findViewById<FloatingActionButton>(R.id.fab_sair)
        fabSair.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.btnCadastrar.setOnClickListener {
            val nome = binding.edtNome.text.toString()
            val email = binding.edtEmail.text.toString()
            val senha = binding.edtSenha.text.toString()
            val confirmSenha = binding.edtConfirmSenha.text.toString()
            val telefone = binding.edtTelefone.text.toString()
            val endereco = binding.edtEndereco.text.toString()
            val cpf = binding.edtCpf.text.toString()
            val fabSair = findViewById<FloatingActionButton>(R.id.fab_sair)


            if (senha != confirmSenha) {
                Toast.makeText(this, "As senhas não coincidem", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("Cadastro", "Dados recebidos: $nome, $email, $telefone, $endereco, $cpf")

            viewModel.cadastrarCliente(nome, email, senha, telefone, endereco, cpf){ sucesso, mensagem ->
                    Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show()
                    if(sucesso) finish()
                }
        }
    }
}