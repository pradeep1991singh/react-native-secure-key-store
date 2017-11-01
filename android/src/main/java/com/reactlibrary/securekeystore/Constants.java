package com.reactlibrary.securekeystore;

// Secure key store helper class, contains constants used for encrypting/decrypting data on Android.

public class Constants {

    // Key Store
    public static final String KEYSTORE_PROVIDER_1 = "AndroidKeyStore";
    public static final String KEYSTORE_PROVIDER_2 = "AndroidKeyStoreBCWorkaround";
    public static final String KEYSTORE_PROVIDER_3 = "AndroidOpenSSL";

    public static final String RSA_ALGORITHM = "RSA/ECB/PKCS1Padding";
    public static final String AES_ALGORITHM = "AES/ECB/PKCS5Padding";

    public static final String TAG = "SecureKeyStore";

    // Internal storage file
    public static final String SKS_KEY_FILENAME = "SKS_KEY_FILE";
    public static final String SKS_DATA_FILENAME = "SKS_DATA_FILE";
}
