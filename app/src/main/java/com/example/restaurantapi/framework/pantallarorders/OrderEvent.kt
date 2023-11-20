package com.example.restaurantapi.framework.pantallarorders


import com.example.restaurantapi.domain.modelo.Order

sealed class OrderEvent {

    class DeletePersonasSeleccionadas() : OrderEvent()
    class DeletePersona(val order:Order) : OrderEvent()
    class SelectOrder(val order: Order) : OrderEvent()

    class AddOrder(val order: Order) : OrderEvent()

    class GetPersonaFiltradas(val filtro: String) : OrderEvent()
    class GetOrders(val id: Int) : OrderEvent()

    object ErrorVisto : OrderEvent()

    object StartSelectMode: OrderEvent()
    object ResetSelectMode: OrderEvent()
}
