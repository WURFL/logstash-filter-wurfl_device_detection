package com.scientiamobile.logstash;

import co.elastic.logstash.api.Configuration;
import co.elastic.logstash.api.PluginConfigSpec;
import java.util.ArrayList;
import java.util.List;

/**
 * Holds configuration information for WURFL device detection Logstash plugin
 */
class Config {

    private static final List<Object> DEFAULT_VIRTUAL_CAPABILITIES = new ArrayList<>();
    private static List<Object> DEFAULT_STATIC_CAPABILITIES = new ArrayList<>();
    static final Integer DEFAULT_CACHE_SIZE = 100000;

    static final PluginConfigSpec<String> SOURCE_CONFIG =
            PluginConfigSpec.stringSetting("source", "headers");
    static final PluginConfigSpec<Long> CACHE_SIZE_CONFIG =
            PluginConfigSpec.numSetting("cache_size", DEFAULT_CACHE_SIZE);
    static final PluginConfigSpec<String> WM_CONN_SCHEME_CONFIG =
            PluginConfigSpec.stringSetting("scheme", "http");
    static final PluginConfigSpec<String> WM_CONN_HOST_CONFIG =
            PluginConfigSpec.stringSetting("host", "localhost");
    static final PluginConfigSpec<String> WM_CONN_PORT_CONFIG =
            PluginConfigSpec.stringSetting("port", "80");
    static final PluginConfigSpec<Boolean> INJECT_WURFL_ID =
            PluginConfigSpec.booleanSetting("inject_wurfl_id", true);
    static final PluginConfigSpec<Boolean> INJECT_WURFL_INFO =
            PluginConfigSpec.booleanSetting("inject_wurfl_info", false);
    static final PluginConfigSpec<Boolean> INJECT_WURFL_API_VERSION =
            PluginConfigSpec.booleanSetting("inject_wurfl_api_version", false);

    static final PluginConfigSpec<List<Object>> STATIC_CAPABILITIES_CONFIG =
            PluginConfigSpec.arraySetting("static_capabilities", DEFAULT_STATIC_CAPABILITIES, false, false);
    static final PluginConfigSpec<List<Object>> VIRTUAL_CAPABILITIES_CONFIG =
            PluginConfigSpec.arraySetting("virtual_capabilities", DEFAULT_VIRTUAL_CAPABILITIES, false, false);

    static final String TARGET_CONFIG = "wurfl";

    private String   source;
    private Integer  cacheSize;
    private Boolean  injectWurflId;
    private Boolean  injectWurflInfo;
    private Boolean  injectWurflApiVersion;
    private String[] staticCapabilities;
    private String[] virtualCapabilities;
    private String   scheme;
    private String   host;
    private String   port;

    public Config(Configuration lconfig){

        source = lconfig.get(SOURCE_CONFIG);

        // WM client connection info
        scheme = lconfig.get(WM_CONN_SCHEME_CONFIG);
        host   = lconfig.get(WM_CONN_HOST_CONFIG);
        port = lconfig.get(WM_CONN_PORT_CONFIG);

        // WM client Cache configuration
        cacheSize = lconfig.get(CACHE_SIZE_CONFIG).intValue();
        if (cacheSize <= 0) {
            cacheSize = DEFAULT_CACHE_SIZE;
        }

        // WM data inject configuration
        injectWurflId = lconfig.get(INJECT_WURFL_ID);
        injectWurflInfo = lconfig.get(INJECT_WURFL_INFO);
        injectWurflApiVersion = lconfig.get(INJECT_WURFL_API_VERSION);

        // WM capabilities configuration
        staticCapabilities = lconfig.get(STATIC_CAPABILITIES_CONFIG).toArray(new String[0]);
        virtualCapabilities = lconfig.get(VIRTUAL_CAPABILITIES_CONFIG).toArray(new String[0]);

    }

    public String getSource() {
        return source;
    }

    public Integer getCacheSize() {
        return cacheSize;
    }

    public Boolean getInjectWurflId() {
        return injectWurflId;
    }

    public Boolean getInjectWurflInfo() {
        return injectWurflInfo;
    }

    public Boolean getInjectWurflApiVersion() {
        return injectWurflApiVersion;
    }

    public String[] getStaticCapabilities() {
        return staticCapabilities;
    }

    public String[] getVirtualCapabilities() {
        return virtualCapabilities;
    }

    public String getScheme() {
        return scheme;
    }

    public String getHost() {
        return host;
    }

    public String getPort() {
        return port;
    }
}