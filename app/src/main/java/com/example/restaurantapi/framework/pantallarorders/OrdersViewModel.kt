package com.example.restaurantapi.framework.pantallarorders

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapi.domain.modelo.Order
import com.example.restaurantapi.domain.usecases.AddOrderUseCase
import com.example.restaurantapi.domain.usecases.DeleteOrderUseCase
import com.example.restaurantapi.domain.usecases.GetAllOrdersUseCase
import com.example.restaurantapi.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("SuspiciousIndentation")
@HiltViewModel
class OrdersViewModel @Inject constructor(
    private val addOrderUseCase: AddOrderUseCase,
    private val getAllOrdersUseCase: GetAllOrdersUseCase,
    private val deleteOrderUseCase: DeleteOrderUseCase
) : ViewModel() {


    private val listaPersonas = mutableListOf<Order>()


    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private var selectedPersonas = mutableListOf<Order>()


    private val _uiState = MutableLiveData(OrdersState())
    val uiState: LiveData<OrdersState> get() = _uiState


    init {
        _uiState.value = OrdersState(
            personas = emptyList(),
            personasSeleccionadas = emptyList(),
            selectMode = false
        )
    }

    fun handleEvent(event: OrderEvent) {
        when (event) {
            is OrderEvent.GetOrders -> {
                getOrders(event.id)
            }
            is OrderEvent.AddOrder -> {
                addOrder(event.order)
            }
            OrderEvent.ErrorVisto -> _uiState.value = _uiState.value?.copy(error = null)
            is OrderEvent.DeletePersonasSeleccionadas -> {
                deleteOrder(uiState.value?.personasSeleccionadas ?: emptyList())
                resetSelectMode()
            }
            is OrderEvent.SelectOrder -> seleccionaOrder(event.order)
            is OrderEvent.GetPersonaFiltradas -> TODO()
            is OrderEvent.DeletePersona -> {
                deleteOrder(event.order)
            }
            OrderEvent.ResetSelectMode -> resetSelectMode()
            OrderEvent.StartSelectMode -> _uiState.value = _uiState.value?.copy(selectMode = true)
        }
    }

    private fun resetSelectMode() {
        selectedPersonas.clear()
        _uiState.value =
            _uiState.value?.copy(selectMode = false, personasSeleccionadas = selectedPersonas)

    }


    private fun addOrder(order: Order) {
        viewModelScope.launch {
            when (val result = addOrderUseCase.invoke(order)) {
                is NetworkResult.Error<*> -> _error.value = result.message ?: "Error"
                is NetworkResult.Loading<*> -> TODO()
                is NetworkResult.Success<*> -> {
                    if (result.data is Order) {
                        getOrders(order.customerId)
                    }
                }
            }
        }
    }

    private fun getOrders(id: Int) {
        viewModelScope.launch {
            val result = getAllOrdersUseCase.invoke(id)
            listaPersonas.clear()
            listaPersonas.addAll(result)
            _uiState.value = _uiState.value?.copy(personas = listaPersonas)
        }

    }


    private fun deleteOrder(orders: List<Order>) {
        viewModelScope.launch {
            val copiaPersonas = orders.toList()
            val personasParaEliminar = mutableListOf<Order>()
            var isSuccessful = true
            for (persona in copiaPersonas) {
                val result = deleteOrderUseCase.invoke(persona)
                if (result is NetworkResult.Error<*>) {
                    _error.value = "Error al borrar"
                    isSuccessful = false

                } else {
                    personasParaEliminar.add(persona)
                }
            }
            if (isSuccessful) {
                listaPersonas.removeAll(personasParaEliminar)
                selectedPersonas.removeAll(personasParaEliminar)
                _uiState.value =
                    _uiState.value?.copy(
                        personasSeleccionadas = selectedPersonas.toList(),
                        personas = listaPersonas
                    )
            }
        }
    }

    private fun deleteOrder(persona: Order) {

        deleteOrder(listOf(persona))

    }


    private fun seleccionaOrder(order: Order) {
        if (isSelected(order)) {
            selectedPersonas.remove(order)
        } else {
            selectedPersonas.add(order)
        }
        _uiState.value = _uiState.value?.copy(personasSeleccionadas = selectedPersonas)
    }

    private fun isSelected(persona: Order): Boolean {
        return selectedPersonas.contains(persona)
    }


}