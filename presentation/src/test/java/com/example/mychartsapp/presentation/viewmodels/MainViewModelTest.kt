package com.example.mychartsapp.presentation.viewmodels

import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
import com.example.mychartsapp.presentation.extensions.InstantTaskExecutorExtension
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Тесты для MainViewModel.
 * 
 * Проверяют:
 * - Передачу данных от UseCase в LiveData
 * - Состояние загрузки (isLoading)
 * - Обработку ошибок
 * - Очистку ошибки после успешной загрузки
 * 
 * @OptIn(ExperimentalCoroutinesApi::class) - разрешает использование экспериментального API корутин
 * @ExtendWith(InstantTaskExecutorExtension::class) - подменяет ArchTaskExecutor,
 *         чтобы LiveData обновлялась синхронно без UI потока
 */
@OptIn(ExperimentalCoroutinesApi::class)
@ExtendWith(InstantTaskExecutorExtension::class)
class MainViewModelTest {

    // Тестовый диспетчер для управления корутинами
    private val testDispatcher = StandardTestDispatcher()

    /**
     * Выполняется перед каждым тестом.
     * Подменяет главный диспетчер корутин на тестовый.
     */
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }

    /**
     * Выполняется после каждого теста.
     * Возвращает главный диспетчер в исходное состояние.
     */
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * Тест 1: Проверяет, что результат UseCase попадает в appVersion LiveData.
     * 
     * Строчка реального кода: val version = getAppVersionUseCase()
     * Строчка реального кода: _appVersion.value = version
     */
    @Test
    fun `getAppVersionUseCase result goes to appVersion LiveData`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        val expectedVersion = AppVersion("1.0.0", 100L)

        coEvery { mockUseCase.invoke() } returns expectedVersion

        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()

        advanceUntilIdle()

        assertEquals(expectedVersion, viewModel.appVersion.value)
    }

    /**
     * Тест 2: Проверяет, что isLoading = true во время загрузки и false после.
     * 
     * Строчка реального кода: _isLoading.value = true
     * Строчка реального кода: _isLoading.value = false
     */
    @Test
    fun `isLoading is true during loading and false after completion`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        coEvery { mockUseCase.invoke() } coAnswers {
            delay(100)
            AppVersion("1.0.0", 100L)
        }

        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()

        assertTrue(viewModel.isLoading.value == true)

        advanceUntilIdle()

        assertTrue(viewModel.isLoading.value == false)
    }

    /**
     * Тест 3: Проверяет, что при ошибке UseCase сообщение попадает в error LiveData.
     * 
     * Строчка реального кода: _error.value = e.message
     */
    @Test
    fun `error LiveData gets error message when useCase throws exception`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        val errorMessage = "Ошибка подключения"

        coEvery { mockUseCase.invoke() } throws Exception(errorMessage)

        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()

        advanceUntilIdle()

        assertEquals(errorMessage, viewModel.error.value)
        assertTrue(viewModel.isLoading.value == false)
    }

    /**
     * Тест 4: Проверяет, что error очищается после успешной загрузки.
     * 
     * Строчка реального кода: _error.value = null
     */
    @Test
    fun `error LiveData becomes null after successful load`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        
        // Первый вызов: ошибка
        coEvery { mockUseCase.invoke() } throws Exception("Ошибка")
        
        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()
        advanceUntilIdle()
        
        assertEquals("Ошибка", viewModel.error.value)
        
        // Второй вызов: успех
        val expectedVersion = AppVersion("2.0.0", 200L)
        coEvery { mockUseCase.invoke() } returns expectedVersion
        
        viewModel.loadAppVersion()
        advanceUntilIdle()
        
        assertNull(viewModel.error.value)
        assertEquals(expectedVersion, viewModel.appVersion.value)
    }
}
