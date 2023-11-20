package com.example.restaurantapi.framework.pantallarorders

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurantapi.R
import com.example.restaurantapi.databinding.ViewOrderBinding

import com.example.restaurantapi.domain.modelo.Order
import com.example.restaurantapi.framework.pantallamain.SwipeGesture


class OrderAdapter(
    val context: Context,
    val actions: PersonaActions
) :
    ListAdapter<Order, OrderAdapter.ItemViewholder>(DiffCallback()) {

    interface PersonaActions {
        fun onDelete(order: Order)
        fun onStartSelectMode(order: Order)
        fun itemHasClicked(order: Order)

    }

    private var selectedPersonas = mutableSetOf<Order>()

    fun startSelectMode() {
        selectedMode = true
        notifyDataSetChanged()
    }


    fun resetSelectMode() {
        selectedMode = false
        selectedPersonas.clear()
        notifyDataSetChanged()
    }

    fun setSelectedItems(personasSeleccionadas: List<Order>){
        selectedPersonas.clear()
        selectedPersonas.addAll(personasSeleccionadas)
    }

    private var selectedMode: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewholder {

        return ItemViewholder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.view_order, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewholder, position: Int) = with(holder) {
        val item = getItem(position)
        bind(item)

    }



    inner class ItemViewholder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val binding = ViewOrderBinding.bind(itemView)

        fun bind(item: Order) {

            itemView.setOnLongClickListener {
                if (!selectedMode) {

                    actions.onStartSelectMode(item)

                }
                true
            }
            with(binding) {
                selected.setOnClickListener {
                    if (selectedMode) {

                        if (binding.selected.isChecked ) {
                            item.isSelected = true
                            itemView.setBackgroundColor(Color.GREEN)
                            selectedPersonas.add(item)
                        } else {
                            item.isSelected = false
                            itemView.setBackgroundColor(Color.WHITE)
                            selectedPersonas.remove(item)


                        }
                        actions.itemHasClicked(item)
                    }
                }

                tvNombre.text = item.id.toString()
                if (selectedMode)
                    selected.visibility = View.VISIBLE
                else{
                    item.isSelected = false
                    selected.visibility = View.GONE
                }

                if (selectedPersonas.contains(item)) {
                    itemView.setBackgroundColor(Color.GREEN)
                    binding.selected.isChecked = true
                    //selected.visibility = View.VISIBLE
                } else {
                    itemView.setBackgroundColor(Color.WHITE)
                    binding.selected.isChecked = false
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }

    val swipeGesture = object : SwipeGesture(context) {

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val position = viewHolder.bindingAdapterPosition
                    selectedPersonas.remove(currentList[position])
                    actions.onDelete(currentList[position])
                    if (selectedMode)
                        actions.itemHasClicked(currentList[position])
                }
            }
        }

    }


}


