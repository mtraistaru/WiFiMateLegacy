package Peer;

import Config.Base;
import Network.Router;
import Network.StreamSenderTCP;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Sender implements Runnable {

    private static ConcurrentLinkedQueue<Packet> packetConcurrentLinkedQueue;

    public Sender() {
        if (packetConcurrentLinkedQueue == null) {
            packetConcurrentLinkedQueue = new ConcurrentLinkedQueue<>();
        }
    }

    public static boolean addPacket(Packet packet) {
        return packetConcurrentLinkedQueue.add(packet);
    }

    @Override
    public void run() {
        StreamSenderTCP packetSender = new StreamSenderTCP();
        do {
            while (packetConcurrentLinkedQueue.isEmpty()) {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException exception) {
                    exception.printStackTrace();
                }
            }
            Packet packet = packetConcurrentLinkedQueue.remove();
            String ipAddress = Router.getClientIpAddress(packet.getReceiverMacAddress());
            packetSender.sendPacket(
                    ipAddress,
                    Base.GROUP_OWNER_RECEIVE_PORT,
                    packet.serialisePacket()
            );
        } while (true);
    }
}
