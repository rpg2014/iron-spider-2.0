package com.rpg2014.wrappers;

import com.amazonaws.encryptionsdk.AwsCrypto;
import com.amazonaws.encryptionsdk.CryptoResult;
import com.amazonaws.encryptionsdk.kms.KmsMasterKey;
import com.amazonaws.encryptionsdk.kms.KmsMasterKeyProvider;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.rpg2014.JsonObjectMapper.JsonObjectMapperProvider;
import com.rpg2014.model.EncryptionResult;
import com.rpg2014.model.journal.JournalEntry;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.core.SdkBytes;

import javax.ws.rs.ForbiddenException;
import javax.ws.rs.InternalServerErrorException;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
public class EncryptionWrapper {
    private static   final String KEY_ARN = System.getenv("key_arn");
    private static final String USERNAME = "username";
    @Getter
    private static EncryptionWrapper ourInstance = new EncryptionWrapper();
    private final AwsCrypto crypto;
    private final KmsMasterKeyProvider keyProvider;
    private final ObjectMapper jsonObjectMapper;

    private EncryptionWrapper() {
        this.crypto = new AwsCrypto();
        this.keyProvider  = KmsMasterKeyProvider.builder().withKeysForEncryption(KEY_ARN).build();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        mapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        mapper.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        this.jsonObjectMapper = mapper;
    }

    public EncryptionResult encryptJournalEntries(List<JournalEntry> journalEntryList, final String username) {
        final Map<String, String> context = Collections.singletonMap(USERNAME, username);
        try {
            String listString = jsonObjectMapper.writeValueAsString(journalEntryList);
            log.info(listString);

            CryptoResult<byte[],KmsMasterKey > result = crypto.encryptData(keyProvider, listString.getBytes(), context);
            return EncryptionResult.builder().encryptedKey(KEY_ARN).encryptedBytes(result.getResult()).build();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new InternalServerErrorException("unable to marshall journal entry list into json.");
        }catch (Exception e) {
            log.error(e.getMessage());
            throw new InternalServerErrorException(e);
        }
    }

    public List<JournalEntry> decryptBytesToJournalList(SdkBytes bytes, String username) {

        try {
            CryptoResult<byte[], KmsMasterKey> result = crypto.decryptData(keyProvider, bytes.asByteArray());
            if(!result.getEncryptionContext().get(USERNAME).equals(username)) {
                throw new ForbiddenException("Username from request, "+username+"; does not equal username from entries, "+ result.getEncryptionContext().get(USERNAME));
            }
            return jsonObjectMapper.readValue(new String(result.getResult()), new TypeReference<List<JournalEntry>>(){});
        } catch (IOException e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new InternalServerErrorException("Unable to decrypt/ unmarshal the entries for user" + username);
        } catch (Exception e){
            log.error(e.getMessage());
            throw new InternalServerErrorException(e);
        }

    }
}
