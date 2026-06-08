package com.example.mychartsapp

import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.rules.ActivityScenarioRule
import com.example.mychartsapp.presentation.MainActivity

/**
 * Вспомогательные утилиты для Compose UI тестов.
 */
object TestUtils {
    
    /**
     * Расширение для AndroidComposeTestRule, добавляющее удобные методы.
     */
    fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.clickOnButton(buttonText: String) {
        onNodeWithText(buttonText).performClick()
    }
    
    /**
     * Проверяет, что текущий экран - главное меню.
     */
    fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertOnMainScreen() {
        onNodeWithText("Обычный график").assertIsDisplayed()
        onNodeWithText("График ИБА").assertIsDisplayed()
        onNodeWithText("Настройки").assertIsDisplayed()
        onNodeWithText("Выход").assertIsDisplayed()
    }
    
    /**
     * Проверяет, что текущий экран - экран с кнопкой "Назад".
     */
    fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.assertOnScreenWithBackButton() {
        onNodeWithText("← Назад").assertIsDisplayed()
    }
}
