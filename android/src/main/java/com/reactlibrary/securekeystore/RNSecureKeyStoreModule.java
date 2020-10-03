/**
 * React Native Secure Key Store
 * Store keys securely in Android Keystore
 * Ref: cordova-plugin-secure-key-store
 */

package com.reactlibrary.securekeystore;

import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKeys;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.GeneralSecurityException;

public class RNSecureKeyStoreModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNSecureKeyStoreModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
  }

  @Override
  public String getName() {
    return "RNSecureKeyStore";
  }

  @ReactMethod
  public void set(String alias, String input, @Nullable ReadableMap options, Promise promise) {
    try {
      getSecureSharedPreferences().edit().putString(alias, input).commit();
      promise.resolve("stored ciphertext in app storage");
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("{\"code\":9,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
    }
  }

  private SharedPreferences getSecureSharedPreferences() throws GeneralSecurityException, IOException {
    return EncryptedSharedPreferences.create(
      "secret_shared_prefs",
      MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
      reactContext,
      EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
      EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    );
  }

  @ReactMethod
  public void get(String alias, Promise promise) {
    try {
      String value = getSecureSharedPreferences().getString(alias, null);
      if (value == null) {
        //throw FileNotFoundException to keep match old behaviour when a value is missing
        throw new FileNotFoundException(alias + " has not been set");
      } else {
        promise.resolve(value);
      }
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      promise.reject("404", "{\"code\":404,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + fnfe.getMessage() + "}", fnfe);
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("{\"code\":1,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
    }
  }

  @ReactMethod
  public void remove(String alias, Promise promise) {
    try {
      getSecureSharedPreferences().edit().remove(alias).commit();
      promise.resolve("cleared alias");
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("{\"code\":6,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
    }
  }
}
