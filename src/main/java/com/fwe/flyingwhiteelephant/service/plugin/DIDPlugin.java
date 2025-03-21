package com.fwe.flyingwhiteelephant.service.plugin;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.service.AppContextUtil;
import com.fwe.flyingwhiteelephant.service.BlockchainStorageService;
import com.fwe.flyingwhiteelephant.spi.CCRequest;
import com.fwe.flyingwhiteelephant.spi.CCResponse;
import com.fwe.flyingwhiteelephant.spi.IPlugin;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
public class DIDPlugin implements IPlugin {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final String DID_PREFIX = "did:fwe:";
    
    @Override
    public CCResponse call(CCRequest ccRequest) {
        try {
            return (CCResponse) this.getClass().getDeclaredMethod(ccRequest.getMethod(), CCRequest.class).invoke(this, ccRequest);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void storeState(String key, String value) {
        AppContextUtil.getBean(BlockchainStorageService.class).storeState(key, value);
    }

    @Override
    public String readState(String key) {
        return AppContextUtil.getBean(BlockchainStorageService.class).readState(key);
    }

    public CCResponse create(CCRequest ccRequest) throws JsonProcessingException {
        log.info("Creating new DID document");
        
        // Generate new DID
        String did = DID_PREFIX + UUID.randomUUID();
        
        // Create DID Document
        Map<String, Object> didDocument = new HashMap<>();
        didDocument.put("id", did);
        didDocument.put("created", Instant.now().toString());
        didDocument.put("updated", Instant.now().toString());
        didDocument.put("status", "active");
        
        // Add controller if specified
        // Create DID document, the parameter key is "DIDDocument
        String didDocumentStr = ccRequest.getParams().get("DIDDocument");
        // convert the string to map
        @SuppressWarnings("unchecked")
        Map<String, Object> didDocumentMap = objectMapper.readValue(didDocumentStr, Map.class);
        if (didDocumentMap.containsKey("controller")) {
            didDocument.put("controller", didDocumentMap.get("controller"));
        }
        
        // Add verification method if specified
        if (didDocumentMap.containsKey("publicKey")) {
            Map<String, Object> verificationMethod = new HashMap<>();
            verificationMethod.put("id", did + "#key-1");
            verificationMethod.put("type", "Ed25519VerificationKey2020");
            verificationMethod.put("controller", did);
            verificationMethod.put("publicKeyBase58", didDocumentMap.get("publicKey"));
            didDocument.put("verificationMethod", verificationMethod);
        }
        
        // Store DID Document
        storeState(did, objectMapper.writeValueAsString(didDocument));
        
        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(objectMapper.writeValueAsString(didDocument));
        return response;
    }

    public CCResponse resolve(CCRequest ccRequest) throws JsonProcessingException {
        log.info("Resolving DID document");
        
        String did = ccRequest.getParams().get("did");
        if (did == null || !did.startsWith(DID_PREFIX)) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("Invalid DID format");
            return response;
        }
        
        String didDocument = readState(did);
        if (didDocument == null) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("DID not found");
            return response;
        }
        
        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(didDocument);
        return response;
    }

    public CCResponse update(CCRequest ccRequest) throws JsonProcessingException {
        log.info("Updating DID document");
        
        String did = ccRequest.getParams().get("did");
        if (did == null || !did.startsWith(DID_PREFIX)) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("Invalid DID format");
            return response;
        }
        
        // Read existing DID document
        String existingDocStr = readState(did);
        if (existingDocStr == null) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("DID not found");
            return response;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> didDocument = objectMapper.readValue(existingDocStr, Map.class);
        
        // Update fields
        didDocument.put("updated", Instant.now().toString());
        
        if (ccRequest.getParams().containsKey("controller")) {
            didDocument.put("controller", ccRequest.getParams().get("controller"));
        }
        
        if (ccRequest.getParams().containsKey("publicKey")) {
            Map<String, Object> verificationMethod = new HashMap<>();
            verificationMethod.put("id", did + "#key-1");
            verificationMethod.put("type", "Ed25519VerificationKey2020");
            verificationMethod.put("controller", did);
            verificationMethod.put("publicKeyBase58", ccRequest.getParams().get("publicKey"));
            didDocument.put("verificationMethod", verificationMethod);
        }
        
        // Store updated document
        storeState(did, objectMapper.writeValueAsString(didDocument));
        
        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(objectMapper.writeValueAsString(didDocument));
        return response;
    }

    public CCResponse deactivate(CCRequest ccRequest) throws JsonProcessingException {
        log.info("Deactivating DID document");
        
        String did = ccRequest.getParams().get("did");
        if (did == null || !did.startsWith(DID_PREFIX)) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("Invalid DID format");
            return response;
        }
        
        // Read existing DID document
        String existingDocStr = readState(did);
        if (existingDocStr == null) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("DID not found");
            return response;
        }
        
        @SuppressWarnings("unchecked")
        Map<String, Object> didDocument = objectMapper.readValue(existingDocStr, Map.class);
        
        // Update status and timestamp
        didDocument.put("status", "deactivated");
        didDocument.put("updated", Instant.now().toString());
        
        // Store updated document
        storeState(did, objectMapper.writeValueAsString(didDocument));
        
        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(objectMapper.writeValueAsString(didDocument));
        return response;
    }
} 