package com.example.restaurantapi.framework.pantallamain

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapi.data.CustomerRepository
import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.domain.usecases.DeleteCustomerUseCase
import com.example.restaurantapi.domain.usecases.GetAllCustomersUseCase
import com.example.restaurantapi.utils.NetworkResult

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@SuppressLint("SuspiciousIndentation")
@HiltViewModel
class MainViewModel @Inject constructor(private val deleteCustomerUseCase: DeleteCustomerUseCase, private val getAllCustomersUseCase: GetAllCustomersUseCase) : ViewModel() {


    private val listaPersonas = mutableListOf<Customer>()
    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error
    private var selectedPersonas = mutableListOf<Customer>()
    private val _uiState = MutableLiveData(MainState())
    val uiState: LiveData<MainState> get() = _uiState


    init {

    _uiState.value = MainState(personas = emptyList(), personasSeleccionadas = emptyList(), selectMode = false)
        getPersonas()


    }

    fun handleEvent(event: MainEvent) {
        when (event) {
            MainEvent.GetPersonas -> {
                getPersonas()
            }
            MainEvent.ErrorVisto -> _uiState.value = _uiState.value?.copy(error = null)


            is MainEvent.DeletePersonasSeleccionadas -> {
                deletePersona(uiState.value?.personasSeleccionadas ?: emptyList())
                resetSelectMode()
            }
            is MainEvent.SeleccionaPersona -> seleccionaPersona(event.customer)
            is MainEvent.GetPersonaFiltradas -> getPersonas(event.filtro)
            is MainEvent.DeletePersona -> {
                deletePersona(event.customer)
            }

            MainEvent.ResetSelectMode -> resetSelectMode()

            MainEvent.StartSelectMode -> _uiState.value = _uiState.value?.copy(selectMode = true)
        }
    }

    private fun resetSelectMode()
    {
        selectedPersonas.clear()
        _uiState.value = _uiState.value?.copy(selectMode = false, personasSeleccionadas = selectedPersonas)

    }

    private fun getPersonas() {
        viewModelScope.launch {
            val result = getAllCustomersUseCase.invoke()

            when (result) {
                is NetworkResult.Error<*> -> _error.value = result.message ?: "Error"
                is NetworkResult.Loading<*> -> TODO()
                is NetworkResult.Success<*> -> {
                    if (result.data is List<*>) {
                        listaPersonas.clear()
                        listaPersonas.addAll(result.data as List<Customer>)
                    }
                }
            }

            _uiState.value = _uiState.value?.copy(personas = listaPersonas)
        }
    }



    private fun getPersonas(filtro: String) {

        viewModelScope.launch {

            _uiState.value =  _uiState.value?.copy (
                personas = listaPersonas.filter { it.name.startsWith(filtro) }.toList())


        }

    }



    private fun deletePersona(personas: List<Customer>) {
        viewModelScope.launch {
            // Hacemos una copia de la lista original para iterar sobre ella
            val copiaPersonas = personas.toList()

            // Lista para rastrear los elementos que se eliminarán.
            val personasParaEliminar = mutableListOf<Customer>()

            // Bucle que intenta borrar cada persona de la copia y si hay error, rompe el bucle.
            var isSuccessful = true
            for (persona in copiaPersonas) {
                val result = deleteCustomerUseCase.invoke(persona)
                if (result is NetworkResult.Error<*>) {
                    _error.value = "Error al borrar"
                    isSuccessful = false
                    break // Sale del bucle si hay un error.
                } else {
                    personasParaEliminar.add(persona) // Agrega a la lista temporal si el borrado fue exitoso.
                }
            }

            // Si todas las personas se borraron exitosamente, actualiza la lista original.
            if (isSuccessful) {
                listaPersonas.removeAll(personasParaEliminar)
                selectedPersonas.removeAll(personasParaEliminar)
                _uiState.value =
                    _uiState.value?.copy(personasSeleccionadas = selectedPersonas.toList())
            }

            // Vuelve a cargar la lista de personas, independientemente del resultado del borrado.
            getPersonas()
        }
    }

    private fun deletePersona(persona: Customer) {

        deletePersona(listOf(persona))

    }


    private fun seleccionaPersona(persona: Customer) {

            if (isSelected(persona)) {
                selectedPersonas.remove(persona)
            } else {
                selectedPersonas.add(persona)
            }
            _uiState.value = _uiState.value?.copy(personasSeleccionadas = selectedPersonas)

    }

    private fun isSelected(persona: Customer): Boolean {
        return selectedPersonas.contains(persona)
    }


}