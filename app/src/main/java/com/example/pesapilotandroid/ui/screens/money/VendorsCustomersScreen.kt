package com.example.pesapilotandroid.ui.screens.money

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.pesapilotandroid.data.model.Contact
import com.example.pesapilotandroid.data.model.ContactType
import com.example.pesapilotandroid.ui.components.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VendorsCustomersScreen(
    navController: NavController,
    viewModel: VendorsCustomersViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }
    var showAddDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            PesaPilotTopBar(
                title = "Vendors & Customers",
                onBackClick = { navController.popBackStack() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true }
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Contact")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Customers") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Vendors") }
                )
            }

            val filteredContacts = if (selectedTab == 0) {
                uiState.contacts.filter { it.contactType == ContactType.CUSTOMER.value }
            } else {
                uiState.contacts.filter { it.contactType == ContactType.VENDOR.value }
            }

            when {
                uiState.isLoading -> LoadingScreen()
                filteredContacts.isEmpty() -> {
                    EmptyStateScreen(
                        title = if (selectedTab == 0) "No Customers" else "No Vendors",
                        message = "Add your business contacts to manage relationships",
                        icon = Icons.Default.People
                    )
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(filteredContacts) { contact ->
                            ContactCard(
                                contact = contact,
                                onDelete = { viewModel.deleteContact(contact.id) }
                            )
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddContactDialog(
            contactType = if (selectedTab == 0) ContactType.CUSTOMER.value else ContactType.VENDOR.value,
            onDismiss = { showAddDialog = false },
            onAdd = { name, type, phone, email, address ->
                viewModel.createContact(name, type, phone, email, address)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ContactCard(
    contact: Contact,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = if (contact.contactType == ContactType.CUSTOMER.value)
                    MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.secondaryContainer,
                modifier = Modifier.size(48.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        if (contact.contactType == ContactType.CUSTOMER.value)
                            Icons.Default.Person
                        else
                            Icons.Default.Store,
                        contentDescription = null,
                        tint = if (contact.contactType == ContactType.CUSTOMER.value)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.name,
                    style = MaterialTheme.typography.titleMedium
                )
                contact.phone?.let { phone ->
                    Text(
                        text = phone,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                contact.email?.let { email ->
                    Text(
                        text = email,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun AddContactDialog(
    contactType: String,
    onDismiss: () -> Unit,
    onAdd: (String, String, String?, String?, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                if (contactType == ContactType.CUSTOMER.value) 
                    "Add Customer" 
                else 
                    "Add Vendor"
            ) 
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PesaPilotTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = "Name *"
                )
                PesaPilotTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    label = "Phone"
                )
                PesaPilotTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = "Email"
                )
                PesaPilotTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = "Address"
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onAdd(
                        name,
                        contactType,
                        phone.ifBlank { null },
                        email.ifBlank { null },
                        address.ifBlank { null }
                    )
                },
                enabled = name.isNotBlank()
            ) {
                Text("Add")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
