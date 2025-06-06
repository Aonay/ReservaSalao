package br.edu.fatecpg.reservassalao.view.cliente

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import br.edu.fatecpg.reservassalao.databinding.ActivityAgendamentosClienteBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AgendamentosClienteActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityAgendamentosClienteBinding.inflate(layoutInflater)
    }

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(binding.root)

        auth = Firebase.auth

        // Abertura do menu lateral
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(binding.navigationView)
        }

        // Você pode adicionar comportamento de clique nos itens do menu aqui se quiser
        binding.navigationView.setNavigationItemSelectedListener { menuItem ->
            // Ex: if (menuItem.itemId == R.id.menu_sair) { ... }
            binding.drawerLayout.closeDrawers()
            true
        }
    }
}
