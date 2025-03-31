package com.fwe.flyingwhiteelephant.service.plugin;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONFactory;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fwe.flyingwhiteelephant.service.AppContextUtil;
import com.fwe.flyingwhiteelephant.service.BlockchainStorageService;
import com.fwe.flyingwhiteelephant.service.crypto.Identity;
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

    /**
     * Creates a new DID document based on the provided request parameters.
     *
     * @param ccRequest The request containing DID document parameters
     * @return CCResponse containing the created DID document or error message
     * @throws JsonProcessingException if JSON processing fails
     */
    public CCResponse create(CCRequest ccRequest) throws JsonProcessingException {
        log.info("Creating new DID document");

        Map<String, String> params = ccRequest.getParams();
        String did = params.get("key");
        String didDocumentStr;

        // Generate or use provided DID
        if (!did.startsWith(DID_PREFIX)) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("Invalid DID format. Must start with " + DID_PREFIX);
            return response;
        }

        // Check if DID already exists
        if (readState(did) != null) {
            var response = new CCResponse();
            response.setStatus("error");
            response.setResult("DID already exists");
            return response;
        }

        // Create DID Document with basic structure
        Map<String, Object> didDocument = new HashMap<>();
        didDocument.put("id", did);
        didDocument.put("created", Instant.now().toString());
        didDocument.put("updated", Instant.now().toString());
        didDocument.put("status", "active");

        // Add controller if provided
        didDocument.put("controller", did); // Self-controlled by default

        String identityContent = params.get("value");
        // use JSON2 to parse the identity content

        String pkContent = JSONObject.parseObject(identityContent).getString("certificate");
        // Add verification method if certificate is provided
        Map<String, Object> verificationMethod = new HashMap<>();
        verificationMethod.put("id", did + "#key-1");
        verificationMethod.put("type", "Ed25519VerificationKey2020");
        verificationMethod.put("controller", didDocument.get("controller"));
        if (pkContent != null) {
            verificationMethod.put("publicKeyBase58", pkContent);
        }
        didDocument.put("verificationMethod", verificationMethod);
        // Store DID Document
        didDocumentStr = objectMapper.writeValueAsString(didDocument);
        storeState(did, didDocumentStr);

        var response = new CCResponse();
        response.setStatus("success");
        response.setResult(didDocumentStr);
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