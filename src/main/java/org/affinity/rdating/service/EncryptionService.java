/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.service;

import jakarta.annotation.PostConstruct;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EncryptionService {

  private static final Logger logger = LoggerFactory.getLogger(EncryptionService.class);
  private static final String AES = "AES";

  @Value("${key}")
  private String encryptionKey;

  private Cipher encryptionCipher;
  private Cipher decryptionCipher;

  @PostConstruct
  public void init() {
    encryptionCipher = getEncryptionCipher(encryptionKey);
    decryptionCipher = getDecryptionCipher(encryptionKey);
  }

  private Cipher getEncryptionCipher(String encryptionKey) {
    Key key = new SecretKeySpec(encryptionKey.getBytes(), AES);
    try {
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.ENCRYPT_MODE, key);
      return cipher;
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
      logger.error("init cipher failed", e);
      throw new IllegalStateException(e);
    }
  }

  private Cipher getDecryptionCipher(String encryptionKey) {
    Key key = new SecretKeySpec(encryptionKey.getBytes(), AES);
    try {
      Cipher cipher = Cipher.getInstance(AES);
      cipher.init(Cipher.DECRYPT_MODE, key);
      return cipher;
    } catch (InvalidKeyException | NoSuchAlgorithmException | NoSuchPaddingException e) {
      logger.error("init cipher failed", e);
      throw new IllegalStateException(e);
    }
  }

  public String encrypt(String rawValue) {
    if (rawValue == null) {
      return null;
    }
    try {
      return Base64.getEncoder().encodeToString(encryptionCipher.doFinal(rawValue.getBytes()));
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      logger.error("Encryption failed", e);
      throw new IllegalStateException(e);
    }
  }

  public String decrypt(String encryptedValue) {
    try {
      return Base64.getEncoder()
          .encodeToString(decryptionCipher.doFinal(encryptedValue.getBytes()));
    } catch (BadPaddingException | IllegalBlockSizeException e) {
      logger.error("Decryption failed", e);
      throw new IllegalStateException(e);
    }
  }
}
