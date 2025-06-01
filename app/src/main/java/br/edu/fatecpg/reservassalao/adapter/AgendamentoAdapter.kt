package br.edu.fatecpg.reservassalao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.reservassalao.databinding.ItemAgendamentoBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import java.text.SimpleDateFormat
import java.util.*

class AgendamentoAdapter(private val lista: List<Triple<Agendamento, String, String>>) :
    RecyclerView.Adapter<AgendamentoAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAgendamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = lista.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(lista[position])
    }

    class ViewHolder(val binding: ItemAgendamentoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Triple<Agendamento, String, String>) {
            val (agendamento, nomeCliente, nomeServico) = item
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val horaStr = agendamento.hora

            binding.tvInfo.text =
                "• $nomeServico com $nomeCliente em ${sdf.format(agendamento.data)} às $horaStr (${agendamento.status})"
        }
    }
}
