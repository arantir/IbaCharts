package com.example.mychartsapp

import androidx.test.espresso.matcher.ViewMatchers.withId

/**
 * Вспомогательный объект для UI тестов.
 * Содержит предварительно сконфигурированные ViewMatchers для кнопок.
 * 
 * Позволяет использовать например: TestUtils.normalChartBtn.perform(click())
 * вместо длинной записи с полным путём к R.id.
 * 
 * Примечание: В текущих тестах не используется, но может пригодиться.
 */
object TestUtils {
    // Кнопка "Обычный график"
    val normalChartBtn = androidx.test.espresso.Espresso.onView(withId(com.example.mychartsapp.presentation.R.id.normalChartBtn))
    
    // Кнопка "График ИБА"
    val ibaChartBtn = androidx.test.espresso.Espresso.onView(withId(com.example.mychartsapp.presentation.R.id.ibaChartBtn))
    
    // Кнопка "Настройки"
    val settingsBtn = androidx.test.espresso.Espresso.onView(withId(com.example.mychartsapp.presentation.R.id.settingsBtn))
    
    // Кнопка "Выход" (не используется в тестах, так как после её нажатия приложение закрывается)
    val exitBtn = androidx.test.espresso.Espresso.onView(withId(com.example.mychartsapp.presentation.R.id.exitBtn))
}
