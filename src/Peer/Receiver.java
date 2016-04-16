package Peer;

import Config.Base;
import Network.Router;
import Network.StreamReceiverTCP;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Receiver implements Runnable {

    private static int client = 0;
    private Packet packet;

    public void run() {

        ConcurrentLinkedQueue<Packet> packetConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        new Thread(new StreamReceiverTCP(Base.RECEIVE_PORT, packetConcurrentLinkedQueue)).start();


        do {
            while (packetConcurrentLinkedQueue.isEmpty()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            packet = packetConcurrentLinkedQueue.remove();

            if (packet.getType().equals(Packet.TYPE.HELLO)) {
                Router.routingTable.values()
                        .stream()
                        .filter(
                                customWiFiP2PDevice ->
                                        !customWiFiP2PDevice.getMacAddress().equals(Router.getCustomWiFiP2PDevice().getMacAddress())
                                                && !customWiFiP2PDevice.getMacAddress().equals(packet.getSenderMacAddress())
                        )
                        .forEachOrdered(
                                customWiFiP2PDevice -> {
                                    Packet update = new Packet(
                                            Packet.TYPE.UPDATE,
                                            Packet.getMacAsBytes(packet.getSenderMacAddress()),
                                            customWiFiP2PDevice.getMacAddress(),
                                            Router.getCustomWiFiP2PDevice().getMacAddress()
                                    );
                                    Sender.addPacket(update);
                                });
                Router.routingTable.put(
                        packet.getSenderMacAddress(),
                        new CustomWiFiP2PDevice(
                                packet.getSenderMacAddress(),
                                packet.getSenderIpAddress(),
                                packet.getSenderMacAddress(),
                                Router.getCustomWiFiP2PDevice().getMacAddress()
                        )
                );
                byte[] routingTable = Router.serialiseRoutingTable();

                Packet acknowledgedPacked = new Packet(
                        Packet.TYPE.HELLO_ACKNOWLEDGED,
                        routingTable,
                        packet.getSenderMacAddress(),
                        Router.getCustomWiFiP2PDevice().getMacAddress()
                );
                Sender.addPacket(acknowledgedPacked);
            } else {
                if (packet.getReceiverMacAddress().equals(Router.getCustomWiFiP2PDevice().getMacAddress())) {
                    if (packet.getType().equals(Packet.TYPE.HELLO_ACKNOWLEDGED)) {
                        Router.deserialiseRoutingTableAndAdd(packet.getData());
                        Router.getCustomWiFiP2PDevice().setClientGroupOwnerMacAddress(packet.getSenderMacAddress());
                        System.out.println("Connected.\n");
                        System.out.println("Peers:\n");
                        for (CustomWiFiP2PDevice customWiFiP2PDevice : Router.routingTable.values()) {
                            System.out.println(client++ + ": " + customWiFiP2PDevice.getMacAddress());
                        }
                        System.out.println("To send a message, type de recipient\'s MAC address.");
                        System.out.println("Type the message on the next line.");
                    } else if (packet.getType().equals(Packet.TYPE.UPDATE)) {
                        String peerMacAddress = Packet.serialiseMacBytes(packet.getData(), 0);
                        Router.routingTable.put(
                                peerMacAddress,
                                new CustomWiFiP2PDevice(
                                        peerMacAddress,
                                        packet.getSenderIpAddress(),
                                        packet.getReceiverMacAddress(),
                                        Router.getCustomWiFiP2PDevice().getMacAddress()
                                )
                        );
                        System.out.println(peerMacAddress + " joined.");
                    } else if (packet.getType().equals(Packet.TYPE.MESSAGE)) {
                        System.out.println("Message from " + packet.getSenderMacAddress() + ": " + new String(packet.getData()));
                    }
                } else {
                    int timeToLive = packet.getTimeToLive();
                    timeToLive--;
                    if (timeToLive > 0) {
                        Sender.addPacket(packet);
                        packet.setTimeToLive(timeToLive);
                    }
                }
            }
        } while (true);
    }
}