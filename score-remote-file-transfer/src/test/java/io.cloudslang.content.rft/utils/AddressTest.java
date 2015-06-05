package io.cloudslang.content.rft.utils;

import com.hp.oo.content.commons.util.Address;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class AddressTest {


    String[] hostsName = new String[]{
            "myd-vm08596.hpswlabs.adapps.hp.com",
            "localhost",
    };

    String[] hostsIPV6 = new String[]{
            "fe80::d4f9:37d5:7879:532c%15",
            "fe80::d4f9:37d5:7879:532c",
            "2002:1033:b516:c:2e27:d7ff:fe31:3552",
            "::1",
            "[fe80::d4f9:37d5:7879:532c%15]",
            "[::1]",
            "[2002:1033:b516:c:2e27:d7ff:fe31:3552]",
            "[2002:1033:b516:c:2e27:d7ff:fe31:3552",
            "2002:1033:b516:c:2e27:d7ff:fe31:3552]",
    };
    String[] hostsIPV4 = new String[]{
            "16.22.69.18",
    };
    String[] portSeparators = new String[] {
            ":"
    };
    @Test
    public void testIPV6() {
        com.hp.oo.content.commons.util.Address address;
        for (String host : hostsIPV6) {
            address = new com.hp.oo.content.commons.util.Address(host, "23");
            host = stripBrackets(host);
            assertEquals(getAssetMessage("getBareHost", host, "23"), host, address.getBareHost());
            assertEquals(getAssetMessage("getPort", host, "23"), 23, address.getPort());
            if (!host.contains("%") && (host.startsWith("[") || host.split(":").length==8) ) {
                for (String portSeparator : portSeparators) {
                    address = new com.hp.oo.content.commons.util.Address(host + portSeparator + "23","");
                    assertEquals(getAssetMessage("getBareHost", host + portSeparator + "23", ""), host, address.getBareHost());
                    assertEquals(getAssetMessage("getPort", host + portSeparator + "23", ""),23, address.getPort());
                }
            }
        }

        String ipv6LiteralPlusPort = "2002:1033:b516:c:2e27:d7ff:fe31:3552:23";
        address = new com.hp.oo.content.commons.util.Address(ipv6LiteralPlusPort, "");
        assertEquals(getAssetMessage("getBareHost", ipv6LiteralPlusPort, ""), "2002:1033:b516:c:2e27:d7ff:fe31:3552", address.getBareHost());
        assertEquals(getAssetMessage("getPort", ipv6LiteralPlusPort, ""), 23, address.getPort());
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidIPv6MultiParentheses() {
        new com.hp.oo.content.commons.util.Address("[[2000:1234:433c:1:6503:f416:fa98:8301]]");
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidIPv6BadPort() {
        new com.hp.oo.content.commons.util.Address("[2000:1234:433c:1:6503:f416:fa98:8301]:P");
    }

    @Test
    public void testNineTokens() {
        com.hp.oo.content.commons.util.Address address = new com.hp.oo.content.commons.util.Address("2000:1234:433c:1:6503:f416:fa98:8301:443");
        assertEquals(address.getBareHost(), "2000:1234:433c:1:6503:f416:fa98:8301");
    }

    @Test
    public void testJapan() {
        com.hp.oo.content.commons.util.Address address = new com.hp.oo.content.commons.util.Address("五七桐");
        assertEquals(address.getBareHost(), "五七桐");
        address = new com.hp.oo.content.commons.util.Address("五七桐:99");
        assertEquals(address.getBareHost(), "五七桐");
    }

    @Test
    public void testBareHostIPv6() {
        com.hp.oo.content.commons.util.Address address = new com.hp.oo.content.commons.util.Address("[2000:1234:433c:1:6503:f416:fa98:8301]");
        assertEquals(address.getBareHost(), "2000:1234:433c:1:6503:f416:fa98:8301");
        address = new com.hp.oo.content.commons.util.Address("[2000:1234:433c:1:6503:f416:fa98:8301]:31");
        assertEquals(address.getBareHost(), "2000:1234:433c:1:6503:f416:fa98:8301");
        address = new com.hp.oo.content.commons.util.Address("localhost:31");
        assertEquals(address.getBareHost(), "localhost");

    }

    @Test
    public void testIPV4() {
        com.hp.oo.content.commons.util.Address address;
        for (String host : hostsIPV4) {
            address = new com.hp.oo.content.commons.util.Address(host, "23");
            assertEquals(getAssetMessage("getBareHost", host, "23"), host, address.getBareHost());
            assertEquals(getAssetMessage("getPort", host, "23"), 23, address.getPort());

            address = new com.hp.oo.content.commons.util.Address(host + ":23","");
            assertEquals(getAssetMessage("getBareHost", host + ":23", ""), host, address.getBareHost());
            assertEquals(getAssetMessage("getPort", host + ":23", ""), 23, address.getPort());
        }
    }

    @Test
    public void testName() {
        com.hp.oo.content.commons.util.Address address;
        for (String host : hostsName) {
            address = new com.hp.oo.content.commons.util.Address(host, "23");
            assertEquals(getAssetMessage("getBareHost", host, "23"), host, address.getBareHost());
            assertEquals(getAssetMessage("getPort", host, "23"), 23, address.getPort());

            address = new com.hp.oo.content.commons.util.Address(host + ":23","");
            assertEquals(getAssetMessage("getBareHost", host + ":23", ""), host, address.getBareHost());
            assertEquals(getAssetMessage("getPort", host + ":23", ""), 23, address.getPort());
        }
    }

    @Test
    public void testNoPort() {
        com.hp.oo.content.commons.util.Address address;
        List<String> hosts = new ArrayList<String>();
        hosts.addAll(Arrays.asList(hostsIPV4));
        hosts.addAll(Arrays.asList(hostsIPV6));
        hosts.addAll(Arrays.asList(hostsName));

        for (String host : hosts) {
            address = new com.hp.oo.content.commons.util.Address(host, "");
            host = stripBrackets(host);
            assertEquals(getAssetMessage("getBareHost", host, ""), host, address.getBareHost());

            address = new com.hp.oo.content.commons.util.Address(host, null);
            assertEquals(getAssetMessage("getBareHost", host, null),host, address.getBareHost());
        }
    }

    @Test
    public void testHostPortURI() {
        com.hp.oo.content.commons.util.Address address = new com.hp.oo.content.commons.util.Address("[::1]:8080");
        assertEquals(address.getHostAndPortForURI(),"[::1]:8080");
        address = new Address("::1");
        assertEquals(address.getHostAndPortForURI(),"[::1]");
    }

    private String getAssetMessage(String method, String host, String port) {
        return "testing Address." + method + " with host=\"" + host + "\" and port=\""+port+"\"";
    }

    private String stripBrackets(String host){
        if(!host.contains("[") && !host.contains("]")){
            return host;
        }
        host = host.replace("[", "");
        host = host.replace("]", "");
        return host;
    }

}
