### WURFL Logstash plugin

This project contains a java plugin for Logstash that enriches a stream of data with device detection data obtained via WURFL Microservice.

This plugin requires Java 8+, Gradle 5.x or above and has been tested with Logstash 8.0.0 and 7.6.x

## Compile the project

From the root of the project do ` ./gradlew gem`
A file `logstash-filter-logstash_filter_wurfl_device_detection-x.y.z.gem`, where x.y.z version number
is the one defined in the `VERSION` file.

## Install the plugin on logstash

  From the logstash installation `bin` directory execute
  `./logstash-plugin install --local <plugin_project_home>/logstash-filter-logstash_filter_wurfl_device_detection-x.y.z.gem`
  
  Please note that this plugin requires a `stdin` plugin as specified in the sample configuration file
  `wurfl_filter.conf`; also note that the aforementioned file is a sample: you will want to create your own
  production configuration file.
  
  ## Sample Logstash execution with WURFL device detection plugin - user-agent list file example
  Scenario: we have a file with a list of user-agent strings. We want to output some device detection data for each input user-agent.
  We can execute logstash sending the user-agent to its standard input via pipe, like this:
  
  `head -n <number of user-agents to send> <path_to_user_agent_list> | ./logstash -f <path_to_configuration>.conf>`
  
  In case you use the sample configuration in the file wurfl_filter.conf, you'll get an output like this:
  
  ```
{
    "@timestamp" => 2020-03-10T15:50:19.917Z,
      "message" => "Mozilla/5.0 (Linux; Android 5.1; DL718M Build/LMY47I) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/43.0.2357.93 Safari/537.36",
      "@version" => "1",
      "host" => "my-laptop",
      "wurfl" => {
        "form_factor" => "Tablet",
         "brand_name" => "Digiland",
         "wurfl_id" => "digiland_dl718m_ver1",
         "model_name" => "DL718M"
    }
```

### Example configuration 1 - Input file with a list of user-agent strings

```
input {
  stdin { }
}
filter {
  logstash_filter_wurfl_device_detection {
    cache_size => 300000
    inject_wurfl_id => true
    inject_wurfl_info => false
    inject_wurfl_api_version => false
    static_capabilities => ["model_name", "brand_name"]
    virtual_capabilities => ["form_factor"]
    scheme => "http"
    host => "localhost"
    port => "8080"
  }
}
output {
  stdout { codec => rubydebug }
}
```

## Sample Logstash execution with WURFL device detection plugin - header map received from HTTP input
Scenario: we configure logstash to receive HTTP request info which we want to enrich with WURFL data.
We execute logstash:

`./logstash -f <path_to_configuration>.conf>`

The input configuration for this file is defined in http input configuration, ie: 
```
input {
  http {
    host => "0.0.0.0"
    port => "19080"
  }
}
filter {
  logstash_filter_wurfl_device_detection {
    source => "headers"
    cache_size => 300000
    inject_wurfl_id => true
    inject_wurfl_info => false
    inject_wurfl_api_version => false
    scheme => "http"
    host => "localhost"
    port => "8080"
  }
}
output {
  stdout { codec => rubydebug }
}
```

The http input plugin receives an http request to the specified host and port, with a payload map that contains the HTTP headers
to be analyzed by the WURFL plugin.
Note that the `source` name is `headers`. Also note that you can configure the logstash input as you want,
but if you want the WURFL plugin to work with headers, you must configure it so that it uses an header map.

- `stdin` and `stdout` define which input and output plugin will be used: in the first scenario we use the standard input, in the second scenario
 we use the HTTP input plugin, while in both scenarios we use the ruby debug console as output.
- `cache_size` (integer) is the size of the WURFL Microservice client cache. Defaults to 100000
- `inject_wurfl_id` defines whether `wurfl_id` will be added to enriched output (defaults to true)
- `inject_wurfl_info` defines whether `wurfl_info` will be added to enriched output (defaults to false)
- `inject_wurfl_api_version` defines whether `wurfl_api_version` will be added to enriched output (defaults to false)
- `static_capabilities` defines the list of static capabilities that you want to add to the enriched output (defaults to all)
- `virtual_capabilities` defines the list of virtual capabilities that you want to add to the enriched output (defaults to all)
- `scheme` defines the connection scheme to use to connect to WURFL Microservice server (currently only HTTP is supported)
- `host` host/ip address of the WURFL Microservice server (defaults to localhost)
- `port` port of the WURFL Microservice server (defaults to 80)


  
    
   
