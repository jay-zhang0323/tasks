package com.example.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.config.Node;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.impl.ThrowableProxy;
import org.apache.logging.log4j.core.layout.AbstractStringLayout;
import org.apache.logging.log4j.core.util.StringBuilderWriter;

import org.apache.logging.log4j.util.ReadOnlyStringMap;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Plugin(name = "CustomGCPLayout", category = Node.CATEGORY, elementType = Layout.ELEMENT_TYPE)
public class CustomGCPLayout extends AbstractStringLayout {

    private static final String DEFAULT_EOL = "\r\n";

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    

    @PluginFactory
    public static CustomGCPLayout create() {
        return new CustomGCPLayout();
    }

    public CustomGCPLayout() {
        this(StandardCharsets.UTF_8);
    }

    protected CustomGCPLayout(Charset charset) {
        super(charset);
    }

    @Override
    public String toSerializable(LogEvent event) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("timestamp", event.getTimeMillis());
        map.put("customLayout","true");
        putIfNotNull("severity", event.getLevel().toString(), map);
        putIfNotNull("thread", event.getThreadName(), map);
        putIfNotNull("logger", event.getLoggerFqcn(), map);
        putIfNotNull("message", event.getMessage().getFormattedMessage(), map);
        putIfNotNull("exception", getThrowableAsString(event.getThrownProxy()), map);
        putIfNotNull("trace", event.getContextData().getValue("X-B3-TraceId"), map);
        putIfNotNull("spanId", event.getContextData().getValue("X-B3-SpanId"), map);


        try {
            StringBuilderWriter writer = new StringBuilderWriter();
            OBJECT_MAPPER.writeValue(writer, map);
            writer.append(DEFAULT_EOL);
            return writer.toString();
        } catch (Exception e) {
            LOGGER.error(e);
            return null;
        }
    }

    private String getThrowableAsString(ThrowableProxy thrownProxy) {
        if (thrownProxy != null) {
            return thrownProxy.getExtendedStackTraceAsString();
        }
        return null;
    }

    private void putIfNotNull(String key, String value, Map<String, Object> map) {
        if (value != null) {
            map.put(key, value);
        }
    }
}