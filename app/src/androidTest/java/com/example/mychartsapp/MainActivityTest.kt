package com.example.mychartsapp

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mychartsapp.presentation.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun mainScreen_displaysAllButtons() {
        composeTestRule.onNodeWithText("Обычный график").assertIsDisplayed()
        composeTestRule.onNodeWithText("График ИБА").assertIsDisplayed()
        composeTestRule.onNodeWithText("Настройки").assertIsDisplayed()
        composeTestRule.onNodeWithText("Выход").assertIsDisplayed()
    }

    @Test
    fun normalChartButton_click_navigatesToNormalChart() {
        composeTestRule.onNodeWithText("Обычный график").performClick()
        
        composeTestRule.onNodeWithText("Обычный график").assertDoesNotExist()
        composeTestRule.onNodeWithText("← Назад").assertIsDisplayed()
    }

    @Test
    fun ibaChartButton_click_navigatesToIbaChart() {
        composeTestRule.onNodeWithText("График ИБА").performClick()
        
        composeTestRule.onNodeWithText("График ИБА").assertDoesNotExist()
        composeTestRule.onNodeWithText("← Назад").assertIsDisplayed()
    }

    @Test
    fun settingsButton_click_navigatesToSettings() {
        composeTestRule.onNodeWithText("Настройки").performClick()
        
        composeTestRule.onNodeWithText("Настройки").assertDoesNotExist()
        composeTestRule.onNodeWithText("← Назад").assertIsDisplayed()
    }

    @Test
    fun backButton_returnsToMainScreen() {
        composeTestRule.onNodeWithText("Настройки").performClick()
        composeTestRule.onNodeWithText("← Назад").performClick()
        
        composeTestRule.onNodeWithText("Обычный график").assertIsDisplayed()
        composeTestRule.onNodeWithText("График ИБА").assertIsDisplayed()
        composeTestRule.onNodeWithText("Настройки").assertIsDisplayed()
        composeTestRule.onNodeWithText("Выход").assertIsDisplayed()
    }
}
