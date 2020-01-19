package com.rpg2014.model;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.ToString;

@ToString
@Getter
@Builder
public class EncryptionResult {
    byte[] encryptedBytes;
    String encryptedKey;
}
