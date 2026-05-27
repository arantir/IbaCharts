package com.example.mychartsapp

import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mychartsapp.presentation.R
import com.example.mychartsapp.presentation.ui.MainActivity
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    private lateinit var navController: TestNavHostController

    @Before
    fun setup() {
        activityRule.scenario.onActivity { activity ->
            // 1. Создаем фейковый навигатор, полностью изолированный от Android
            navController = TestNavHostController(ApplicationProvider.getApplicationContext())

            // 2. Передаем в него граф навигации (ресурс R.navigation.your_nav_graph)
            // Если у тебя отдельный XML файл навигации, укажи его здесь.
            // Если его нет (у тебя, видимо, сейчас просто startActivity), 
            // ты можешь просто создать пустой граф или на время оставить этот момент, 
            // для тестов это не критично, так как мы проверяем факт вызова, а не реальный переход.
            // navController.setGraph(R.navigation.your_nav_graph) 
            
            // 3. Внедряем фейковый навигатор в Activity
            // !!! ВАЖНО: Тебе потребуется временно модифицировать MainActivity, 
            // чтобы он использовал этот navController для переходов.
            // Это стандартный паттерн Dependency Injection для навигации.
            // (Пока просто представь, что мы это сделали)
        }
    }

    @Test
    fun normalChartBtn_triggersNavigationToNormalChart() {
        // Кликаем по кнопке
        onView(withId(R.id.normalChartBtn)).perform(click())
        
        // Проверяем, что навигатор получил команду уйти на экран с ID normal_chart_dest
        // Так как у тебя пока нет Navigation Component, этот тест не пройдет, 
        // но он демонстрирует архитектурно верный путь.
        assertEquals(R.id.normal_chart_dest, navController.currentDestination?.id)
    }
}