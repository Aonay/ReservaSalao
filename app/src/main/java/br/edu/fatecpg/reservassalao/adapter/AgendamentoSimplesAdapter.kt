package br.edu.fatecpg.reservassalao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.reservassalao.databinding.ItemAgendamentoBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import java.text.SimpleDateFormat
import java.util.*

class AgendamentoSimplesAdapter(private val agendamentos: List<Agendamento>) :
    RecyclerView.Adapter<AgendamentoSimplesAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemAgendamentoBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAgendamentoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = agendamentos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = agendamentos[position]
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        holder.binding.txtData.text = "Data: ${sdf.format(item.data)}"
        holder.binding.txtHora.text = "Hora: ${item.hora}"
        holder.binding.txtStatus.text = "Status: ${item.status}"

        // Como não temos cliente e serviço aqui, escondemos esses campos
        holder.binding.txtCliente.visibility = View.GONE
        holder.binding.txtServico.visibility = View.GONE
    }
}