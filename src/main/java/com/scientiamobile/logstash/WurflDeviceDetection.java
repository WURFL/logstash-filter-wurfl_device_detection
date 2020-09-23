package com.scientiamobile.logstash;

import co.elastic.logstash.api.*;
import com.scientiamobile.wurfl.wmclient.*;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

import static com.scientiamobile.logstash.Config.*;

// Filter name uses underscores because classname and plugin names MUST match with the exception of casing and underscores
@LogstashPlugin(name = "wurfl_device_detection")
public class WurflDeviceDetection implements Filter {

    static final String WURFL_ID = "wurfl_id";
    static final String WURFL_INFO = "wurfl_info";
    static final String TAG_PARSE_FAILURE = "_wurflparsefailure";
    static final String WURFL_API_VERSION = "wurfl_api_version";
    private static Logger logger = LogManager.getLogger(WurflDeviceDetection.class);


    private final Config configuration;
    private String id;
    private WmClient wmClient;

    public WurflDeviceDetection(String id, Configuration lconfig, Context ctx) {
        this(id, lconfig);
    }

    public WurflDeviceDetection(String id, Configuration lconfig) {
        this.id = id;
        this.configuration = new Config(lconfig);
        initWmClient(configuration);
        logger.debug("WURFL microservice client created");
    }

    /*
     Initializes the WURFL Microservice client (server to which it connects, cache size, capabilities to be returned).
     */
    private void initWmClient(Config config) {
        try {
            wmClient = WmClient.create(config.getScheme(), config.getHost(), config.getPort(), "");
            wmClient.setCacheSize(config.getCacheSize());
            if (ArrayUtils.isNotEmpty(config.getStaticCapabilities())) {
                wmClient.setRequestedStaticCapabilities(config.getStaticCapabilities());
            }
            if (ArrayUtils.isNotEmpty(config.getVirtualCapabilities())) {
                wmClient.setRequestedVirtualCapabilities(config.getVirtualCapabilities());
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("[WURFL filter]: An error occurred initializing WURFL microservice client, please check your client configuration "
                    + e.getMessage(), e);
        }
    }

    /**
     * Extracts the HTTP request headers and uses them to query the WURFL Microservice server to get device detection information, which are added
     * to the event output data.
     *
     * @param collection          a collection of events
     * @param filterMatchListener this is unused
     * @return the collection of events, enriched with device detection data
     */
    @Override
    public Collection<Event> filter(Collection<Event> collection, FilterMatchListener filterMatchListener) {

        collection.forEach(event -> {
            Model.JSONInfoData info;
            try {
                info = wmClient.getInfo();
                Object o = event.getField(configuration.getSource());
                // This happens if source field has been wrongly configured (ie: source is "headers", but event data are in a field with another name
                if (o == null) {
                    logger.error("Message object taken from source  [" + configuration.getSource() + "] is null, check your plugin configuration");
                    event.tag(TAG_PARSE_FAILURE);
                    return;
                }

                Model.JSONDeviceData tdevice = null;
                final List<Model.JSONDeviceData> tdevices = new ArrayList<>();

                // We usually get a Map when a single event is sent
                if (o instanceof Map) {
                    HttpServletRequest req = new HttpServletRequestWrapper((Map) o);
                    tdevice = wmClient.lookupRequest(req);
                    final Model.JSONDeviceData device = tdevice;
                    // Device can be null if WM server version is < 2.1.0
                    if (device == null) {
                        event.tag(TAG_PARSE_FAILURE);
                        return;
                    }
                    Map<String, String> data = fillDataMap(info, device);
                    event.setField(TARGET_CONFIG, data);
                    // We usually get an ArrayList when we get an array or set of events
                } else if (o instanceof ArrayList) {
                    final List<Map<String, String>> dataMaps = new ArrayList<>();
                    ArrayList<Map> maps = (ArrayList<Map>) o;
                    for (Map m : maps) {
                        HttpServletRequest req = new HttpServletRequestWrapper((Map) m.get("headers"));
                        tdevice = wmClient.lookupRequest(req);
                        if (tdevice == null) {
                            continue;
                        }
                        tdevices.add(tdevice);
                        dataMaps.add(fillDataMap(info, tdevice));
                    }
                    event.setField(TARGET_CONFIG, dataMaps);
                } else {
                    logger.error("Message source name  [" + configuration.getSource() + "] is not of type String or Map, check your plugin configuration");
                }
            } catch (WmException e) {
                // Just log and go on
                logger.error("dropping event " + event.toString() + " due to " + e.getLocalizedMessage(), e);
            }
        });

        return collection;
    }

    private Map<String, String> fillDataMap(Model.JSONInfoData info, Model.JSONDeviceData device) {
        // Device has been detected, let's collect device info and capabilities
        Map<String, String> data = new HashMap<>();
        if (configuration.getInjectWurflId()) {
            data.put(WURFL_ID, device.capabilities.get(WURFL_ID));
        }

        if (configuration.getInjectWurflInfo()) {
            data.put(WURFL_INFO, info.getWurflInfo());
        }

        if (configuration.getInjectWurflApiVersion()) {
            data.put(WURFL_API_VERSION, info.getWurflApiVersion());
        }

        // Add capabilities to data
        if (configuration.getInjectWurflId()) {
            device.capabilities.keySet().forEach(name -> {
                data.put(name, device.capabilities.get(name));
            });
        } else { // inject_wurfl_id not enabled, we filter it from capability list
            device.capabilities.keySet().stream().filter(name -> !"wurfl_id".equals(name)).forEach(name -> {
                data.put(name, device.capabilities.get(name));
            });
        }
        return data;
    }

    @Override
    public Collection<PluginConfigSpec<?>> configSchema() {
        List<PluginConfigSpec<?>> configs = new ArrayList<>();
        configs.add(SOURCE_CONFIG);
        configs.add(CACHE_SIZE_CONFIG);
        configs.add(WM_CONN_HOST_CONFIG);
        configs.add(WM_CONN_SCHEME_CONFIG);
        configs.add(WM_CONN_PORT_CONFIG);
        configs.add(INJECT_WURFL_API_VERSION);
        configs.add(INJECT_WURFL_ID);
        configs.add(INJECT_WURFL_INFO);
        configs.add(STATIC_CAPABILITIES_CONFIG);
        configs.add(VIRTUAL_CAPABILITIES_CONFIG);

        return configs;
    }

    @Override
    public String getId() {
        return this.id;
    }
}
