package com.example.restaurantapi.framework.pantallamain

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.view.ActionMode
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import com.example.hiltmenu.ui.main.CustomerAdapter
import com.example.recyclerviewenhanced.framework.main.MainViewModel
import com.example.restaurantapi.R
import com.example.restaurantapi.databinding.ActivityMainBinding
import com.example.restaurantapi.domain.modelo.Customer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private var primeraVez : Boolean = false

    private var anteriorState: MainState? = null

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
        customAdapter = CustomerAdapter(this,
            object : CustomerAdapter.PersonaActions {
                override fun onDelete(persona: Customer) = viewModel.handleEvent(MainEvent.DeletePersona(persona))

                override fun onStartSelectMode(persona: Customer) {
                    viewModel.handleEvent(MainEvent.StartSelectMode)
                    viewModel.handleEvent(MainEvent.SeleccionaPersona(persona))
                }

                override fun itemHasClicked(persona: Customer) {

                    viewModel.handleEvent(MainEvent.SeleccionaPersona(persona))
                }




            })
        binding.rvPersonas.adapter = customAdapter

        val touchHelper = ItemTouchHelper(customAdapter.swipeGesture)
        touchHelper.attachToRecyclerView(binding.rvPersonas)

        binding.button.setOnClickListener {
//            val cosas = listOf(Cosa("cosa1", 22))
//            println(personasAdapter.getSelectedItems().toString())
//            viewModel.insertPersonaWithCosas(Persona(0, "nombre", LocalDate.now(), cosas))
            viewModel.handleEvent(MainEvent.GetPersonas)
        }

        viewModel.uiState.observe(this) { state ->
            if (state.personas != anteriorState?.personas
                && state.personas.isNotEmpty())
                customAdapter.submitList(state.personas)

            if (state.personasSeleccionadas != anteriorState?.personasSeleccionadas) {
                customAdapter.setSelectedItems(state.personasSeleccionadas)
                actionMode?.title =
                    "${state.personasSeleccionadas.size} selected"
            }

            if (state.selectMode != anteriorState?.selectMode) {
                if (state.selectMode) {
                    customAdapter.startSelectMode()
                    if (primeraVez) {
                        startSupportActionMode(callback)?.let {
                            actionMode = it;
                        }
                        primeraVez = false
                    }

                } else {
                    customAdapter.resetSelectMode()
                    primeraVez = true
                    actionMode?.finish()
                }
            }
            state.error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                viewModel.handleEvent(MainEvent.ErrorVisto)
            }

            anteriorState = state
        }

        val context = this
        lifecycleScope.launch {
            viewModel.sharedFlow.collect(){ error->
                Toast.makeText(context, error, Toast.LENGTH_SHORT).show()
            }

        }

        //viewModel.handleEvent(MainEvent.GetPersonas)
        configAppBar();

    }


    private fun configContextBar() =
        object : ActionMode.Callback {

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
        binding.topAppBar.setNavigationOnClickListener {
            // Handle navigation icon press
        }


        val actionSearch = binding.topAppBar.menu.findItem(R.id.search).actionView as SearchView

        actionSearch.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {

                newText?.let {filtro ->
                    viewModel.handleEvent(MainEvent.GetPersonaFiltradas(filtro))
                }

                return false
            }


        })

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.favorite -> {
                    // Handle favorite icon press
                    true
                }
                R.id.search -> {
                    // Handle search icon press
                    true
                }
                R.id.more -> {
                    // Handle more item (inside overflow menu) press
                    true
                }
                else -> false
            }
        }
    }

}