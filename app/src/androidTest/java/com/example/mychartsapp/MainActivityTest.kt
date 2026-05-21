package com.example.mychartsapp

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.mychartsapp.presentation.ui.IBAChartActivity
import com.example.mychartsapp.presentation.ui.MainActivity
import com.example.mychartsapp.presentation.ui.NormalChartActivity
import com.example.mychartsapp.presentation.ui.SettingsActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * UI тесты для MainActivity.
 * 
 * Проверяют навигацию: при нажатии на кнопки открываются нужные Activity.
 * 
 * Запускаются на эмуляторе или реальном устройстве.
 * Команда: ./gradlew :app:connectedAndroidTest
 */
@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    // Правило запускает MainActivity перед каждым тестом
    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun setup() {
        // Инициализируем Intent для проверки навигации
        Intents.init()
    }

    @After
    fun tearDown() {
        // Освобождаем ресурсы
        Intents.release()
    }

    /**
     * Тест 1: Проверяет, что кнопка "Обычный график" открывает NormalChartActivity
     */
    @Test
    fun normalChartBtn_opensNormalChartActivity() {
        // Нажимаем на кнопку "Обычный график"
        onView(withId(R.id.normalChartBtn)).perform(click())

        // Проверяем, что запустилась NormalChartActivity
        Intents.intended(hasComponent(NormalChartActivity::class.java.name))
    }

    /**
     * Тест 2: Проверяет, что кнопка "График ИБА" открывает IBAChartActivity
     */
    @Test
    fun ibaChartBtn_opensIBAChartActivity() {
        onView(withId(R.id.ibaChartBtn)).perform(click())
        Intents.intended(hasComponent(IBAChartActivity::class.java.name))
    }

    /**
     * Тест 3: Проверяет, что кнопка "Настройки" открывает SettingsActivity
     */
    @Test
    fun settingsBtn_opensSettingsActivity() {
        onView(withId(R.id.settingsBtn)).perform(click())
        Intents.intended(hasComponent(SettingsActivity::class.java.name))
    }

    /**
     * Тест 4: Проверяет, что кнопка "Выход" закрывает приложение
     * (Проверяем, что Activity завершается)
     */
    @Test
    fun exitBtn_finishesActivity() {
        // Проверяем, что Activity активна
        activityRule.scenario.onActivity { activity ->
            assert(!activity.isFinishing)
        }

        // Нажимаем на кнопку "Выход"
        onView(withId(R.id.exitBtn)).perform(click())

        // Проверяем, что Activity завершается
        activityRule.scenario.onActivity { activity ->
            assert(activity.isFinishing)
        }
    }
}
