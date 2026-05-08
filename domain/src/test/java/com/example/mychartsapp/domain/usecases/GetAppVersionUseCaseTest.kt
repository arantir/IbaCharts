package com.example.mychartsapp.domain.usecases

import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.repositories.VersionRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

/**
 * Тесты для GetAppVersionUseCase.
 * Проверяют бизнес-логику получения версии приложения.
 */
class GetAppVersionUseCaseTest {

    @Test
    /**
     * Тест проверяет, что useCase успешно возвращает версию из репозитория.
     * 
     * Этот тест использует MockK для создания фейкового репозитория.
     * Mock (фейковый объект) нужен чтобы:
     * - Не зависеть от реальной реализации при тестировании
     * - Контролировать, что именно вернет репозиторий
     * - Проверить, что useCase правильно вызывает репозиторий
     * 
     * Шаги:
     * 1. Создаем мок репозитория (фейковый объект)
     * 2. Настраиваем мок, чтобы он возвращал определенную версию
     * 3. Создаем useCase с этим моком
     * 4. Вызываем useCase
     * 5. Проверяем, что useCase вернул ту же версию, которую вернул репозиторий
     * 
     * Ожидаемый результат: useCase должен вернуть данные из репозитория без изменений.
     */
    fun `getAppVersion returns version from repository`() = runBlocking {
        // Создаем фейковый репозиторий (мок)
        val mockRepository = mockk<VersionRepository>()
        
        // Создаем тестовые данные версии
        val expectedVersion = AppVersion("2.0.0", 200L)
        
        // Настраиваем мок: когда кто-то вызывает getAppVersion(), возвращаем expectedVersion
        // coEvery нужен для suspend функций
        coEvery { mockRepository.getAppVersion() } returns expectedVersion
        
        // Создаем useCase с нашим моком
        val useCase = GetAppVersionUseCase(mockRepository)
        
        // Вызываем useCase
        val actualVersion = useCase()
        
        // Проверяем, что useCase вернул именно то, что вернул репозиторий
        assertEquals(expectedVersion, actualVersion)
    }
    
    @Test
    /**
     * Тест проверяет, что useCase пробрасывает исключение, если репозиторий его выбросил.
     * 
     * Важно: useCase не должен перехватывать исключения, только пробрасывать выше.
     * Обработкой ошибок занимается ViewModel.
     * 
     * Шаги:
     * 1. Настраиваем мок репозитория, чтобы он выбрасывал исключение
     * 2. Создаем useCase
     * 3. Вызываем useCase и ожидаем, что он выбросит то же исключение
     * 
     * Ожидаемый результат: useCase должен пробросить исключение от репозитория.
     */
    fun `getAppVersion throws exception when repository throws`() = runBlocking {
        // Создаем мок репозитория
        val mockRepository = mockk<VersionRepository>()
        
        // Настраиваем мок: выбрасываем исключение при вызове
        val expectedException = RuntimeException("Ошибка получения версии")
        coEvery { mockRepository.getAppVersion() } throws expectedException
        
        // Создаем useCase
        val useCase = GetAppVersionUseCase(mockRepository)
        
        // Вызываем useCase и ожидаем, что он выбросит исключение
        val exception = org.junit.jupiter.api.assertThrows<RuntimeException> {
            runBlocking { useCase() }
        }
        
        // Проверяем, что исключение то же самое
        assertEquals(expectedException.message, exception.message)
    }
}
