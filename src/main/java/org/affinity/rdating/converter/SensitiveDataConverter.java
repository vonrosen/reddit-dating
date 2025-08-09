/*
  Copyright 2025, RDating. All rights reserved.
*/
package org.affinity.rdating.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.affinity.rdating.service.EncryptionService;
import org.springframework.stereotype.Component;

@Component
@Converter
public class SensitiveDataConverter implements AttributeConverter<String, String> {

  private final EncryptionService encryptionService;

  public SensitiveDataConverter(EncryptionService encryptionService) {
    this.encryptionService = encryptionService;
  }

  @Override
  public String convertToDatabaseColumn(String rawValue) {
    if (rawValue == null) {
      return null;
    }
    return encryptionService.encrypt(rawValue);
  }

  @Override
  public String convertToEntityAttribute(String dbData) {
    if (dbData == null) {
      return null;
    }
    return encryptionService.decrypt(dbData);
  }
}
