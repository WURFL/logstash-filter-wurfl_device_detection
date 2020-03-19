package com.scientiamobile.logstash;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.security.Principal;
import java.util.*;


public class HttpServletRequestWrapper implements HttpServletRequest {

    private static final String X_OPERAMINI_PHONE_UA_HEADER = "X-OperaMini-Phone-UA";
    private static final String DEVICE_STOCK_UA_HEADER = "Device-Stock-UA";
    private static final String USER_AGENT_HEADER = "User-Agent";
    private static final String X_UCBROWSER_DEVICE_UA_HEADER = "X-UCBrowser-Device-UA";
    private static final String ACCEPT_ENCODING_HEADER = "Accept-Encoding";
    private static final String X_REQUESTED_WITH_HEADER = "X-Requested-With";
    private static final String CAST_DEVICE_CAPABILITIES = "Cast-Device-Capabilities";

    private static  final List<String> importantHeaderNames = new ArrayList<>();

    static {
        importantHeaderNames.add(X_OPERAMINI_PHONE_UA_HEADER);
        importantHeaderNames.add(DEVICE_STOCK_UA_HEADER);
        importantHeaderNames.add(USER_AGENT_HEADER);
        importantHeaderNames.add(X_UCBROWSER_DEVICE_UA_HEADER);
        importantHeaderNames.add(ACCEPT_ENCODING_HEADER);
        importantHeaderNames.add(X_REQUESTED_WITH_HEADER);
        importantHeaderNames.add(CAST_DEVICE_CAPABILITIES);
    }

    private Map<String, String> _headers = new TreeMap<>();

    public HttpServletRequestWrapper(Map<String,String> m){
        if (m != null){

            m.keySet().forEach(name -> {
                if (importantHeaderNames.contains(name)){
                    _headers.put(name, m.get(name));
                } else { // let's check if headers are here in the for of user_agent
                    importantHeaderNames.forEach(impHeaderName -> {
                        String transformed = toLowerAndUnderscore(impHeaderName);
                        if (name.equals(transformed)) {
                            _headers.put(impHeaderName, m.get(transformed));
                        }
                    });
                }
            });
        }
        //System.out.println(_headers);
    }

    private String toLowerAndUnderscore(String value) {
        value = value.toLowerCase();
        value =  value.replaceAll("-", "_");
        // specific for user-agent
        if (value.equals("user_agent")){
            value = "http_user_agent";
        }
        return value;
    }

    @Override
    public String getAuthType() {
        return "";
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return  _headers.get(name);
    }

    @Override
    public Enumeration getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration getHeaderNames() {
        return Collections.enumeration(_headers.keySet());
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return null;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean create) {
        return null;
    }

    @Override
    public HttpSession getSession() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public Object getAttribute(String name) {
        return null;
    }

    @Override
    public Enumeration getAttributeNames() {
        return null;
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() {
        return null;
    }

    @Override
    public String getParameter(String name) {
        return null;
    }

    @Override
    public Enumeration getParameterNames() {
        return null;
    }

    @Override
    public String[] getParameterValues(String name) {
        return new String[0];
    }

    @Override
    public Map getParameterMap() {
        return null;
    }

    @Override
    public String getProtocol() {
        return null;
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return null;
    }

    @Override
    public String getRemoteHost() {
        return null;
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return null;
    }

    @Override
    public Enumeration getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public String getRealPath(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return null;
    }

    @Override
    public int getLocalPort() {
        return 0;
    }
}
