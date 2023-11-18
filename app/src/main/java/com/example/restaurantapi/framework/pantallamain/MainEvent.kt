package com.example.restaurantapi.framework.pantallamain


import com.example.restaurantapi.domain.modelo.Customer

sealed class MainEvent {


    class DeletePersonasSeleccionadas() : MainEvent()
    class DeletePersona(val persona:Customer) : MainEvent()
    class SeleccionaPersona(val persona: Customer) : MainEvent()
    class InsertPersona(val persona: Customer) : MainEvent()
    class GetPersonaPorId(val id: Int) : MainEvent()

    class GetPersonaFiltradas(val filtro: String) : MainEvent()
    object GetPersonas : MainEvent()
    object ErrorVisto : MainEvent()

    object StartSelectMode: MainEvent()
    object ResetSelectMode: MainEvent()
}
