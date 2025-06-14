package br.edu.fatecpg.reservassalao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.reservassalao.databinding.ItemAgendamentoBinding
import br.edu.fatecpg.reservassalao.model.AgendamentoCompleto
import java.text.SimpleDateFormat
import java.util.*

class AgendamentoAdapter(private val agendamentos: List<AgendamentoCompleto>) :
    RecyclerView.Adapter<AgendamentoAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAgendamentoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAgendamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = agendamentos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = agendamentos[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.binding.txtData.text = "Data: ${sdf.format(item.agendamento.data)}"
        holder.binding.txtHora.text = "Hora: ${item.agendamento.hora}"
        holder.binding.txtStatus.text = "Status: ${item.agendamento.status}"
        holder.binding.txtCliente.text = "Cliente: ${item.nomeCliente}"
        holder.binding.txtServico.text = "Serviço: ${item.nomeServico}"

        // Garantir que os campos estão visíveis aqui
        holder.binding.txtCliente.visibility = View.VISIBLE
        holder.binding.txtServico.visibility = View.VISIBLE
    }
}