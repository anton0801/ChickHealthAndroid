package com.helathchickapp.chickhealth

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.helathchickapp.chickhealth.ui.theme.Analytics
import com.helathchickapp.chickhealth.ui.theme.JournalMedical
import com.helathchickapp.chickhealth.ui.theme.Pets
import kotlinx.coroutines.delay

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChickHealthPlusTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun ChickHealthPlusTheme(content: @Composable () -> Unit) {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    val gradient = when {
        hour < 6 || hour > 20 -> listOf(Color(0xFF1B3B35), Color(0xFF0D221E))
        hour < 12 -> listOf(Color(0xFF1B3B35), Color(0xFFFFD93D))
        else -> listOf(Color(0xFF1B3B35), Color(0xFFFF6B6B))
    }

    MaterialTheme(
        colorScheme = darkColorScheme(
            primary = Color(0xFF1B3B35),
            secondary = Color(0xFFFFD93D),
            tertiary = Color(0xFFFF6B6B),
            background = Color(0xFF1B3B35),
            surface = Color(0x80FFF8E1)
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(gradient)
                )
        ) {
            content()
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            PremiumBottomNavigationBar(navController, currentRoute)
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues)
        ) {
            composable("home") { PremiumHomeScreen() }
            composable("birds") { PremiumBirdsScreen() }
            composable("treatment") { PremiumTreatmentScreen() }
            composable("statistics") { PremiumStatisticsScreen() }
            composable("settings") { PremiumSettingsScreen() }
        }
    }
}

@Composable
fun PremiumBottomNavigationBar(navController: NavHostController, currentRoute: String?) {
    val items = listOf(
        NavItem("home", Icons.Default.Home, "Home"),
        NavItem("birds", Icons.Default.List, "Birds"),
        NavItem("treatment", JournalMedical, "Treatment"),
        NavItem("statistics", Analytics, "Statistics"),
        NavItem("settings", Icons.Default.Settings, "Settings")
    )

    NavigationBar(
        containerColor = Color(0xFF1B3B35).copy(alpha = 0.9f),
        contentColor = Color.White
    ) {
        items.forEach { item ->
            val isSelected = currentRoute == item.route
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label,
                        modifier = Modifier.scale(if (isSelected) 1.2f else 1f)
                    )
                },
                label = {
                    Text(
                        item.label,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                },
                selected = isSelected,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = Color(0xFFFFD93D),
                    selectedTextColor = Color(0xFFFFD93D),
                    indicatorColor = Color(0xFFFFD93D).copy(alpha = 0.2f)
                )
            )
        }
    }
}

data class NavItem(val route: String, val icon: androidx.compose.ui.graphics.vector.ImageVector, val label: String)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumHomeScreen() {
    val context = LocalContext.current
    var showGreeting by remember { mutableStateOf(false) }
    var showAddDialog by remember { mutableStateOf(false) }
    var hasBirds by remember { mutableStateOf(loadBirds(context).isNotEmpty()) } // Загружаем из prefs

    LaunchedEffect(Unit) {
        delay(500)
        showGreeting = true
    }

    if (showAddDialog) {
        AddEventDialog(
            onDismiss = { showAddDialog = false },
            onAddBird = { bird ->
                val currentBirds = loadBirds(context).toMutableList()
                currentBirds.add(bird)
                saveBirds(context, currentBirds)
                hasBirds = true
            },
            onAddTreatment = { treatment ->
                val currentTreatments = loadTreatments(context).toMutableList()
                currentTreatments.add(treatment)
                saveTreatments(context, currentTreatments)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(28.dp)
    ) {
        // ✅ ПУСТОЕ СОСТОЯНИЕ ИЛИ АНИМАЦИЯ
        AnimatedVisibility(
            visible = showGreeting,
            enter = slideInVertically() + scaleIn(spring(dampingRatio = 0.8f))
        ) {
            if (!hasBirds) {
                EmptyStateCard()
            } else {
                PremiumGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            Pets,
                            "",
                            tint = Color(0xFFFFD93D),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            "Flock Health Stable 🕊️✨",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        if (hasBirds) {
            // 🌈 HEALTH PILLS - ТОЛЬКО ЕСЛИ ЕСТЬ ПТИЦЫ
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                HealthPillPremium("🟢 Healthy", 85, Color(0xFF4CAF50))
                HealthPillPremium("🟡 Watching", 12, Color(0xFFFFCA28))
                HealthPillPremium("🔴 Sick", 3, Color(0xFFF44336))
            }

            // 💎 REMINDERS - ТОЛЬКО ЕСЛИ ЕСТЬ ПТИЦЫ
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Icon(Icons.Default.Notifications, "", tint = Color(0xFFFFD93D), modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Today's Reminders ✨",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                    ReminderItemPremium("🦠 Vaccination - Bella", Color(0xFF4CAF50))
                    ReminderItemPremium("💊 Antibiotics - Ducky", Color(0xFF2196F3))
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // ✅ РАБОТАЮЩАЯ КНОПКА +
        FloatingActionButton(
            onClick = { showAddDialog = true },
            containerColor = Color(0xFFFFD93D),
            modifier = Modifier
                .size(80.dp)
                .shadow(16.dp, CircleShape)
        ) {
            Text(
                "+",
                fontSize = 32.sp,
                color = Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun EmptyStateCard() {
    PremiumGlassCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 40.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                Pets,
                "Add your first bird",
                tint = Color(0xFFFFD93D),
                modifier = Modifier.size(80.dp)
            )
            Text(
                "Welcome to Chick Health+ 🐔",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                "Tap + to add your first bird\nand start tracking health ✨",
                textAlign = TextAlign.Center,
                color = Color.White.copy(alpha = 0.8f),
                fontSize = 16.sp
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    onDismiss: () -> Unit,
    onAddBird: (BirdData) -> Unit,
    onAddTreatment: (TreatmentData) -> Unit
) {
    var selectedType by remember { mutableStateOf("Bird") }
    var name by remember { mutableStateOf("") } // Для Bird
    var breed by remember { mutableStateOf("") } // Для Bird
    var age by remember { mutableStateOf("") } // Для Bird
    var status by remember { mutableStateOf("healthy") } // Для Bird
    var photoUrl by remember { mutableStateOf("https://via.placeholder.com/80") } // Для Bird

    var date by remember { mutableStateOf("") } // Для Treatment
    var type by remember { mutableStateOf("") } // Для Treatment
    var bird by remember { mutableStateOf("") } // Для Treatment
    var action by remember { mutableStateOf("") } // Для Treatment
    var drug by remember { mutableStateOf("") } // Для Treatment
    var dose by remember { mutableStateOf("") } // Для Treatment

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                // ✅ РАБОТАЮЩИЙ SWITCH ДЛЯ ТИПА
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("Add Bird", fontSize = 16.sp)
                    Spacer(modifier = Modifier.weight(1f))
                    Switch(
                        checked = selectedType == "Bird",
                        onCheckedChange = { selectedType = if (it) "Bird" else "Treatment" }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Add Treatment", fontSize = 16.sp)
                }

                if (selectedType == "Bird") {
                    OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = breed, onValueChange = { breed = it }, label = { Text("Breed") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = age, onValueChange = { age = it }, label = { Text("Age") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = status, onValueChange = { status = it }, label = { Text("Status") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = photoUrl, onValueChange = { photoUrl = it }, label = { Text("Photo URL") }, modifier = Modifier.fillMaxWidth())
                } else {
                    OutlinedTextField(value = date, onValueChange = { date = it }, label = { Text("Date") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = type, onValueChange = { type = it }, label = { Text("Type") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = bird, onValueChange = { bird = it }, label = { Text("Bird") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = action, onValueChange = { action = it }, label = { Text("Action") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = drug, onValueChange = { drug = it }, label = { Text("Drug") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = dose, onValueChange = { dose = it }, label = { Text("Dose") }, modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (selectedType == "Bird" && name.isNotBlank()) {
                    val birdData = BirdData(
                        name = name,
                        breed = breed,
                        age = age.toIntOrNull() ?: 0,
                        status = status,
                        photoUrl = photoUrl
                    )
                    onAddBird(birdData)
                } else if (selectedType == "Treatment" && date.isNotBlank() && bird.isNotBlank()) {
                    val treatmentData = TreatmentData(
                        date = date,
                        type = type,
                        bird = bird,
                        action = action,
                        drug = drug,
                        dose = dose
                    )
                    onAddTreatment(treatmentData)
                }
                onDismiss()
            }) { Text("Add") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
fun PremiumBirdsScreen() {
    val context = LocalContext.current
    var birds by remember { mutableStateOf(loadBirds(context)) }
    var showAddBird by remember { mutableStateOf(false) }

    if (showAddBird) {
        AddBirdDialog(onDismiss = { showAddBird = false }, onAdd = { bird ->
            birds = birds + bird
            saveBirds(context, birds)
            showAddBird = false
        })
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // 🔍 SEARCH BAR
        PremiumGlassCard(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Search, "", tint = Color(0xFFFFD93D), modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    "Search your birds 🪶",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        if (birds.isEmpty()) {
            // ✅ ПУСТОЕ СОСТОЯНИЕ
            EmptyBirdsState(onAddBird = { showAddBird = true })
        } else {
            LazyColumn(
                modifier = Modifier.padding(horizontal = 20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(birds) { bird ->
                    PremiumBirdCard(bird)
                }
            }
        }
    }
}

@Composable
fun EmptyBirdsState(onAddBird: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Icon(Pets, "", tint = Color(0xFFFFD93D), modifier = Modifier.size(100.dp))
            Text("No birds yet 🐣", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Tap + to add your first feathered friend", color = Color.White.copy(alpha = 0.8f))
            Button(
                onClick = onAddBird,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD93D))
            ) {
                Text("Add Bird", color = Color.Black)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBirdDialog(onDismiss: () -> Unit, onAdd: (BirdData) -> Unit) {
    var name by remember { mutableStateOf("") }
    var breed by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("healthy") }
    var photoUrl by remember { mutableStateOf("https://via.placeholder.com/80") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Bird", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = breed,
                    onValueChange = { breed = it },
                    label = { Text("Breed") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    label = { Text("Age") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = status,
                    onValueChange = { status = it },
                    label = { Text("Status") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = photoUrl,
                    onValueChange = { photoUrl = it },
                    label = { Text("Photo URL") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (name.isNotBlank()) {
                    onAdd(BirdData(name, breed, age.toIntOrNull() ?: 0, status, photoUrl))
                }
                onDismiss()
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun PremiumTreatmentScreen() {
    val context = LocalContext.current
    var treatments by remember { mutableStateOf(loadTreatments(context)) }
    var showAddTreatment by remember { mutableStateOf(false) }

    if (showAddTreatment) {
        AddTreatmentDialog(onDismiss = { showAddTreatment = false }, onAdd = { treatment ->
            treatments = treatments + treatment
            saveTreatments(context, treatments)
            showAddTreatment = false
        })
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(JournalMedical, "", tint = Color(0xFFFFD93D), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Treatment Log 📋",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        Button(
            onClick = { showAddTreatment = true },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD93D))
        ) {
            Text("Add Treatment", color = Color.Black)
        }

        if (treatments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No treatments yet 💊", color = Color.White.copy(alpha = 0.8f))
            }
        } else {
            PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(treatments) { treatment ->
                        TreatmentItemPremium(
                            treatment.date, treatment.type, treatment.bird,
                            treatment.action, treatment.drug, treatment.dose
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTreatmentDialog(onDismiss: () -> Unit, onAdd: (TreatmentData) -> Unit) {
    var date by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("") }
    var bird by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var drug by remember { mutableStateOf("") }
    var dose by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add New Treatment", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = date,
                    onValueChange = { date = it },
                    label = { Text("Date") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = type,
                    onValueChange = { type = it },
                    label = { Text("Type") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = bird,
                    onValueChange = { bird = it },
                    label = { Text("Bird") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = action,
                    onValueChange = { action = it },
                    label = { Text("Action") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = drug,
                    onValueChange = { drug = it },
                    label = { Text("Drug") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = dose,
                    onValueChange = { dose = it },
                    label = { Text("Dose") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                if (date.isNotBlank() && bird.isNotBlank()) {
                    onAdd(TreatmentData(date, type, bird, action, drug, dose))
                }
                onDismiss()
            }) { Text("Add") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
fun PremiumStatisticsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        Text(
            "Flock Statistics 📊",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        // ✅ КРУГАЯЯ ДИАГРАММА С ПРОЦЕНТАМИ!
        PremiumGlassCard(
            modifier = Modifier
                .size(280.dp)
                .border(3.dp, Color(0xFFFFD93D), CircleShape)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier.size(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Центр с процентом
                    Text(
                        "85%",
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4CAF50)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text("Healthy Birds 🟢", fontSize = 20.sp, color = Color.Black)
                Text("12 birds total", fontSize = 14.sp, color = Color.Gray)
            }
        }

        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Detailed charts coming soon! ✨",
                color = Color.Black,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun PremiumSettingsScreen() {
    val context = LocalContext.current
    var darkTheme by remember { mutableStateOf(loadBoolean(context, "dark_theme", true)) }
    var notifications by remember { mutableStateOf(loadBoolean(context, "notifications", true)) }
    var exportData by remember { mutableStateOf(loadBoolean(context, "export_data", false)) }
    var privacy by remember { mutableStateOf(loadBoolean(context, "privacy", true)) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.Settings, "", tint = Color(0xFFFFD93D), modifier = Modifier.size(32.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                "Settings ⚙️",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        PremiumGlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                SettingItemPremium("🌙 Dark Theme", darkTheme) {
                    darkTheme = it
                    saveBoolean(context, "dark_theme", it)
                }
                SettingItemPremium("🔔 Notifications", notifications) {
                    notifications = it
                    saveBoolean(context, "notifications", it)
                }
                SettingItemPremium("📤 Export Data", exportData) {
                    exportData = it
                    saveBoolean(context, "export_data", it)
                }
                SettingItemPremiumButton("Privacy Policy", "https://chickheallth.com/privacy-policy.html")
//                SettingItemPremium("🔒 Privacy", privacy) {
//                    privacy = it
//                    saveBoolean(context, "privacy", it)
//                }
            }
        }
    }
}

// 🔥 UI COMPONENTS (ОСТАВШИЕСЯ БЕЗ ИЗМЕНЕНИЙ)
@Composable
fun PremiumGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier
            .shadow(20.dp, RoundedCornerShape(24.dp)),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(24.dp)
    ) {
        Box(
            modifier = Modifier
                .padding(24.dp)
                .background(
                    brush = Brush.linearGradient(
                        0f to Color.Transparent,
                        0.8f to Color.White.copy(alpha = 0.1f)
                    )
                )
        ) {
            content()
        }
    }
}

@Composable
fun HealthPillPremium(text: String, percent: Int, color: Color) {

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(30.dp))
            .background(
                brush = Brush.linearGradient(
                    listOf(color.copy(alpha = 0.3f), color.copy(alpha = 0.1f))
                )
            )
            .padding(horizontal = 20.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text, color = color, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(12.dp))
        Text("$percent%", color = color, fontSize = 18.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun ReminderItemPremium(title: String, color: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(color.copy(alpha = 0.15f))
            .clickable { }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun PremiumBirdCard(bird: BirdData) {
    val statusColor = when (bird.status) {
        "healthy" -> Color(0xFF4CAF50)
        "watching" -> Color(0xFFFFCA28)
        else -> Color(0xFFF44336)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { }
            .shadow(12.dp, RoundedCornerShape(20.dp)),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = bird.photoUrl,
                contentDescription = bird.name,
                modifier = Modifier
                    .size(70.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFFFFD93D), CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.width(20.dp))
            Column {
                Text(bird.name, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("${bird.breed} • ${bird.age}y", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.weight(1f))
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(16.dp))
                    .background(statusColor.copy(alpha = 0.3f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    bird.status.replaceFirstChar { it.uppercase() },
                    color = statusColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun TreatmentItemPremium(date: String, type: String, bird: String, treatment: String, drug: String, dose: String) {
    Column(modifier = Modifier.padding(vertical = 8.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("$date", fontSize = 12.sp, color = Color.Gray)
            Spacer(modifier = Modifier.weight(1f))
            Icon(JournalMedical, "", tint = Color(0xFF2196F3), modifier = Modifier.size(16.dp))
        }
        Text(bird, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
        Text("$treatment - $drug ($dose)", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
        Spacer(modifier = Modifier.height(12.dp))
        Divider(color = Color.White.copy(alpha = 0.3f))
    }
}

@Composable
fun SettingItemPremium(title: String, isEnabled: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isEnabled) }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
        }
        Switch(
            checked = isEnabled,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFFFFD93D),
                checkedTrackColor = Color(0xFFFFD93D).copy(alpha = 0.5f)
            )
        )
    }
}

@Composable
fun SettingItemPremiumButton(
    title: String,
    url: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(intent)
            }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        // Стрелка вправо вместо Switch
        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
            contentDescription = "Open",
            tint = Color.White.copy(alpha = 0.7f),
            modifier = Modifier.size(24.dp)
        )
    }
}

// DATA CLASSES
data class BirdData(
    val name: String,
    val breed: String,
    val age: Int,
    val status: String,
    val photoUrl: String
)

data class TreatmentData(
    val date: String,
    val type: String,
    val bird: String,
    val action: String,
    val drug: String,
    val dose: String
)

// Функции для сохранения и загрузки данных с использованием SharedPreferences и Gson
private fun saveBirds(context: Context, birds: List<BirdData>) {
    val sharedPrefs = context.getSharedPreferences("chick_health_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = gson.toJson(birds)
    sharedPrefs.edit().putString("birds", json).apply()
}

private fun loadBirds(context: Context): List<BirdData> {
    val sharedPrefs = context.getSharedPreferences("chick_health_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPrefs.getString("birds", null) ?: return emptyList()
    val type = object : TypeToken<List<BirdData>>() {}.type
    return gson.fromJson(json, type)
}

private fun saveTreatments(context: Context, treatments: List<TreatmentData>) {
    val sharedPrefs = context.getSharedPreferences("chick_health_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = gson.toJson(treatments)
    sharedPrefs.edit().putString("treatments", json).apply()
}

private fun loadTreatments(context: Context): List<TreatmentData> {
    val sharedPrefs = context.getSharedPreferences("chick_health_prefs", Context.MODE_PRIVATE)
    val gson = Gson()
    val json = sharedPrefs.getString("treatments", null) ?: return emptyList()
    val type = object : TypeToken<List<TreatmentData>>() {}.type
    return gson.fromJson(json, type)
}

private fun saveBoolean(context: Context, key: String, value: Boolean) {
    val sharedPrefs = context.getSharedPreferences("chick_health_prefs", Context.MODE_PRIVATE)
    sharedPrefs.edit().putBoolean(key, value).apply()
}

private fun loadBoolean(context: Context, key: String, default: Boolean): Boolean {
    val sharedPrefs = context.getSharedPreferences("chick_health_prefs", Context.MODE_PRIVATE)
    return sharedPrefs.getBoolean(key, default)
}