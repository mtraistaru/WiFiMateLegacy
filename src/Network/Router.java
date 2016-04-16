package Network;

import Config.Base;
import Peer.CustomWiFiP2PDevice;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class Router {

    public static ConcurrentHashMap<String, CustomWiFiP2PDevice> routingTable = new ConcurrentHashMap<>();

    private static CustomWiFiP2PDevice customWiFiP2PDevice;

    public static void newClient(CustomWiFiP2PDevice c) {
        routingTable.put(c.getMacAddress(), c);
    }

    public static CustomWiFiP2PDevice getCustomWiFiP2PDevice() {
        return customWiFiP2PDevice;
    }

    public static void setCustomWiFiP2PDevice(CustomWiFiP2PDevice customWiFiP2PDevice) {
        Router.customWiFiP2PDevice = customWiFiP2PDevice;
    }

    private static String getClientIpAddress(CustomWiFiP2PDevice customWiFiP2PDevice) {

        if (Objects.equals(Router.customWiFiP2PDevice.getClientGroupOwnerMacAddress(), customWiFiP2PDevice.getClientGroupOwnerMacAddress())) {
            return customWiFiP2PDevice.getClientIpAddress();
        }

        CustomWiFiP2PDevice customWiFiP2PDeviceGroupOwner = routingTable.get(customWiFiP2PDevice.getClientGroupOwnerMacAddress());

        if (Objects.equals(Router.customWiFiP2PDevice.getClientGroupOwnerMacAddress(), Router.customWiFiP2PDevice.getMacAddress())) {
            if (Objects.equals(Router.customWiFiP2PDevice.getClientGroupOwnerMacAddress(), customWiFiP2PDevice.getClientGroupOwnerMacAddress()) || !customWiFiP2PDeviceGroupOwner.isDirectConnectionPeer()) {
                if (customWiFiP2PDeviceGroupOwner != null &&
                        !Objects.equals(Router.customWiFiP2PDevice.getClientGroupOwnerMacAddress(), customWiFiP2PDevice.getClientGroupOwnerMacAddress()) &&
                        !customWiFiP2PDeviceGroupOwner.isDirectConnectionPeer()) {
                    // implement propagation to all available group owners
                    return "0.0.0.0";
                }
            } else {
                // direct connection with the group owner, although not the same
                return customWiFiP2PDevice.getClientIpAddress();
            }
        } else if (customWiFiP2PDeviceGroupOwner != null) {
            // send it to the group owner
            return Base.GROUP_OWNER_IP_ADDRESS;
        }

        return "0.0.0.0";
    }

    public static byte[] serialiseRoutingTable() {
        StringBuilder serialisedRoutingTable = new StringBuilder();
        for (CustomWiFiP2PDevice customWiFiP2PDevice : routingTable.values()) {
            serialisedRoutingTable.append(customWiFiP2PDevice.toString());
            serialisedRoutingTable.append("\n");
        }
        return serialisedRoutingTable.toString().getBytes();
    }

    public static void deserialiseRoutingTableAndAdd(byte[] serialisedRoutingTable) {
        String serialisedRoutingTableString = new String(serialisedRoutingTable);
        String[] splitSerialisedRoutingTableString = serialisedRoutingTableString.split("\n");
        for (String customWiFiP2PDeviceString : splitSerialisedRoutingTableString) {
            CustomWiFiP2PDevice customWiFiP2PDevice = CustomWiFiP2PDevice.deserialiseCustomWiFiP2PDevice(customWiFiP2PDeviceString);
            Router.routingTable.put(customWiFiP2PDevice.getMacAddress(), customWiFiP2PDevice);
        }
    }

    public static String getClientIpAddress(String macAddress) {
        return routingTable.get(macAddress) != null ? getClientIpAddress(routingTable.get(macAddress)) : Base.GROUP_OWNER_IP_ADDRESS;
    }
}
