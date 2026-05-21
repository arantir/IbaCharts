package com.example.mychartsapp.presentation.extensions

import androidx.arch.core.executor.ArchTaskExecutor
import androidx.arch.core.executor.TaskExecutor
import org.junit.jupiter.api.extension.BeforeEachCallback
import org.junit.jupiter.api.extension.ExtensionContext

/**
 * Запускается перед каждым тестом.
 * Подменяет глобальный планировщик задач AndroidX на фейковый.
 * Задачи вместо фонового/UI потока выполняются сразу в текущем потоке.
 * Это позволяет тестировать LiveData без реального UI потока.
 */
class InstantTaskExecutorExtension : BeforeEachCallback {
    
    override fun beforeEach(context: ExtensionContext) {
        // Получаем глобальный синглтон ArchTaskExecutor
        ArchTaskExecutor.getInstance().setDelegate(object : TaskExecutor() {
            
            // Задачи для диска выполняем сразу
            override fun executeOnDiskIO(runnable: Runnable) = runnable.run()
            
            // Задачи для UI потока выполняем сразу
            override fun postToMainThread(runnable: Runnable) = runnable.run()
            
            // Сообщаем, что текущий поток считается главным
            override fun isMainThread(): Boolean = true
        })
    }
}
