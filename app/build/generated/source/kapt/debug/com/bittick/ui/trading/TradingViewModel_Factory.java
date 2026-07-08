package com.bittick.ui.trading;

import android.content.Context;
import com.bittick.data.ai.NotificationHelper;
import com.bittick.data.preferences.BittickPreferences;
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
public final class TradingViewModel_Factory implements Factory<TradingViewModel> {
  private final Provider<Context> contextProvider;

  private final Provider<ApiService> apiProvider;

  private final Provider<NotificationHelper> notifierProvider;

  private final Provider<BittickPreferences> prefsProvider;

  public TradingViewModel_Factory(Provider<Context> contextProvider,
      Provider<ApiService> apiProvider, Provider<NotificationHelper> notifierProvider,
      Provider<BittickPreferences> prefsProvider) {
    this.contextProvider = contextProvider;
    this.apiProvider = apiProvider;
    this.notifierProvider = notifierProvider;
    this.prefsProvider = prefsProvider;
  }

  @Override
  public TradingViewModel get() {
    return newInstance(contextProvider.get(), apiProvider.get(), notifierProvider.get(), prefsProvider.get());
  }

  public static TradingViewModel_Factory create(Provider<Context> contextProvider,
      Provider<ApiService> apiProvider, Provider<NotificationHelper> notifierProvider,
      Provider<BittickPreferences> prefsProvider) {
    return new TradingViewModel_Factory(contextProvider, apiProvider, notifierProvider, prefsProvider);
  }

  public static TradingViewModel newInstance(Context context, ApiService api,
      NotificationHelper notifier, BittickPreferences prefs) {
    return new TradingViewModel(context, api, notifier, prefs);
  }
}
