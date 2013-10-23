package org.apache.ftpserver.ssl.impl;

import java.io.FileInputStream;
import java.security.KeyStore;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import junit.framework.TestCase;

public class ExtendedAliasKeymanagerTest extends TestCase {

    private KeyManager km;

    protected void setUp() throws Exception {
        KeyStore ks = KeyStore.getInstance("JKS");
        FileInputStream fis = new FileInputStream("src/test/resources/keymanager-test.jks");
        ks.load(fis, "".toCharArray());
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, "".toCharArray());
        km = kmf.getKeyManagers()[0];
    }

    public void testServerAliasWithAliasDSAKey() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, "dsakey");
        assertEquals("dsakey", akm.chooseServerAlias("DSA", null, null));
        assertEquals(null, akm.chooseServerAlias("RSA", null, null));
    }

    public void testServerAliasWithAliasRSAKey() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, "rsakey");
        assertEquals(null, akm.chooseServerAlias("DSA", null, null));
        assertEquals("rsakey", akm.chooseServerAlias("RSA", null, null));
    }

    public void testServerAliasWithoutAlias() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, null);
        assertEquals("dsakey", akm.chooseServerAlias("DSA", null, null));
        assertEquals("rsakey", akm.chooseServerAlias("RSA", null, null));
    }

    public void testServerAliasNonExistingKey() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, "nonexisting");
        assertEquals(null, akm.chooseServerAlias("DSA", null, null));
        assertEquals(null, akm.chooseServerAlias("RSA", null, null));
    }

    public void testEngineServerAliasWithAliasDSAKey() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, "dsakey");
        assertEquals("dsakey", akm.chooseEngineServerAlias("DSA", null, null));
        assertEquals(null, akm.chooseEngineServerAlias("RSA", null, null));
    }

    public void testEngineServerAliasWithAliasRSAKey() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, "rsakey");
        assertEquals(null, akm.chooseEngineServerAlias("DSA", null, null));
        assertEquals("rsakey", akm.chooseEngineServerAlias("RSA", null, null));
    }

    public void testEngineServerAliasWithoutAlias() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, null);
        assertEquals("dsakey", akm.chooseEngineServerAlias("DSA", null, null));
        assertEquals("rsakey", akm.chooseEngineServerAlias("RSA", null, null));
    }

    public void testEngineServerAliasNonExistingKey() throws Exception {
        ExtendedAliasKeyManager akm = new ExtendedAliasKeyManager(km, "nonexisting");
        assertEquals(null, akm.chooseEngineServerAlias("DSA", null, null));
        assertEquals(null, akm.chooseEngineServerAlias("RSA", null, null));
    }
}
