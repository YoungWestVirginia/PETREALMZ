package ph.edu.auf.realmdiscussion.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import ph.edu.auf.realmdiscussion.components.ItemPet
import ph.edu.auf.realmdiscussion.database.realmodel.PetModel
import ph.edu.auf.realmdiscussion.viewmodels.PetViewModel



@Composable
fun PetScreen(petViewModel: PetViewModel = viewModel()) {
    val pets by petViewModel.pets.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var showAddPetDialog by remember { mutableStateOf(false) }
    var newPetName by remember { mutableStateOf("") }
    var newPetType by remember { mutableStateOf("") }
    var newPetAge by remember { mutableStateOf("") }

    // Default search term to show common pets like dogs and cats if no search is provided
    val defaultSearchTerms = listOf("dog", "cat")
    val searchQuery = if (searchText.isEmpty()) defaultSearchTerms.joinToString(" ") else searchText

    // Filter pets based on the search text or default search terms
    val filteredPets = pets.filter {
        it.name.contains(searchQuery, ignoreCase = true)
    }

    LaunchedEffect(petViewModel.showSnackbar) {
        petViewModel.showSnackbar.collect { message ->
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = message,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Short
                )
            }
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar for user input
            TextField(
                value = searchText,
                onValueChange = { searchText = it },  // Update search text as the user types
                label = { Text("Search pets...") },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(onClick = { searchText = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                keyboardActions = KeyboardActions(
                    onDone = {
                        // Additional logic for handling "Done" if needed
                    }
                ),
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Done
                )
            )

            // Button to show the Add Pet Dialog
            Button(
                onClick = { showAddPetDialog = true },
                modifier = Modifier.padding(8.dp)
            ) {
                Text("Add Pet")
            }

            // Display message if no pets match the search query
            if (filteredPets.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No pets found.")
                }
            } else {
                LazyColumn {
                    itemsIndexed(
                        items = filteredPets,
                        key = { _, item -> item.id }
                    ) { _, pet ->
                        ItemPet(
                            pet,
                            onRemove = petViewModel::deletePet,
                            onClick = { /* Define the action for clicking on the pet item */ }
                        )
                    }
                }

            }

            // Add Pet Dialog
            if (showAddPetDialog) {
                AlertDialog(
                    onDismissRequest = { showAddPetDialog = false },
                    title = { Text("Add a New Pet") },
                    text = {
                        Column {
                            TextField(
                                value = newPetName,
                                onValueChange = { newPetName = it },
                                label = { Text("Pet Name") }
                            )
                            TextField(
                                value = newPetType,
                                onValueChange = { newPetType = it },
                                label = { Text("Pet Type") }
                            )
                            TextField(
                                value = newPetAge,
                                onValueChange = { newPetAge = it },
                                label = { Text("Pet Age") },
                                keyboardOptions = KeyboardOptions.Default.copy(

                                )
                            )
                        }
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                // Validate input and create a new pet object
                                if (newPetName.isNotEmpty() && newPetType.isNotEmpty() && newPetAge.isNotEmpty()) {
                                    val newPet = PetModel(

                                    )
                                    petViewModel.addPet(newPet) // Add the new pet
                                    showAddPetDialog = false // Close the dialog
                                    newPetName = "" // Clear input fields
                                    newPetType = ""
                                    newPetAge = ""
                                }
                            }
                        ) {
                            Text("Add Pet")
                        }
                    },
                    dismissButton = {
                        Button(
                            onClick = {
                                showAddPetDialog = false
                                newPetName = ""
                                newPetType = ""
                                newPetAge = ""
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}





