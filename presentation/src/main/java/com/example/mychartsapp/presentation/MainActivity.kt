package com.example.mychartsapp.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mychartsapp.data.datasources.VersionLocalDataSource
import com.example.mychartsapp.data.repositories.VersionRepositoryImpl
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
import com.example.mychartsapp.presentation.ui.*
import com.example.mychartsapp.presentation.ui.theme.IbaChartsTheme
import com.example.mychartsapp.presentation.viewmodels.MainViewModel

/**
 * Главная активность приложения.
 * Использует Compose Navigation для управления экранами.
 */
class MainActivity : ComponentActivity() {
    
    // Создаем зависимости один раз для всей активности
    private val versionDataSource by lazy { VersionLocalDataSource(this) }
    private val versionRepository by lazy { VersionRepositoryImpl(versionDataSource) }
    private val getVersionUseCase by lazy { GetAppVersionUseCase(versionRepository) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            IbaChartsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Настройка Compose Navigation
                    AppNavigation(
                        getVersionUseCase = getVersionUseCase,
                        onExitApp = { finishAffinity() }
                    )
                }
            }
        }
    }
}

/**
 * Главный компонент навигации приложения.
 * Определяет все возможные экраны и переходы между ними.
 *
 * @param getVersionUseCase UseCase для получения версии приложения
 * @param onExitApp Callback для выхода из приложения
 */
@Composable
fun AppNavigation(
    getVersionUseCase: GetAppVersionUseCase,
    onExitApp: () -> Unit
) {
    val navController = rememberNavController()
    
    NavHost(
        navController = navController,
        startDestination = "main"
    ) {
        // Экран "Главное меню"
        composable("main") {
            MainScreen(
                getVersionUseCase = getVersionUseCase,
                onNormalChartClick = {
                    navController.navigate("normalChart")
                },
                onIbaChartClick = {
                    navController.navigate("ibaChart")
                },
                onSettingsClick = {
                    navController.navigate("settings")
                },
                onExitClick = onExitApp
            )
        }
        
        // Экран "Обычный график"
        composable("normalChart") {
            NormalChartScreen(
                onBackClick = { navController.popBackStack() },
                onOpenFileClick = {
                    // TODO: Реализовать открытие файла
                },
                onClearChartClick = {
                    // TODO: Реализовать очистку графика
                },
                onSettingsClick = {
                    navController.navigate("normalChartSettings")
                }
            )
        }
        
        // Экран "График ИБА"
        composable("ibaChart") {
            IBAChartScreen(
                onBackClick = { navController.popBackStack() },
                onSettingsClick = {
                    navController.navigate("ibaChartSettings")
                }
            )
        }
        
        // Экран "Настройки"
        composable("settings") {
            SettingsScreen(
                onBackClick = { navController.popBackStack() }
            )
        }
        
        // Экран "Настройки обычного графика"
        composable("normalChartSettings") {
            NormalChartSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    // TODO: Сохранить настройки обычного графика
                },
                onCancelClick = { navController.popBackStack() },
                onSetting1Click = {
                    // TODO: Обработка настройки 1
                },
                onSetting2Click = {
                    // TODO: Обработка настройки 2
                },
                onSetting3Click = {
                    // TODO: Обработка настройки 3
                }
            )
        }
        
        // Экран "Настройки IBA графика"
        composable("ibaChartSettings") {
            IBAChartSettingsScreen(
                onBackClick = { navController.popBackStack() },
                onSaveClick = {
                    // TODO: Сохранить настройки IBA графика
                },
                onCancelClick = { navController.popBackStack() },
                onSetting1Click = {
                    // TODO: Обработка настройки 1
                },
                onSetting2Click = {
                    // TODO: Обработка настройки 2
                },
                onSetting3Click = {
                    // TODO: Обработка настройки 3
                }
            )
        }
    }
}

/**
 * Экран главного меню.
 *
 * @param getVersionUseCase UseCase для получения версии приложения
 * @param onNormalChartClick Callback для перехода к обычному графику
 * @param onIbaChartClick Callback для перехода к IBA графику
 * @param onSettingsClick Callback для перехода к настройкам
 * @param onExitClick Callback для выхода из приложения
 */
@Composable
fun MainScreen(
    getVersionUseCase: GetAppVersionUseCase,
    onNormalChartClick: () -> Unit,
    onIbaChartClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onExitClick: () -> Unit
) {
    // Создаем ViewModel напрямую с передачей use case
    val viewModel: MainViewModel = viewModel(
        factory = MainViewModel.Factory(getVersionUseCase)
    )
    val appVersion by viewModel.appVersion.collectAsState()
    
    // Загружаем версию при первом запуске
    LaunchedEffect(Unit) {
        viewModel.loadAppVersion()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = onNormalChartClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Обычный график", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        }
        
        Button(
            onClick = onIbaChartClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("График ИБА", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        }
        
        Button(
            onClick = onSettingsClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text("Настройки", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        }
        
        Button(
            onClick = onExitClick,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.error
            )
        ) {
            Text("Выход", fontSize = MaterialTheme.typography.bodyLarge.fontSize)
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        Text(
            text = "Версия: ${appVersion.versionName}",
            fontSize = MaterialTheme.typography.bodySmall.fontSize,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
