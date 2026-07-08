package com.bittick.service;

import com.bittick.data.ai.NotificationHelper;
import com.bittick.data.preferences.BittickPreferences;
import com.bittick.network.ApiService;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@QualifierMetadata
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
public final class BittickForegroundService_MembersInjector implements MembersInjector<BittickForegroundService> {
  private final Provider<BittickPreferences> prefsProvider;

  private final Provider<NotificationHelper> notifierProvider;

  private final Provider<ApiService> apiProvider;

  public BittickForegroundService_MembersInjector(Provider<BittickPreferences> prefsProvider,
      Provider<NotificationHelper> notifierProvider, Provider<ApiService> apiProvider) {
    this.prefsProvider = prefsProvider;
    this.notifierProvider = notifierProvider;
    this.apiProvider = apiProvider;
  }

  public static MembersInjector<BittickForegroundService> create(
      Provider<BittickPreferences> prefsProvider, Provider<NotificationHelper> notifierProvider,
      Provider<ApiService> apiProvider) {
    return new BittickForegroundService_MembersInjector(prefsProvider, notifierProvider, apiProvider);
  }

  @Override
  public void injectMembers(BittickForegroundService instance) {
    injectPrefs(instance, prefsProvider.get());
    injectNotifier(instance, notifierProvider.get());
    injectApi(instance, apiProvider.get());
  }

  @InjectedFieldSignature("com.bittick.service.BittickForegroundService.prefs")
  public static void injectPrefs(BittickForegroundService instance, BittickPreferences prefs) {
    instance.prefs = prefs;
  }

  @InjectedFieldSignature("com.bittick.service.BittickForegroundService.notifier")
  public static void injectNotifier(BittickForegroundService instance,
      NotificationHelper notifier) {
    instance.notifier = notifier;
  }

  @InjectedFieldSignature("com.bittick.service.BittickForegroundService.api")
  public static void injectApi(BittickForegroundService instance, ApiService api) {
    instance.api = api;
  }
}
