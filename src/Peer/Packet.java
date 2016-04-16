package Peer;

public class Packet {

    public enum TYPE {
        HELLO,
        HELLO_ACKNOWLEDGED,
        MESSAGE,
        UPDATE
    }

    private byte[] data;
    private TYPE type;
    private String receiverMacAddress;
    private String senderMacAddress;
    private String senderIpAddress;
    private int timeToLive;

    public Packet(TYPE type, byte[] data, String receiverMacAddress, String senderMacAddress) {
        this.data = data;
        this.type = type;
        this.receiverMacAddress = receiverMacAddress;
        this.timeToLive = 3; // default initialisation
        if (receiverMacAddress == null) {
            this.receiverMacAddress = "00:00:00:00:00:00";
        }
        this.senderMacAddress = senderMacAddress;
    }

    private Packet(TYPE type, byte[] data, String receiverMacAddress, String senderMacAddress, int timeToLive) {
        this.data = data;
        this.type = type;
        this.receiverMacAddress = receiverMacAddress;
        if (this.receiverMacAddress == null) {
            this.receiverMacAddress = "00:00:00:00:00:00";
        }
        this.senderMacAddress = senderMacAddress;
        this.timeToLive = timeToLive;
    }

    byte[] getData() {
        return data;
    }

    TYPE getType() {
        return type;
    }

    String getReceiverMacAddress() {
        return receiverMacAddress;
    }

    String getSenderMacAddress() {
        return senderMacAddress;
    }

    String getSenderIpAddress() {
        return senderIpAddress;
    }

    public void setSenderIpAddress(String senderIpAddress) {
        this.senderIpAddress = senderIpAddress;
    }

    int getTimeToLive() {
        return timeToLive;
    }

    void setTimeToLive(int timeToLive) {
        this.timeToLive = timeToLive;
    }

    static byte[] getMacAsBytes(String macAddressBytesString) {
        String[] splitMacAddressBytesString = macAddressBytesString.split(":");
        byte[] macAddressBytes = new byte[6];
        for (int i = 0; i < splitMacAddressBytesString.length; i++) {
            macAddressBytes[i] = Integer.decode("0x" + splitMacAddressBytesString[i]).byteValue();
        }
        return macAddressBytes;
    }

    static String serialiseMacBytes(byte[] data, int startingIndex) {
        StringBuilder stringBuilder = new StringBuilder(18);
        for (int i = startingIndex; i < startingIndex + 6; i++) {
            byte b = data[i];
            if (stringBuilder.length() > 0) {
                stringBuilder.append(':');
            }
            stringBuilder.append(String.format("%02x", b));
        }
        return stringBuilder.toString();
    }

    byte[] serialisePacket() {
        byte[] serialisedPacked = new byte[1 + data.length + 13];
        serialisedPacked[0] = (byte) type.ordinal();
        serialisedPacked[1] = (byte) timeToLive;
        byte[] mac = getMacAsBytes(this.receiverMacAddress);
        System.arraycopy(mac, 0, serialisedPacked, 2, 6);
        mac = getMacAsBytes(this.senderMacAddress);
        System.arraycopy(mac, 0, serialisedPacked, 8, 6);
        System.arraycopy(data, 0, serialisedPacked, 14, serialisedPacked.length - 14);
        return serialisedPacked;
    }

    public static Packet deserialisePacket(byte[] serialisedPacked) {
        TYPE type = TYPE.values()[(int) serialisedPacked[0]];
        byte[] packedData = new byte[serialisedPacked.length - 14];
        int timeToLive = (int) serialisedPacked[1];
        String serialisedMacAddress = serialiseMacBytes(serialisedPacked, 2);
        String serialisedReceiverMacAddress = serialiseMacBytes(serialisedPacked, 8);
        System.arraycopy(serialisedPacked, 14, packedData, 0, serialisedPacked.length - 14);
        return new Packet(type, packedData, serialisedMacAddress, serialisedReceiverMacAddress, timeToLive);
    }

    @Override
    public String toString() {
        return "Type" + getType().toString() + "receiver:" + getReceiverMacAddress() + "sender:" + getSenderMacAddress();
    }
}
