package com.example.mychartsapp.presentation.viewmodels

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.mychartsapp.domain.models.AppVersion
import com.example.mychartsapp.domain.usecases.GetAppVersionUseCase
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
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.Rule

@OptIn(ExperimentalCoroutinesApi::class)
class MainViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    @BeforeEach
    fun setup() {
        Dispatchers.setMain(testDispatcher)
    }
    
    @AfterEach
    fun tearDown() {
        Dispatchers.resetMain()
    }
    
    @Test
    fun `loadAppVersion updates appVersion LiveData`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        val expectedVersion = AppVersion("1.0.0", 100L)
        
        coEvery { mockUseCase.invoke() } returns expectedVersion
        
        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()
        
        advanceUntilIdle()
        
        assertEquals(expectedVersion, viewModel.appVersion.value)
    }
    
    @Test
    fun `isLoading shows correct state during loading`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        coEvery { mockUseCase.invoke() } coAnswers {
            delay(1000)
            AppVersion("1.0.0", 100L)
        }
        
        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()
        
        assertTrue(viewModel.isLoading.value == true)
        
        advanceUntilIdle()
        
        assertTrue(viewModel.isLoading.value == false)
    }
    
    @Test
    fun `error LiveData gets error message when useCase throws`() = runTest {
        val mockUseCase = mockk<GetAppVersionUseCase>()
        val errorMessage = "Ошибка подключения"
        coEvery { mockUseCase.invoke() } throws Exception(errorMessage)
        
        val viewModel = MainViewModel(mockUseCase)
        viewModel.loadAppVersion()
        
        advanceUntilIdle()
        
        assertEquals(errorMessage, viewModel.error.value)
        assertTrue(viewModel.isLoading.value == false)
    }
}
