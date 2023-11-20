package com.example.restaurantapi.framework.pantallarorders

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.appcompat.view.ActionMode
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.restaurantapi.R
import com.example.restaurantapi.databinding.ActivityOrderBinding

import com.example.restaurantapi.domain.modelo.Order
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDate

@AndroidEntryPoint
class OrdersActivity: AppCompatActivity() {
    private lateinit var binding: ActivityOrderBinding
    private var primeraVez: Boolean = false

    private lateinit var customAdapter: OrderAdapter
    private val viewModel: OrdersViewModel by viewModels()
    private val callback by lazy {
        configContextBar()
    }
    private var actionMode: ActionMode? = null

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        primeraVez = true
        binding = ActivityOrderBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //manejar los gestos de deslizamiento y longclick enviandolos al viewmodel
        customAdapter = OrderAdapter(this,
            object : OrderAdapter.PersonaActions {
                override fun onDelete(order: Order) =
                    viewModel.handleEvent(OrderEvent.DeletePersona(order))

                override fun onStartSelectMode(order: Order) {
                    viewModel.handleEvent(OrderEvent.StartSelectMode)
                    viewModel.handleEvent(OrderEvent.SelectOrder(order))
                }

                override fun itemHasClicked(order: Order) =
                    viewModel.handleEvent(OrderEvent.SelectOrder(order))
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

        val intent= intent
        if (intent.hasExtra("EXTRA_CUSTOMER_ID")) {
            val id = intent.getIntExtra("EXTRA_CUSTOMER_ID", 0)
            viewModel.handleEvent(OrderEvent.GetOrders(id))
        }
        add()

    }
private fun add() {
    with(binding) {
        button.setOnClickListener() {
            val id = intent.getIntExtra("EXTRA_CUSTOMER_ID", 0)
            val tableid =textName.text.toString().toInt()
            val order = Order(0, id, LocalDate.now(), tableid)
            viewModel.handleEvent(OrderEvent.AddOrder(order))
        }
    }
}
    private fun configContextBar() = object : ActionMode.Callback {

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
                    viewModel.handleEvent(OrderEvent.DeletePersonasSeleccionadas())
                    true
                }

                else -> false
            }
        }
        override fun onDestroyActionMode(mode: ActionMode?) {
            viewModel.handleEvent(OrderEvent.ResetSelectMode)
        }
    }



}