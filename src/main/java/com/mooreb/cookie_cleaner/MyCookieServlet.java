package com.mooreb.cookie_cleaner;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyCookieServlet extends HttpServlet {

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final Mode mode = getMode(request);
        final List<Cookie> cookies = getAllCookies(request);
        final int numCookies = cookies.size();
        final long approxCookieBytes = computeApproxBytesForCookies(cookies);
        Cookie addedCookie = null;
        switch(mode) {
            case ADD:
                addedCookie = addCookie(response);
                break;
            case CLEAR:
                clearCookies(request, response, cookies);
                break;
            case REPORT:
                break;
            default:
                throw new UnsupportedOperationException("should not reach here");
        }

        response.addHeader("Cache-Control", "no-cache");

        final PrintWriter printWriter = response.getWriter();
        printWriter.println("<pre>");
        printWriter.println("mode is " + mode);
        if(null != addedCookie) printWriter.println("added cookie " + formatCookie(addedCookie));
        final String suffix = ((1 == numCookies) ? "" : "s");
        printWriter.println("" + numCookies + " input cookie" + suffix + ": ");
        for(final Cookie cookie : cookies) {
            printWriter.println(formatCookie(cookie));
        }
        printWriter.println("approx bytes: " + approxCookieBytes);
        printWriter.println("</pre>");
        printWriter.flush();
        response.setStatus(200);
        response.flushBuffer();
    }

    private Cookie addCookie(HttpServletResponse response) {
        final long now = System.currentTimeMillis();
        final Date nowDate = new Date();
        final String nowString = nowDate.toString().replace(" ", "_");
        Cookie nowCookie = new Cookie("now-" + now, nowString);
        response.addCookie(nowCookie);
        return nowCookie;
    }

    private void clearCookies(final HttpServletRequest request, final HttpServletResponse response, List<Cookie> cookies) {
        for(final Cookie cookie : cookies) {
            final Cookie removalCookie = new Cookie(cookie.getName(), "deleted");
            removalCookie.setMaxAge(0);
            removalCookie.setPath(cookie.getPath());
            final String inputDomain = cookie.getDomain();
            if(null != inputDomain) {
                removalCookie.setDomain(inputDomain);
            }
            removalCookie.setSecure(cookie.getSecure());
            removalCookie.setVersion(cookie.getVersion());
            response.addCookie(removalCookie);
        }
    }

    private List<Cookie> getAllCookies(final HttpServletRequest request) {
        final Cookie[] allCookies = request.getCookies();
        if(null == allCookies) return Collections.emptyList();
        final List<Cookie> retval = Arrays.asList(allCookies);
        return retval;
    }

    private static Map<String, String> splitQuery(final String query) throws UnsupportedEncodingException {
        if(null == query) return Collections.emptyMap();
        Map<String, String> query_pairs = new HashMap<>();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(
                    URLDecoder.decode(pair.substring(0, idx), "UTF-8"),
                    URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return Collections.unmodifiableMap(query_pairs);
    }

    private Mode getMode(final HttpServletRequest request) {
        final String queryString = request.getQueryString();
        Map<String, String> query;
        try {
            query = splitQuery(queryString);
        }
        catch(UnsupportedEncodingException e) {
            return Mode.REPORT;
        }

        if(query.containsKey("add")) {
            final String addValue = query.get("add");
            if("0".equals(addValue) || "false".equalsIgnoreCase(addValue) || "no".equals(addValue)) {
                return Mode.REPORT;
            }
            else {
                return Mode.ADD;
            }
        }

        if(query.containsKey("clear")) {
            final String clearValue = query.get("clear");
            if("0".equals(clearValue) || "false".equalsIgnoreCase(clearValue) || "no".equals(clearValue)) {
                return Mode.REPORT;
            }
            else {
                return Mode.CLEAR;
            }
        }
        return Mode.REPORT;
    }

    private static String formatCookie(final Cookie cookie) {
        final StringBuilder sb = new StringBuilder();
        sb.append("{Cookie: ");
        sb.append("name=").append(cookie.getName()).append(", ");
        sb.append("value=").append(cookie.getValue()).append(", ");
        sb.append("expires=").append(cookie.getMaxAge()).append(", ");
        sb.append("path=").append(cookie.getPath()).append(", ");
        sb.append("domain=").append(cookie.getDomain()).append(", ");
        sb.append("secure=").append(cookie.getSecure()).append(", ");
        sb.append("version=").append(cookie.getVersion());
        sb.append("}");
        return sb.toString();
    }

    private long computeApproxBytesForCookies(final List<Cookie> cookies) {
        if((null == cookies) || cookies.isEmpty()) return 0L;
        long retval = 0L;
        final String header = "Cookie: ";
        retval += header.length();
        for(final Cookie cookie : cookies) {
            final String cookieName = cookie.getName();
            final String cookieValue = cookie.getValue();
            retval += cookieName.length();
            retval += 1; // equals
            retval += cookieValue.length();
            retval += 1; // semicolon
            retval += 1; // space
        }
        return retval;
    }

}
