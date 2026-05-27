package com.example.mychartsapp.presentation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Compose компонент экрана настроек приложения.
 * Отображает панель управления с кнопкой назад и область для будущих настроек.
 * 
 * @param onBackClick Callback для кнопки "Назад" (возврат на предыдущий экран)
 */
@Composable
fun SettingsScreen(
    onBackClick: () -> Unit
) {
    // Основной вертикальный контейнер
    Column(modifier = Modifier.fillMaxSize()) {
        
        // Верхняя панель с кнопкой навигации
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Кнопка "Назад" - закрытие активности
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Назад"
                )
            }
            
            // Растягивающийся разделитель
            Spacer(modifier = Modifier.weight(1f))
        }
        
        // Разделительная линия
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp
        )
        
        // Область для будущих настроек (пока пустая)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Настройки приложения\n\n(будут добавлены позже)",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
