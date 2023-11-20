package com.example.restaurantapi.framework.pantallamain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode


import androidx.recyclerview.widget.ItemTouchHelper
import com.example.restaurantapi.R
import com.example.restaurantapi.databinding.ActivityMainBinding
import com.example.restaurantapi.domain.modelo.Customer

import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var primeraVez: Boolean = false
    private lateinit var customAdapter: CustomerAdapter
    private val viewModel: MainViewModel by viewModels()
    private val callback by lazy {
        configContextBar()
    }
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        primeraVez = true
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //manejar los gestos de deslizamiento y longclick enviandolos al viewmodel
        customAdapter = CustomerAdapter(this,
            object : CustomerAdapter.PersonaActions {
                override fun onDelete(customer: Customer) =
                    viewModel.handleEvent(MainEvent.DeletePersona(customer))

                override fun onStartSelectMode(customer: Customer) {
                    viewModel.handleEvent(MainEvent.StartSelectMode)
                    viewModel.handleEvent(MainEvent.SeleccionaPersona(customer))
                }

                override fun itemHasClicked(customer: Customer) =
                    viewModel.handleEvent(MainEvent.SeleccionaPersona(customer))
            })

        //configurar el recyclerview
        binding.rvPersonas.adapter = customAdapter
        val touchHelper = ItemTouchHelper(customAdapter.swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvPersonas)

        //configurar el boton de recargar personas


        //observar los cambios en el estado del viewmodel
        viewModel.uiState.observe(this) { state ->
            //si la lista de personas cambia, se actualiza el adapter y se cambia en pantalla
            state.personas.let {
                if (it.isNotEmpty()) {
                    customAdapter.submitList(it)
                }
            }

            //si la lista de personas seleccionadas cambia, se actualiza el adapter y se cambia en pantalla junto al titulo del actionmode
            state.personasSeleccionadas.let {
                if (it.isNotEmpty()) {
                    customAdapter.setSelectedItems(it)
                    actionMode?.title = "${it.size} selected"
                } else {
                    customAdapter.resetSelectMode()
                    primeraVez = true
                    actionMode?.finish()
                }
            }

            //si el modo seleccion cambia, se llama al adapter para que cambie el modo seleccion
            state.selectMode.let { seleccionado ->
                if (seleccionado) {
                    if (primeraVez) {
                        customAdapter.startSelectMode()
                        //si es la primera vez que se entra en modo seleccion, se crea el actionmode, que es el menu contextual
                        startSupportActionMode(callback)?.let {
                            actionMode = it;
                        }
                        primeraVez = false
                    }
                    else{
                        customAdapter.startSelectMode()
                    }
                } else {//si se sale del modo seleccion, se llama al adapter para que cambie el modo seleccion
                    customAdapter.resetSelectMode()
                    primeraVez = true
                    actionMode?.finish()//se cierra el actionmode
                }
            }

            //si hay un error, se muestra en pantalla y se resetea el estado del viewmodel
            state.error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        configAppBar();
    }

    private fun configContextBar() = object : ActionMode.Callback {
        // esto es para el menu contextual que se muestra cuando se pulsa un elemento de la lista durante un tiempo
        // largo. Se muestra en la parte superior de la pantalla y tiene tres opciones: favoritos, buscar y mas.
        // La opcion favoritos no hace nada, la opcion buscar no hace nada y la opcion mas borra los elementos
        // seleccionados de la lista y sale del modo seleccion
        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            menuInflater.inflate(R.menu.context_bar, menu)
            return true
        }

        override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            return false
        }

        override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
            return when (item?.itemId) {
                R.id.favorite -> {
                    // Handle share icon press
                    true
                }

                R.id.search -> {
                    // Handle delete icon press
                    true
                }

                R.id.more -> {
                    viewModel.handleEvent(MainEvent.DeletePersonasSeleccionadas())
                    true
                }

                else -> false
            }
        }

        override fun onDestroyActionMode(mode: ActionMode?) {
            viewModel.handleEvent(MainEvent.ResetSelectMode)
        }
    }

    private fun configAppBar() {
        //configurar la appbar para que tenga un menu de busqueda y un menu de opciones en la esquina
        // superior derecha de la pantalla y un menu de navegacion en la esquina superior izquierda de
        // la pantalla que no hace nada al pulsarlo (no hay navegacion) y que se pueda cerrar el menu
        // de navegacion pulsando en cualquier parte de la pantalla que no sea el menu de navegacion
        // o el menu de opciones o el menu de busqueda o el actionmode

    }

}