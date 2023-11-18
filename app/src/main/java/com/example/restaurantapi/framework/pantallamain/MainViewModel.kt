package com.example.recyclerviewenhanced.framework.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.restaurantapi.data.RepositoryCustomers
import com.example.restaurantapi.domain.modelo.Customer
import com.example.restaurantapi.framework.pantallamain.MainEvent
import com.example.restaurantapi.framework.pantallamain.MainState
import com.example.restaurantapi.utils.NetworkResultt

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(private val customerRepositoryCustomers: RepositoryCustomers) : ViewModel() {


    private val listaPersonas = mutableListOf<Customer>()



    private val _error = MutableLiveData<String>()
    val error: LiveData<String> get() = _error

    private val _sharedFlow = MutableSharedFlow<String>()
    val sharedFlow = _sharedFlow.asSharedFlow()

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
            is MainEvent.InsertPersona -> {

                getPersonas()
            }
            MainEvent.ErrorVisto -> _uiState.value = _uiState.value?.copy(error = null)
            is MainEvent.GetPersonaPorId -> {
            }

            is MainEvent.DeletePersonasSeleccionadas -> {
                _uiState.value?.let {
                    deletePersona(it.personasSeleccionadas)
                    resetSelectMode()
                }
            }
            is MainEvent.SeleccionaPersona -> seleccionaPersona(event.persona)
            is MainEvent.GetPersonaFiltradas -> getPersonas(event.filtro)
            is MainEvent.DeletePersona -> {
                deletePersona(event.persona)
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
            val result = customerRepositoryCustomers.getCustomers()

            when (result) {
                is NetworkResultt.Error<*> -> _error.value = result.message ?: "mal"
                is NetworkResultt.Loading<*> -> TODO()
                is NetworkResultt.Success<*> -> {
                    // Asegúrate de que los datos son de tipo List<Customer>
                    if (result.data is List<*>) {
                        listaPersonas.clear()
                        listaPersonas.addAll(result.data as Collection<Customer>)
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
//            _sharedFlow.emit("error")
            listaPersonas.removeAll(personas)
            selectedPersonas.removeAll(personas)
            _uiState.value = _uiState.value?.copy(personasSeleccionadas = selectedPersonas.toList())
            getPersonas()
        }

    }

    private fun deletePersona(persona: Customer) {

        deletePersona(listOf(persona))

    }


    private fun seleccionaPersona(persona: Customer) {

        if (isSelected(persona)) {
            selectedPersonas.remove(persona)

        }
        else {
            selectedPersonas.add(persona)
        }
        _uiState.value = _uiState.value?.copy(personasSeleccionadas = selectedPersonas)

    }

    private fun isSelected(persona: Customer): Boolean {
        return selectedPersonas.contains(persona)
    }


}