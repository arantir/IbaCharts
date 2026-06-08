package com.example.mychartsapp.presentation.viewmodels

import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
import com.example.mychartsapp.presentation.extensions.InstantTaskExecutorExtension
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

/**
 * Тесты для MainViewModel.
 * 
 * Проверяют:
 * - Передачу данных от UseCase в StateFlow
 * - Обработку ошибок
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
     * Тест 1: Проверяет, что результат UseCase попадает в appVersion StateFlow.
     * 
     * Строчка реального кода: val version = getVersionUseCase()
     * Строчка реального кода: _appVersion.value = version
     */
    @Test
    fun `getAppVersionUseCase result goes to appVersion StateFlow`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        val expectedVersion = AppVersion("1.0.0", 100L)

        coEvery { mockUseCase.invoke() } returns expectedVersion

        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()

        advanceUntilIdle()

        assertEquals(expectedVersion, viewModel.appVersion.value)
    }

    /**
     * Тест 2: Проверяет, что при ошибке UseCase StateFlow продолжает хранить старое значение.
     * ViewModel не обрабатывает ошибки, UseCase пробрасывает их выше.
     */
    @Test
    fun `appVersion retains old value when useCase throws exception`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        val oldVersion = AppVersion("0.0", 0)
        val errorMessage = "Ошибка подключения"

        coEvery { mockUseCase.invoke() } throws Exception(errorMessage)

        val viewModel = MainViewModel(mockUseCase)
        
        // До вызова loadAppVersion - значение по умолчанию
        assertEquals(oldVersion, viewModel.appVersion.value)
        
        viewModel.loadAppVersion()
        advanceUntilIdle()
        
        // После ошибки значение не изменилось
        assertEquals(oldVersion, viewModel.appVersion.value)
    }
}
