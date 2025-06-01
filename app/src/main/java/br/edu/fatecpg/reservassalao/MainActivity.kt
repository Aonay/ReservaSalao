package br.edu.fatecpg.reservassalao

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import br.edu.fatecpg.reservassalao.databinding.ActivityMainBinding
import br.edu.fatecpg.reservassalao.view.auth.LoginActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth

        // Esconde o botão "Começar" inicialmente
        binding.btnComecar.visibility = android.view.View.GONE

        // Espera 2 segundos para exibir o botão
        Handler(Looper.getMainLooper()).postDelayed({
            binding.btnComecar.visibility = android.view.View.VISIBLE
        }, 2000)

        binding.btnComecar.setOnClickListener {
            val intent = Intent(this@MainActivity, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
