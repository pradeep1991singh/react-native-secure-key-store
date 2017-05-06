/**
 * React Native Secure Key Store
 * Store keys securely in Android Keystore
 * Ref: cordova-plugin-secure-key-store
 */

package com.reactlibrary.securekeystore;

import com.facebook.react.bridge.NativeModule;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Promise;

import android.content.Context;
import android.util.Log;
import android.util.Base64;
import android.security.KeyPairGeneratorSpec;
import android.os.Build;

import java.security.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.StringBuffer;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.security.auth.x500.X500Principal;

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
  public void set(String alias, String input, Promise promise) {

    try {

      KeyStore keyStore = KeyStore.getInstance(getKeyStore());
      keyStore.load(null);

      if (!keyStore.containsAlias(alias)) {
        Calendar start = Calendar.getInstance();
        Calendar end = Calendar.getInstance();
        end.add(Calendar.YEAR, 1);
        KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getContext()).setAlias(alias)
            .setSubject(new X500Principal("CN=" + alias)).setSerialNumber(BigInteger.ONE).setStartDate(start.getTime())
            .setEndDate(end.getTime()).build();

        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", getKeyStore());
        generator.initialize(spec);

        KeyPair keyPair = generator.generateKeyPair();

        Log.i(Constants.TAG, "created new key pairs");
      }

      PublicKey publicKey = keyStore.getCertificate(alias).getPublicKey();

      if (input.isEmpty()) {
        Log.d(Constants.TAG, "Exception: input text is empty");
        return;
      }

      Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
      cipher.init(Cipher.ENCRYPT_MODE, publicKey);
      ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
      CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
      cipherOutputStream.write(input.getBytes("UTF-8"));
      cipherOutputStream.close();
      byte[] vals = outputStream.toByteArray();

      // writing key to storage
      KeyStorage.writeValues(getContext(), alias, vals);
      Log.i(Constants.TAG, "key created and stored successfully");
      promise.resolve("key stored successfully");

    } catch (Exception e) {
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("Api-level:" + Build.VERSION.SDK_INT + "\nException: " + e.getMessage());
    }

  }

  @ReactMethod
  public void get(String alias, Promise promise) {

    try {

      KeyStore keyStore = KeyStore.getInstance(getKeyStore());
      keyStore.load(null);
      PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, null);

      Cipher output = Cipher.getInstance(Constants.RSA_ALGORITHM);
      output.init(Cipher.DECRYPT_MODE, privateKey);
      CipherInputStream cipherInputStream = new CipherInputStream(
          new ByteArrayInputStream(KeyStorage.readValues(getContext(), alias)), output);

      ArrayList<Byte> values = new ArrayList<Byte>();
      int nextByte;
      while ((nextByte = cipherInputStream.read()) != -1) {
        values.add((byte) nextByte);
      }
      byte[] bytes = new byte[values.size()];
      for (int i = 0; i < bytes.length; i++) {
        bytes[i] = values.get(i).byteValue();
      }

      String finalText = new String(bytes, 0, bytes.length, "UTF-8");
      promise.resolve(finalText);

    } catch (Exception e) {
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("Api-level:" + Build.VERSION.SDK_INT + "\nException: " + e.getMessage());
    }
  }

  @ReactMethod
  public void remove(String alias, Promise promise) {
    try {
      KeyStorage.resetValues(getContext(), alias);
      Log.i(Constants.TAG, "key removed successfully");
      promise.resolve("key removed successfully");

    } catch (Exception e) {
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("Api-level:" + Build.VERSION.SDK_INT + "\nException: " + e.getMessage());
    }
  }

  private Context getContext() {
    return getReactApplicationContext();
  }

  private String getKeyStore() {
    try {
      KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_1);
      return Constants.KEYSTORE_PROVIDER_1;
    } catch (Exception err) {
      try {
        KeyStore.getInstance(Constants.KEYSTORE_PROVIDER_2);
        return Constants.KEYSTORE_PROVIDER_2;
      } catch (Exception e) {
        return Constants.KEYSTORE_PROVIDER_3;
      }
    }
  }

}