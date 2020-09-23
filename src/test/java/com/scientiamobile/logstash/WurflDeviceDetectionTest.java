package com.scientiamobile.logstash;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.Event;
import org.jruby.RubyString;
import org.junit.BeforeClass;
import org.junit.Test;
import org.logstash.plugins.ConfigurationImpl;

import java.util.*;

import static com.scientiamobile.logstash.Config.TARGET_CONFIG;
import static org.junit.Assert.*;

/*
 NOTE: this test class needs to connect to a running WURFL Microservice server
 */
public class WurflDeviceDetectionTest {

    private static String testHost = "localhost";
    private static String testPort = "8080";

    @BeforeClass
    public static void init(){
        String h = System.getenv("TEST_HOST");
        String p = System.getenv("TEST_PORT");
        if(h!= null && h.length() >0){
            testHost = h;
        }

        if(p!= null && p.length() >0){
            testPort = p;
        }
        System.out.println("TEST HOST: " + testHost);
        System.out.println("TEST PORT: " + testPort);
    }

    @Test
    public void filterTestWithFullConfig(){

        List<String> staticCaps = new ArrayList<>();
        staticCaps.add("brand_name");
        staticCaps.add("model_name");

        List<String> vCaps = new ArrayList<>();
        vCaps.add("form_factor");

        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("cache_size", new Long(10000));
        testConfig.put("source", "headers");
        testConfig.put("inject_wurfl_id", Boolean.TRUE);
        testConfig.put("inject_wurfl_info", Boolean.TRUE);
        testConfig.put("inject_wurfl_api_version", Boolean.TRUE);
        testConfig.put("static_capabilities", staticCaps);
        testConfig.put("virtual_capabilities", vCaps);
        testConfig.put("scheme", "http");
        testConfig.put("host", testHost);
        testConfig.put("port", testPort);

        Configuration config = new ConfigurationImpl(testConfig);
        WurflDeviceDetection filter = new WurflDeviceDetection("test_id", config, null);

        // Create mock event
        Event e = new org.logstash.Event();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.1; ASUS_Z00VD Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/47.0.2526.100 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/108.0.0.17.68;]");
        e.setField("headers", headers);
        Collection<Event> events = filter.filter(Collections.singletonList(e), null);
        Collection<Event> enrichedEvents = filter.filter(events, null);
        assertNotNull(enrichedEvents);
        assertEquals(1, enrichedEvents.size());
        enrichedEvents.forEach(event -> {
            Map<String, Object> map = event.getData();
            assertNotNull(map);
            Object target = map.get(TARGET_CONFIG);
            assertNotNull(target);
            Map<String, RubyString> targetMap = (Map<String, RubyString>)target;
            assertEquals("asus_z00vd_ver1", targetMap.get("wurfl_id").asJavaString());
            assertEquals("Asus", targetMap.get("brand_name").asJavaString());
            assertEquals("Z00VD", targetMap.get("model_name").asJavaString());
            assertEquals("Smartphone", targetMap.get("form_factor").asJavaString());
            assertEquals("asus_z00vd_ver1", targetMap.get("wurfl_id").asJavaString());
            assertNotNull(targetMap.get("wurfl_api_version"));
            assertNotNull(targetMap.get("wurfl_info"));

        });
    }

    @Test
    public void filterTestWithNoCapsInConfig(){

        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("cache_size", new Long(10000));
        testConfig.put("source", "headers");
        testConfig.put("inject_wurfl_id", Boolean.TRUE);
        testConfig.put("inject_wurfl_info", Boolean.TRUE);
        testConfig.put("inject_wurfl_api_version", Boolean.TRUE);
        testConfig.put("scheme", "http");
        testConfig.put("host", testHost);
        testConfig.put("port", testPort);

        Configuration config = new ConfigurationImpl(testConfig);
        WurflDeviceDetection filter = new WurflDeviceDetection("test_id", config, null);

        // Create mock event
        Event e = new org.logstash.Event();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.1; ASUS_Z00VD Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/47.0.2526.100 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/108.0.0.17.68;]");
        e.setField("headers", headers);
        Collection<Event> events = filter.filter(Collections.singletonList(e), null);
        Collection<Event> enrichedEvents = filter.filter(events, null);
        assertNotNull(enrichedEvents);
        assertEquals(1, enrichedEvents.size());
        enrichedEvents.forEach(event -> {
            Map<String, Object> map = event.getData();
            assertNotNull(map);
            Object target = map.get(TARGET_CONFIG);
            assertNotNull(target);
            Map<String, RubyString> targetMap = (Map<String, RubyString>)target;
            assertNotNull(targetMap.get("wurfl_api_version"));
            assertNotNull(targetMap.get("wurfl_info"));
            // When a list of capabilities is NOT specified, all of them are returned
            assertTrue(targetMap.size() >= 10);
        });
    }

    @Test
    public void filterTestWithHttpHeadersConfig(){

        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("cache_size", new Long(10000));
        testConfig.put("source", "headers");
        testConfig.put("inject_wurfl_id", Boolean.TRUE);
        testConfig.put("inject_wurfl_info", Boolean.TRUE);
        testConfig.put("inject_wurfl_api_version", Boolean.TRUE);
        testConfig.put("scheme", "http");
        testConfig.put("host", testHost);
        testConfig.put("port", testPort);

        Configuration config = new ConfigurationImpl(testConfig);
        WurflDeviceDetection filter = new WurflDeviceDetection("test_id", config, null);

        // Create mock event
        Event e = new org.logstash.Event();
        Map<String,String> headers = new HashMap<>();
        headers.put("http_User_Agent".toLowerCase(), "Mozilla/5.0 (Linux; Android 5.1; ASUS_Z00VD Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/47.0.2526.100 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/108.0.0.17.68;]");
        headers.put("X_OperaMini_Phone_UA".toLowerCase(), "test_x_header");
        e.setField("headers", headers);
        Collection<Event> events = filter.filter(Collections.singletonList(e), null);
        Collection<Event> enrichedEvents = filter.filter(events, null);
        assertNotNull(enrichedEvents);
        assertEquals(1, enrichedEvents.size());
        enrichedEvents.forEach(event -> {
            Map<String, Object> map = event.getData();
            assertNotNull(map);
            Object target = map.get(TARGET_CONFIG);
            assertNotNull(target);
            Map<String, RubyString> targetMap = (Map<String, RubyString>)target;
            assertNotNull(targetMap.get("wurfl_api_version"));
            assertNotNull(targetMap.get("wurfl_info"));
            // When a list of capabilities is NOT specified, all of them are returned
            assertTrue(targetMap.size() >= 10);
        });
    }

    @Test
    public void filterTestWithNoWurflInfoInConfig(){

        // This test does not add WURFL ID nor WURFL Info and version strings
        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("cache_size", new Long(10000));
        testConfig.put("source", "headers");
        testConfig.put("scheme", "http");
        testConfig.put("host", testHost);
        testConfig.put("port", testPort);
        testConfig.put("inject_wurfl_id", "false"); // we need to specify this because default is true

        Configuration config = new ConfigurationImpl(testConfig);
        WurflDeviceDetection filter = new WurflDeviceDetection("test_id", config, null);

        // Create mock event
        Event e = new org.logstash.Event();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.1; ASUS_Z00VD Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/47.0.2526.100 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/108.0.0.17.68;]");
        e.setField("headers", headers);
        Collection<Event> events = filter.filter(Collections.singletonList(e), null);
        Collection<Event> enrichedEvents = filter.filter(events, null);
        assertNotNull(enrichedEvents);
        assertEquals(1, enrichedEvents.size());
        enrichedEvents.forEach(event -> {
            Map<String, Object> map = event.getData();
            assertNotNull(map);
            Object target = map.get(TARGET_CONFIG);
            assertNotNull(target);
            Map<String, RubyString> targetMap = (Map<String, RubyString>)target;
            assertNull(targetMap.get("wurfl_api_version"));
            assertNull(targetMap.get("wurfl_info"));
            assertNull(targetMap.get("wurfl_id"));
            // When a list of capabilities is NOT specified, all of them are returned
            assertTrue(targetMap.size() >= 10);
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void filterTestWithNoServerConfig(){

        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("cache_size", new Long(10000));
        testConfig.put("source", "headers");

        Configuration config = new ConfigurationImpl(testConfig);
        new WurflDeviceDetection("test_id", config, null);
    }

    @Test
    public void filterTestWithoutSourceConfig(){

        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("scheme", "http");
        testConfig.put("host", testHost);
        testConfig.put("port", testPort);

        Configuration config = new ConfigurationImpl(testConfig);
        WurflDeviceDetection filter = new WurflDeviceDetection("test_id", config, null);

        // Create mock event
        Event e = new org.logstash.Event();
        Map<String, String> headers = new HashMap<>();
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 5.1; ASUS_Z00VD Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/47.0.2526.100 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/108.0.0.17.68;]");
        e.setField("headers", headers);
        Collection<Event> events = filter.filter(Collections.singletonList(e), null);
        Collection<Event> enrichedEvents = filter.filter(events, null);
        assertNotNull(enrichedEvents);
        assertEquals(1, enrichedEvents.size());
        enrichedEvents.forEach(event -> {
            Map<String, Object> map = event.getData();
            assertNotNull(map);
            Object target = map.get(TARGET_CONFIG);
            assertNotNull(target);
            Map<String, RubyString> targetMap = (Map<String, RubyString>)target;
            assertNull(targetMap.get("wurfl_api_version"));
            assertNull(targetMap.get("wurfl_info"));
            // When a list of capabilities is NOT specified, all of them are returned
            assertTrue(targetMap.size() >= 10);
        });

        // Case two, an event with a different source name ("agent")
        e = new org.logstash.Event();
        // We put the data in "headers" source, thus test will pass because "headers" is the default name for source
        e.setField("agent", "Mozilla/5.0 (Linux; Android 5.1; ASUS_Z00VD Build/LMY47I; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/47.0.2526.100 Mobile Safari/537.36 [FB_IAB/FB4A;FBAV/108.0.0.17.68;]");
        events = filter.filter(Collections.singletonList(e), null);
        enrichedEvents = filter.filter(events, null);
        enrichedEvents.forEach(event -> {
            Map<String, Object> map = event.getData();
            assertNotNull(map);
            Object target = map.get(TARGET_CONFIG);
            // In this case, since "headers" has not been found, the event won't be enriched with WURFL data, which results in an empty "wurfl" data element
            assertNull(target);

        });
    }

    @Test
    public void filterTestWithWrongCacheConfig(){

        Map<String, Object> testConfig = new HashMap<>();
        testConfig.put("cache_size", new Long(-1));
        testConfig.put("source", "headers");

        Configuration c = new ConfigurationImpl(testConfig);
        // Wrong cache size value, default value is used
        Config config = new Config(c);
        assertEquals(Config.DEFAULT_CACHE_SIZE.intValue(), config.getCacheSize().intValue());
    }
}
