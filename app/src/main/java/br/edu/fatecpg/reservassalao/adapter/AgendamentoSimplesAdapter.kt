package br.edu.fatecpg.reservassalao.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import br.edu.fatecpg.reservassalao.databinding.ItemAgendamentoBinding
import br.edu.fatecpg.reservassalao.model.Agendamento
import br.edu.fatecpg.reservassalao.model.AgendamentoComSalao
import java.text.SimpleDateFormat
import java.util.*

class AgendamentoSimplesAdapter(private val agendamentos: List<AgendamentoComSalao>) :
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

        holder.binding.txtData.text = "Data: ${sdf.format(item.agendamento.data)}"
        holder.binding.txtHora.text = "Hora: ${item.agendamento.hora}"
        holder.binding.txtStatus.text = "Status: ${item.agendamento.status}"
        holder.binding.txtCliente.visibility = View.GONE // Oculta se não for necessário
        holder.binding.txtServico.visibility = View.GONE // Oculta se não for necessário
        holder.binding.txtSalao.text = "Salão: ${item.nomeSalao}" // Exibe o nome do salão
    }
}