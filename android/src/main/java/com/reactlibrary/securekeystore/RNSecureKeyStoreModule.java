/**
 * React Native Secure Key Store
 * Store keys securely in Android Keystore
 * Ref: cordova-plugin-secure-key-store
 */

package com.reactlibrary.securekeystore;

import android.content.Context;
import android.os.Build;
import android.security.KeyPairGeneratorSpec;
import android.util.Log;

import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Calendar;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
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
      setCipherText(alias, input);
      promise.resolve("stored ciphertext in app storage");
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("{\"code\":9,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
    }
  }

  private PublicKey getOrCreatePublicKey(String alias) throws GeneralSecurityException, IOException {
    KeyStore keyStore = KeyStore.getInstance(getKeyStore());
    keyStore.load(null);

    if (!keyStore.containsAlias(alias) || keyStore.getCertificate(alias) == null) {
      Log.i(Constants.TAG, "no existing asymmetric keys for alias");

      Calendar start = Calendar.getInstance();
      Calendar end = Calendar.getInstance();
      end.add(Calendar.YEAR, 50);
      KeyPairGeneratorSpec spec = new KeyPairGeneratorSpec.Builder(getContext())
          .setAlias(alias)
          .setSubject(new X500Principal("CN=" + alias))
          .setSerialNumber(BigInteger.ONE)
          .setStartDate(start.getTime())
          .setEndDate(end.getTime())
          .build();

      KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA", getKeyStore());
      generator.initialize(spec);
      generator.generateKeyPair();

      Log.i(Constants.TAG, "created new asymmetric keys for alias");
    }

    return keyStore.getCertificate(alias).getPublicKey();
  }

  private byte[] encryptRsaPlainText(PublicKey publicKey, byte[] plainTextBytes) throws GeneralSecurityException, IOException {
    Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, publicKey);
    return encryptCipherText(cipher, plainTextBytes);
  }

  private byte[] encryptAesPlainText(SecretKey secretKey, String plainText) throws GeneralSecurityException, IOException {
    Cipher cipher = Cipher.getInstance(Constants.AES_ALGORITHM);
    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
    return encryptCipherText(cipher, plainText);
  }

  private byte[] encryptCipherText(Cipher cipher, String plainText) throws GeneralSecurityException, IOException {
    return encryptCipherText(cipher, plainText.getBytes("UTF-8"));
  }

  private byte[] encryptCipherText(Cipher cipher, byte[] plainTextBytes) throws GeneralSecurityException, IOException {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    CipherOutputStream cipherOutputStream = new CipherOutputStream(outputStream, cipher);
    cipherOutputStream.write(plainTextBytes);
    cipherOutputStream.close();
    return outputStream.toByteArray();
  }

  private SecretKey getOrCreateSecretKey(String alias) throws GeneralSecurityException, IOException {
    try {
      return getSymmetricKey(alias);
    } catch (FileNotFoundException fnfe) {
      Log.i(Constants.TAG, "no existing symmetric key for alias");

      KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
      //32bytes / 256bits AES key
      keyGenerator.init(256);
      SecretKey secretKey = keyGenerator.generateKey();
      PublicKey publicKey = getOrCreatePublicKey(alias);
      Storage.writeValues(getContext(), Constants.SKS_KEY_FILENAME + alias,
          encryptRsaPlainText(publicKey, secretKey.getEncoded()));

      Log.i(Constants.TAG, "created new symmetric keys for alias");
      return secretKey;
    }
  }

  private void setCipherText(String alias, String input) throws GeneralSecurityException, IOException {
    Storage.writeValues(getContext(), Constants.SKS_DATA_FILENAME + alias,
        encryptAesPlainText(getOrCreateSecretKey(alias), input));
  }

  @ReactMethod
  public void get(String alias, Promise promise) {
    try {
      promise.resolve(getPlainText(alias));
    } catch (FileNotFoundException fnfe) {
      fnfe.printStackTrace();
      promise.reject("404", "{\"code\":404,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + fnfe.getMessage() + "}", fnfe);
    } catch (Exception e) {
      e.printStackTrace();
      Log.e(Constants.TAG, "Exception: " + e.getMessage());
      promise.reject("{\"code\":1,\"api-level\":" + Build.VERSION.SDK_INT + ",\"message\":" + e.getMessage() + "}");
    }
  }

  private PrivateKey getPrivateKey(String alias) throws GeneralSecurityException, IOException {
    KeyStore keyStore = KeyStore.getInstance(getKeyStore());
    keyStore.load(null);
    return (PrivateKey) keyStore.getKey(alias, null);
  }

  private byte[] decryptRsaCipherText(PrivateKey privateKey, byte[] cipherTextBytes) throws GeneralSecurityException, IOException {
    Cipher cipher = Cipher.getInstance(Constants.RSA_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, privateKey);
    return decryptCipherText(cipher, cipherTextBytes);
  }

  private byte[] decryptAesCipherText(SecretKey secretKey, byte[] cipherTextBytes) throws GeneralSecurityException, IOException {
    Cipher cipher = Cipher.getInstance(Constants.AES_ALGORITHM);
    cipher.init(Cipher.DECRYPT_MODE, secretKey);
    return decryptCipherText(cipher, cipherTextBytes);
  }

  private byte[] decryptCipherText(Cipher cipher, byte[] cipherTextBytes) throws IOException {
    ByteArrayInputStream bais = new ByteArrayInputStream(cipherTextBytes);
    CipherInputStream cipherInputStream = new CipherInputStream(bais, cipher);
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    byte[] buffer = new byte[256];
    int bytesRead = cipherInputStream.read(buffer);
    while (bytesRead != -1) {
      baos.write(buffer, 0, bytesRead);
      bytesRead = cipherInputStream.read(buffer);
    }
    return baos.toByteArray();
  }

  private SecretKey getSymmetricKey(String alias) throws GeneralSecurityException, IOException {
    byte[] cipherTextBytes = Storage.readValues(getContext(), Constants.SKS_KEY_FILENAME + alias);
    return new SecretKeySpec(decryptRsaCipherText(getPrivateKey(alias), cipherTextBytes), Constants.AES_ALGORITHM);
  }

  private String getPlainText(String alias) throws GeneralSecurityException, IOException {
    SecretKey secretKey = getSymmetricKey(alias);
    byte[] cipherTextBytes = Storage.readValues(getContext(), Constants.SKS_DATA_FILENAME + alias);
    return new String(decryptAesCipherText(secretKey, cipherTextBytes), "UTF-8");
  }

  @ReactMethod
  public void remove(String alias, Promise promise) {
    Storage.resetValues(getContext(), new String[] { 
      Constants.SKS_DATA_FILENAME + alias, 
      Constants.SKS_KEY_FILENAME + alias, 
    });
    promise.resolve("cleared alias");
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
