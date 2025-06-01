package br.edu.fatecpg.reservassalao.view.cliente

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.reservassalao.databinding.ItemSalaoBinding
import br.edu.fatecpg.reservassalao.model.Salao
import coil.load

class SalaoAdapter(
    private val saloes: List<Salao>,
    private val onItemClick: (Salao) -> Unit
) : RecyclerView.Adapter<SalaoAdapter.SalaoViewHolder>() {

    inner class SalaoViewHolder(private val binding: ItemSalaoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(salao: Salao) {
            binding.txtNomeItem.text = salao.nome
            binding.txtEnderecoItem.text = salao.endereco
            if (salao.imagemUrl.isNotEmpty()) {
                binding.imgSalaoItem.load(salao.imagemUrl)
            } else {
                binding.imgSalaoItem.setImageResource(android.R.color.transparent)
            }
            binding.root.setOnClickListener {
                onItemClick(salao)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SalaoViewHolder {
        val binding = ItemSalaoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SalaoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SalaoViewHolder, position: Int) {
        holder.bind(saloes[position])
    }

    override fun getItemCount(): Int = saloes.size
}
