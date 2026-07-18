package com.bittick.ui.settings;

import android.content.Context;
import com.bittick.data.cache.BittickImageCache;
import com.bittick.data.preferences.BittickPreferences;
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

  private final Provider<BittickPreferences> preferencesProvider;

  private final Provider<BittickImageCache> imageCacheProvider;

  public SettingsViewModel_Factory(Provider<Context> contextProvider,
      Provider<BittickPreferences> preferencesProvider,
      Provider<BittickImageCache> imageCacheProvider) {
    this.contextProvider = contextProvider;
    this.preferencesProvider = preferencesProvider;
    this.imageCacheProvider = imageCacheProvider;
  }

  @Override
  public SettingsViewModel get() {
    return newInstance(contextProvider.get(), preferencesProvider.get(), imageCacheProvider.get());
  }

  public static SettingsViewModel_Factory create(Provider<Context> contextProvider,
      Provider<BittickPreferences> preferencesProvider,
      Provider<BittickImageCache> imageCacheProvider) {
    return new SettingsViewModel_Factory(contextProvider, preferencesProvider, imageCacheProvider);
  }

  public static SettingsViewModel newInstance(Context context, BittickPreferences preferences,
      BittickImageCache imageCache) {
    return new SettingsViewModel(context, preferences, imageCache);
  }
}
