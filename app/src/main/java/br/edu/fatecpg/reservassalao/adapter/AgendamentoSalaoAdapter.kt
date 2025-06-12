package br.edu.fatecpg.reservassalao.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.reservassalao.databinding.ItemAgendamentoBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import java.text.SimpleDateFormat
import java.util.*

class AgendamentoSalaoAdapter(
    private val agendamentos: List<Triple<Agendamento, String, String>>
) : RecyclerView.Adapter<AgendamentoSalaoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAgendamentoBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAgendamentoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = agendamentos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (agendamento, nomeCliente, nomeServico) = agendamentos[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.binding.txtSalao.text = "Cliente: $nomeCliente"
        holder.binding.txtServico.text = "Serviço: $nomeServico"
        holder.binding.txtData.text = "Data: ${sdf.format(agendamento.data)}"
        holder.binding.txtHora.text = "Hora: ${agendamento.hora}"
        holder.binding.txtStatus.text = "Status: ${agendamento.status}"
    }
}
