package ru.netology.service;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class Request {
    private final String method;
    private final String path;
    private final String rawPath;
    private final List<NameValuePair> nameValuePairs;
    private final Map<String, List<String>> paramsMap;

    public Request(String method, String rawPath) {
        this.method = method;
        this.rawPath = rawPath;

        String pathPart = rawPath;
        String queryString = null;
        int index = rawPath.indexOf('?');
        if (index != -1) {
            pathPart = rawPath.substring(0, index);
            queryString = rawPath.substring(index + 1);
        }
        this.path = pathPart;

        if (queryString != null && !queryString.isEmpty()) {
            this.nameValuePairs = URLEncodedUtils.parse(queryString, StandardCharsets.UTF_8);
        } else {
            this.nameValuePairs = Collections.emptyList();
        }

        this.paramsMap = new HashMap<>();
        for (NameValuePair pair : this.nameValuePairs) {
            paramsMap.computeIfAbsent(pair.getName(), k -> new ArrayList<>())
                    .add(URLDecoder.decode(pair.getValue()));
        }
    }

    public String getMethod() {
        return method;
    }

    public String getRawPath() {
        return rawPath;
    }

    public String getPath() {
        return this.path;
    }

    public String getQueryParameter(String name) {
        List<String> values = paramsMap.get(name);
        return values != null && !values.isEmpty() ? values.get(0) : null;
    }

    public List<String> getQueryParameterValue(String name) {
        List<String> values = paramsMap.get(name);
        return values != null ? List.copyOf(values) : Collections.emptyList();
    }
}
