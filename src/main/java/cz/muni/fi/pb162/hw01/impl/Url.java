package cz.muni.fi.pb162.hw01.impl;

import cz.muni.fi.pb162.hw01.DefaultPortResolver;
import cz.muni.fi.pb162.hw01.url.SmartUrl;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author Lukáš Bašista, 485473
 */
public class Url implements SmartUrl {
    private String url;

    /**
     * @param url adress
     */
    public Url(String url) {
        this.url = url;
    }

    @Override
    public String getAsString() {
        if (this.getProtocol().equals("") || this.getHost().equals("")) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        sb.append(this.getProtocol());
        sb.append("://");
        sb.append(this.getHost());
        DefaultPortResolver portResolver = new DefaultPortResolver();
        int protocolPort = portResolver.getPort(this.getProtocol());

        if (protocolPort != this.getPort()) {
            sb.append(":");
            sb.append(this.getPort());
        }

        sb.append("/");
        sb.append(this.getPath());

        if (!this.getFragment().equals("")) {
            sb.append("#");
            sb.append(this.getFragment());
        }

        if (!this.getQuery().equals("")) {
            sb.append("?");
            sb.append(this.getQuery());
        }

        if (sb.lastIndexOf("/") == sb.length() - 1) {
            sb.deleteCharAt(sb.length() - 1);
        }

        return sb.toString().trim();
    }

    @Override
    public String getAsRawString() {
        return this.url;
    }

    @Override
    public boolean isSameAs(SmartUrl url) {
        return url.getAsString().equals(this.getAsString());
    }

    @Override
    public boolean isSameAs(String url) {
        Url newUrl = new Url(url);
        return newUrl.getAsString().equals(this.getAsString());
    }

    @Override
    public String getHost() {
        String host = this.url.split("://", 2)[1].split("/", 2)[0].split(":, 2")[0].split(":")[0];
        String[] hostParts = host.split("\\.");

        if (hostParts[0].equals("www") && hostParts.length < 3) {
            return null;
        }

        if (!hostParts[0].equals("www") && hostParts.length < 2) {
            return null;
        }

        return host;
    }

    @Override
    public String getProtocol() {
        String[] splittedUrl = this.url.split("://", 2);

        if (splittedUrl.length < 1) {
            return null;
        }

        return splittedUrl[0];
    }

    @Override
    public int getPort() {
        String[] splittedUrl = this.url.split(":", 3);

        if (splittedUrl.length <= 2) {
            DefaultPortResolver portResolver = new DefaultPortResolver();
            return portResolver.getPort(splittedUrl[0]);
        } else {
            return Integer.parseInt(splittedUrl[2].split("/", 2)[0]);
        }
    }

    @Override
    public String getPath() {
        String[] splittedUrl = this.url.split("://", 2)[1].split("/", 2);

        if (splittedUrl.length < 2) {
            return "";
        }

        String[] pathParts = splittedUrl[1].split("[#?]", 2)[0].split("/");
        String[] parts = new String[pathParts.length];

        int position = 0;

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < pathParts.length; i++) {
            if (pathParts[i].equals("..")) {
                if ((i == 0) || (position - 1 < 0)) {
                    return "";
                }
                parts[position - 1] = null;
                position--;
            }
            if (!pathParts[i].equals("..") && !pathParts[i].equals(".")) {
                parts[position] = pathParts[i];
                position++;
            }
        }

        for (String part : parts) {
            if (part != null) {
                sb.append(part);
                sb.append("/");
            }
        }

        if (sb.length() < 1) {
            return "";
        }

        sb.deleteCharAt(sb.lastIndexOf("/"));

        return sb.toString().trim();

    }

    @Override
    public String getQuery() {
        int keyValue;

        String[] splittedUrl = this.url.split("\\?", 2);

        if (splittedUrl.length < 2) {
            return "";
        }
        if (splittedUrl[1].length() < 1) {
            return "";
        }

        String query = splittedUrl[1].split("#", 2)[0];
        SortedMap<Integer, String> map = new TreeMap<>();
        String[] parts = query.split("&", -1);

        for (String part : parts) {
            String[] value = part.split("=");

            if (value.length < 1) {
                continue;
            }

            keyValue = Integer.parseInt(value[0], 36);
            map.put(keyValue, part);

        }

        StringBuilder sb = new StringBuilder();

        for (Integer key : map.keySet()) {
            sb.append(map.get(key));
            sb.append("&");
        }

        sb.deleteCharAt(sb.length() - 1);

        return sb.toString().trim();

    }

    @Override
    public String getFragment() {
        String[] fragment = this.url.split("#", 2);

        if (fragment.length < 2) {
            return "";
        }

        return fragment[1];
    }
}
