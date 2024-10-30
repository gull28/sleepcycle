package com.example.sleep_cycle.modules


import com.example.sleep_cycle.data.modules.ToastModule
import com.example.sleep_cycle.data.modules.Toaster
import com.google.android.datatransport.runtime.dagger.Module
import com.google.android.datatransport.runtime.dagger.Provides
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import javax.inject.Singleton

@Module
@TestInstallIn (
    components = [SingletonComponent::class],
    replaces = [ToastModule::class]
)
class OverrideToastModule {
    @Provides
    @Singleton
    fun provideToaster(): Toaster = FakeToaster
}

object FakeToaster : Toaster {
    val toasts = mutableListOf<String> ()
    override fun showToast(text: String) {
        toasts.add(text)
    }
}