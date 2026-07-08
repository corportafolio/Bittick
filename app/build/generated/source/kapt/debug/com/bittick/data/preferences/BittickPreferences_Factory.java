package com.bittick.data.preferences;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class BittickPreferences_Factory implements Factory<BittickPreferences> {
  private final Provider<Context> contextProvider;

  public BittickPreferences_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public BittickPreferences get() {
    return newInstance(contextProvider.get());
  }

  public static BittickPreferences_Factory create(Provider<Context> contextProvider) {
    return new BittickPreferences_Factory(contextProvider);
  }

  public static BittickPreferences newInstance(Context context) {
    return new BittickPreferences(context);
  }
}
