package com.example.mychartsapp.presentation.ui

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.mychartsapp.presentation.R

/**
 * Compose компонент экрана настроек обычного графика.
 * Отображает панель управления с кнопками назад, сохранить, отмена,
 * а также три кнопки для различных настроек графика.
 * 
 * @param onBackClick Callback для кнопки "Назад" (возврат на предыдущий экран)
 * @param onSaveClick Callback для кнопки "Сохранить" (сохранение настроек)
 * @param onCancelClick Callback для кнопки "Отмена" (отмена изменений)
 * @param onSetting1Click Callback для первой настройки
 * @param onSetting2Click Callback для второй настройки
 * @param onSetting3Click Callback для третьей настройки
 */
@Composable
fun NormalChartSettingsScreen(
    onBackClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    onSetting1Click: () -> Unit,
    onSetting2Click: () -> Unit,
    onSetting3Click: () -> Unit
) {
    // Контекст для Toast
    val context = LocalContext.current
    
    // Основной вертикальный контейнер
    Column(modifier = Modifier.fillMaxSize()) {
        
        // Верхняя панель с кнопками навигации и управления
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
            
            // Кнопка "Сохранить" - сохранение настроек
            Button(onClick = onSaveClick) {
                Text("Сохранить")
            }
            
            // Кнопка "Отмена" - отмена изменений
            Button(onClick = onCancelClick) {
                Text("Отмена")
            }
        }
        
        // Разделительная линия
        HorizontalDivider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp
        )
        
        // Область с настройками
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Кнопка настройки 1
            Button(
                onClick = onSetting1Click,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("setting1", modifier = Modifier.padding(vertical = 8.dp))
            }
            
            // Кнопка настройки 2
            Button(
                onClick = onSetting2Click,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("setting2", modifier = Modifier.padding(vertical = 8.dp))
            }
            
            // Кнопка настройки 3
            Button(
                onClick = onSetting3Click,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("setting3", modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}
