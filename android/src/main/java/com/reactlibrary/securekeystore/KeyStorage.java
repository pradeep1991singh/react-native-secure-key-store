package com.reactlibrary.securekeystore;

// Helper function for storing keys to internal storage.

import android.content.Context;
import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public final class KeyStorage {

    private static void writeChunkLength(Context context, String keyAlias, int chunkLength) throws IOException {
        String filePath = Constants.SKS_FILENAME + keyAlias + Constants.CHUNK_LENGTH_EXTENSION;
        FileOutputStream fos = context.openFileOutput(filePath, Context.MODE_PRIVATE);
        fos.write(String.valueOf(chunkLength).getBytes("UTF-8"));
        fos.close();
    }

    private static void writeValues(FileOutputStream fos, byte[] bytes) throws IOException {
        fos.write(bytes);
    }

    private static void writeValues(FileOutputStream fos, byte[] bytes, int offset, int length) throws IOException {
        fos.write(bytes, offset, length);
    }

    public static void writeValues(Context context, String keyAlias, byte[] bytes) throws IOException {
        if (bytes.length > Constants.MAX_PLAINTEXT_BYTE_LENGTH) {
            int numberOfChunks = (int) Math.ceil(bytes.length / (Constants.MAX_PLAINTEXT_BYTE_LENGTH * 1.0));
            writeChunkLength(context, keyAlias, bytes.length / Constants.MAX_PLAINTEXT_BYTE_LENGTH);

            String baseFilePath = Constants.SKS_FILENAME + keyAlias;
            for (int index = 0; index < numberOfChunks; index++) {
                FileOutputStream fos = context.openFileOutput(baseFilePath + "." + index +
                        Constants.CHUNK_EXTENSION, Context.MODE_PRIVATE);
                int chunkLength = Math.min(bytes.length - (index * Constants.MAX_PLAINTEXT_BYTE_LENGTH), Constants.MAX_PLAINTEXT_BYTE_LENGTH);
                writeValues(fos, bytes, index * Constants.MAX_PLAINTEXT_BYTE_LENGTH, chunkLength);
                fos.close();
            }
        } else {
            FileOutputStream fos = context.openFileOutput(Constants.SKS_FILENAME + keyAlias, Context.MODE_PRIVATE);
            writeValues(fos, bytes);
            fos.close();
        }
    }

    private static boolean isChunked(Context context, String keyAlias) {
        String filePath = Constants.SKS_FILENAME + keyAlias + Constants.CHUNK_LENGTH_EXTENSION;
        return new File(context.getFilesDir(), filePath).exists();
    }

    private static int readChunkLength(Context context, String keyAlias) throws IOException {
        String filePath = Constants.SKS_FILENAME + keyAlias + Constants.CHUNK_LENGTH_EXTENSION;
        FileInputStream fis = context.openFileInput(filePath);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead = fis.read(buffer);
        while(bytesRead != -1) {
            baos.write(buffer, 0, bytesRead);
            bytesRead = fis.read(buffer);
        }
        fis.close();

        return Integer.parseInt(new String(baos.toByteArray(), "UTF-8"));
    }

    private static byte[] readValues(FileInputStream fis) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(Constants.MAX_PLAINTEXT_BYTE_LENGTH);
        byte[] buffer = new byte[Constants.MAX_PLAINTEXT_BYTE_LENGTH];
        int bytesRead = fis.read(buffer);
        while(bytesRead != -1) {
            baos.write(buffer, 0, bytesRead);
            bytesRead = fis.read(buffer);
        }
        return baos.toByteArray();
    }

    public static byte[] readValues(Context context, String keyAlias) throws IOException {
        if (isChunked(context, keyAlias)) {
            int chunkLength = readChunkLength(context, keyAlias);

            //if there is a chunk file, then we need expect at least MAX_PLAINTEXT_BYTE_LENGTH+1 bytes
            ByteArrayOutputStream baos = new ByteArrayOutputStream(Constants.MAX_PLAINTEXT_BYTE_LENGTH + 1);
            String baseFilePath = Constants.SKS_FILENAME + keyAlias;
            for (int index = 0; index < chunkLength; index++) {
                FileInputStream fis = context.openFileInput(baseFilePath + "." + index +
                        Constants.CHUNK_EXTENSION);
                baos.write(readValues(fis));
                fis.close();
            }

            return baos.toByteArray();
        } else {
            String filePath = Constants.SKS_FILENAME + keyAlias;
            FileInputStream fis = context.openFileInput(filePath);
            byte[] cipherText = readValues(fis);
            fis.close();
            return cipherText;
        }
    }

    public static void resetValues(Context context, String keyAlias) {
        context.deleteFile(Constants.SKS_FILENAME + keyAlias);
    }

}