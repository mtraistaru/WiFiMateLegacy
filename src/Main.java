import Network.Router;
import Peer.CustomWiFiP2PDevice;
import Peer.Packet;
import Peer.Receiver;
import Peer.Sender;

import java.util.Objects;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        System.out.println("Started...");

        Receiver receiver = new Receiver();
        new Thread(receiver).start();

        Sender sender = new Sender();
        new Thread(sender).start();

        String randomMacAddress = generateRandomMacAddress();

        try {
            CustomWiFiP2PDevice customWiFiP2PDevice = new CustomWiFiP2PDevice(randomMacAddress, "127.0.0.1", "mock", "00:00:00:00:00:00");
            Router.setCustomWiFiP2PDevice(customWiFiP2PDevice);
            Router.newClient(customWiFiP2PDevice);
        } catch (Exception exception) {
            System.out.println("Server crashed, please try again.");
            exception.printStackTrace();
        }

        Sender.addPacket(
                new Packet(
                        Packet.TYPE.HELLO,
                        new byte[0],
                        null,
                        randomMacAddress
                )
        );

        Scanner scanner = new Scanner(System.in);
        String input = "";

        boolean sendToSingle = false;
        String macAddress = "00:00:00:00:00:00";

        while (!Objects.equals(input, "quit")) {
            input = scanner.nextLine();
            if (sendToSingle) {
                sendToSingle = false;
                CustomWiFiP2PDevice customWiFiP2PDevice = Router.routingTable.get(macAddress);
                Sender.addPacket(
                        new Packet(
                                Packet.TYPE.MESSAGE,
                                input.getBytes(),
                                customWiFiP2PDevice.getMacAddress(),
                                randomMacAddress
                        )
                );
            } else if (input.startsWith("Send to ")) {
                macAddress = input.substring(8, input.length());
                sendToSingle = true;
            } else {
                for (CustomWiFiP2PDevice customWiFiP2PDevice : Router.routingTable.values()) {
                    if (!customWiFiP2PDevice.getMacAddress().equals(Router.getCustomWiFiP2PDevice().getMacAddress())) {
                        Sender.addPacket(
                                new Packet(
                                        Packet.TYPE.MESSAGE,
                                        input.getBytes(),
                                        customWiFiP2PDevice.getMacAddress(),
                                        randomMacAddress
                                )
                        );
                    }
                }
            }
        }
        scanner.close();
    }

    private static String generateRandomMacAddress() {
        Random random = new Random();
        byte[] macAddress = new byte[6];
        random.nextBytes(macAddress);
        macAddress[0] = (byte) (macAddress[0] & (byte) 254);
        StringBuilder stringBuilder = new StringBuilder(18);
        for (byte currentByte : macAddress) {
            if (stringBuilder.length() > 0) {
                stringBuilder.append(":");
            }
            stringBuilder.append(String.format("%02x", currentByte));
        }
        return stringBuilder.toString();
    }
}
