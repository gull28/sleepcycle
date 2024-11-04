package com.example.sleep_cycle.modules

import com.example.sleep_cycle.data.modules.ToastModule
import com.example.sleep_cycle.data.modules.Toaster
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module  // This should now correctly recognize it as a Hilt module
@TestInstallIn(
    components = [SingletonComponent::class],
    replaces = [ToastModule::class]
)
class OverrideToastModule {
    @Provides
    @Singleton
    fun provideToaster(): Toaster = FakeToaster
}

object FakeToaster : Toaster {
    val toasts = mutableListOf<String>()
    override fun showToast(text: String) {
        toasts.add(text)
    }
}
