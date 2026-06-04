package com.example.javaee_ecomorder.common.filter;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

public class XssWrapper extends HttpServletRequestWrapper {

    public XssWrapper(HttpServletRequest request) {
        super(request);
    }

    @Override
    public String[] getParameterValues(String name) {
        String[] values = super.getParameterValues(name);
        if (values == null) {
            return null;
        }
        return Arrays.stream(values).map(this::clean).toArray(String[]::new);
    }

    @Override
    public String getParameter(String name) {
        String value = super.getParameter(name);
        return value == null ? null : clean(value);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return super.getParameterMap().entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        e -> Arrays.stream(e.getValue()).map(this::clean).toArray(String[]::new)
                ));
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        return value
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#x27;")
                .replace("/", "&#x2F;");
    }
}
