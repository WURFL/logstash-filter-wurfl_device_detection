### WURFL Logstash plugin

This project contains a java plugin for Logstash that enriches a collection of event data with device detection data obtained via WURFL Microservice (available on AWS or Azure marketplaces).

It is fully free and fully open source. The license is Apache 2.0, meaning you are pretty much free to use it however you want in whatever way.

This plugin requires Java 8+, Gradle 5.x or above and has been tested with Logstash 8.0.0 and 7.6.x

## Compile the project

From the root of the project do ` ./gradlew gem`
A file `logstash-filter-wurfl_device_detection-x.y.z.gem`, where x.y.z version number
is the one defined in the `VERSION` file.

## Install the plugin on logstash

  From the logstash installation `bin` directory execute
  `./logstash-plugin install --local <plugin_project_home>/logstash-filter-wurfl_device_detection-x.y.z.gem`
  
  Please note that this plugin requires a `stdin` plugin as specified in the sample configuration file
  `sample_config/wurfl_filter_config_with_http_input.conf`; also note that the aforementioned file is a sample: you will want to create your own
  production configuration file.
  
### Example configuration - using HTTP input plugin to receive HTTP request data
Scenario: we configure logstash to receive HTTP request info which we want to enrich with WURFL data.

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

Executing logstash:

`./logstash -f <path_to_configuration>.conf>`

and sending a HTTP request to its configured port (in this case 19080) like this (we just add user agent for simplicity):

`curl -H "User-Agent: Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3359.139 Mobile Safari/537.36" http://localhost:19080`

and we'll get an output that looks like this:
  
  ```
{
       "message" => "",
      "@version" => "1",
       "headers" => {
         "request_method" => "GET",
              "http_host" => "localhost:19080",
            "http_accept" => "*/*",
        "http_user_agent" => "Mozilla/5.0 (Linux; Android 8.0.0; Pixel 2 XL Build/OPD1.170816.004) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3359.139 Mobile Safari/537.36",
         "content_length" => "0",
           "request_path" => "/",
           "http_version" => "HTTP/1.1"
    },
    "@timestamp" => 2020-03-19T09:01:58.761Z,
          "host" => "127.0.0.1",
         "wurfl" => {
                            "is_robot" => "false",
                              "is_ios" => "false",
                  "advertised_browser" => "Chrome Mobile",
                         "form_factor" => "Smartphone",
                      "is_app_webview" => "false",
                           "is_mobile" => "true",
                          "is_android" => "true",
                          "brand_name" => "Google",
                   "resolution_height" => "2560",
                    "is_windows_phone" => "false",
                            "wurfl_id" => "google_pixel_2_xl_ver1",
                              "is_app" => "false",
                "complete_device_name" => "Google Pixel 2 XL",
                     "pointing_method" => "touchscreen",
                           "device_os" => "Android",
                 "advertised_app_name" => "Chrome browser",
                         "device_name" => "Google Pixel 2 XL",
                    "resolution_width" => "1440",
        "advertised_device_os_version" => "8.0.0",
                           "is_tablet" => "false",
                          "model_name" => "Pixel 2 XL",
                      "mobile_browser" => "Chrome Mobile",
                          "is_smarttv" => "false",
                      "is_touchscreen" => "true",
          "advertised_browser_version" => "65.0.3359.139",
                       "is_smartphone" => "true",
                "advertised_device_os" => "Android",
        [...]
    }
}

```

The http input plugin receives an http request to the specified host and port, with a payload map that contains the HTTP headers
to be analyzed by the WURFL plugin.
Note that the `source` name is `headers`. Also note that you can configure the logstash input as you want,
but if you want the WURFL plugin to work with headers, you must configure it so that it uses an header map.

- `stdin` and `stdout` define which input and output plugin will be used:  in our scenario
 we use the HTTP input plugin, while we use the ruby debug console as output.
- `cache_size` (integer) is the size of the WURFL Microservice client cache. Defaults to 100000
- `inject_wurfl_id` defines whether `wurfl_id` will be added to enriched output (defaults to true)
- `inject_wurfl_info` defines whether `wurfl_info` will be added to enriched output (defaults to false)
- `inject_wurfl_api_version` defines whether `wurfl_api_version` will be added to enriched output (defaults to false)
- `static_capabilities` defines the list of static capabilities that you want to add to the enriched output (defaults to all)
- `virtual_capabilities` defines the list of virtual capabilities that you want to add to the enriched output (defaults to all)
- `scheme` defines the connection scheme to use to connect to WURFL Microservice server (currently only HTTP is supported)
- `host` host/ip address of the WURFL Microservice server (defaults to localhost)
- `port` port of the WURFL Microservice server (defaults to 80)


  
    
   
