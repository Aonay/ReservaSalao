package br.edu.fatecpg.reservassalao

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.view.auth.CadastroClienteActivity
import br.edu.fatecpg.reservassalao.view.auth.CadastroSalaoActivity
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding by lazy{
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth
        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        binding.btnCadastroCliente.setOnClickListener {
            val intent = Intent(this, CadastroClienteActivity::class.java)
            startActivity(intent)
        }
        binding.btnCadastroSalao.setOnClickListener {
            val intent = Intent(this, CadastroSalaoActivity::class.java)
            startActivity(intent)
        }

    }
}