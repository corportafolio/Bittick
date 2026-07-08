package com.bittick.ui.settings;

import android.content.Context;
import com.bittick.network.ApiService;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava"
})
public final class SettingsViewModel_Factory implements Factory<SettingsViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ApiService> apiProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<ApiService> apiProvider) {
    this.contextProvider = contextProvider;
    this.apiProvider = apiProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), apiProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ApiService> apiProvider) {
    return new SettingsViewModel_Factory(contextProvider, apiProvider);
  }

  public static SettingsViewModel newInstance(Context context, ApiService api) {
    return new SettingsViewModel(context, api);
  }
}
