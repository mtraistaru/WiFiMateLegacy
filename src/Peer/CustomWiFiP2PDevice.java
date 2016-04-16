package Peer;

public class CustomWiFiP2PDevice {

    private String macAddress;
    private String clientName;
    private String clientGroupOwnerMacAddress;
    private String clientIpAddress;
    private boolean isDirectConnectionPeer;

    public CustomWiFiP2PDevice(String macAddress, String clientIpAddress, String clientName, String clientGroupOwnerMacAddress) {
        this.setMacAddress(macAddress);
        this.setClientName(clientName);
        this.setClientIpAddress(clientIpAddress);
        this.setClientGroupOwnerMacAddress(clientGroupOwnerMacAddress);
        this.isDirectConnectionPeer = true;
    }

    public boolean isDirectConnectionPeer() {
        return isDirectConnectionPeer;
    }

    public String getClientGroupOwnerMacAddress() {
        return clientGroupOwnerMacAddress;
    }

    void setClientGroupOwnerMacAddress(String clientGroupOwnerMacAddress) {
        this.clientGroupOwnerMacAddress = clientGroupOwnerMacAddress;
    }

    private String getClientName() {
        return clientName;
    }

    private void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getMacAddress() {
        return macAddress;
    }

    private void setMacAddress(String macAddress) {
        this.macAddress = macAddress;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    private void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    @Override
    public String toString() {
        return getMacAddress() + "," + getClientIpAddress() + "," + getClientName() + "," + getClientGroupOwnerMacAddress();
    }

    public static CustomWiFiP2PDevice deserialiseCustomWiFiP2PDevice(String serialisedCustomWiFiP2PDevice) {
        String[] dividedSerializedCustomWiFiP2PDevice = serialisedCustomWiFiP2PDevice.split(",");
        return new CustomWiFiP2PDevice(
                dividedSerializedCustomWiFiP2PDevice[0],
                dividedSerializedCustomWiFiP2PDevice[1],
                dividedSerializedCustomWiFiP2PDevice[2],
                dividedSerializedCustomWiFiP2PDevice[3]);
    }
}
