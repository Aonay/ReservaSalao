package br.edu.fatecpg.reservassalao

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
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

        // Esconde todos os elementos inicialmente
        binding.btnComecar.visibility = android.view.View.GONE
        binding.logoImage.visibility = android.view.View.GONE
        binding.appDescription.visibility = android.view.View.GONE

        // Exibe todos os elementos após 2 segundos
        Handler(Looper.getMainLooper()).postDelayed({binding.logoImage.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate().alpha(1f).setDuration(500).start()
        }

            binding.appDescription.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(500).start()
            }

            binding.btnComecar.apply {
                alpha = 0f
                visibility = View.VISIBLE
                animate().alpha(1f).setDuration(500).start()
            }

        }, 2000)

        // Ações de clique
        binding.btnComecar.setOnClickListener { irParaLogin() }
        binding.logoImage.setOnClickListener { irParaLogin() }
        binding.appDescription.setOnClickListener { irParaLogin() }
    }

    private fun irParaLogin() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}

