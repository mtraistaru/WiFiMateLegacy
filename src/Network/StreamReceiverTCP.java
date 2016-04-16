package Network;

import Peer.Packet;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;

public class StreamReceiverTCP implements Runnable {

    private ServerSocket serverSocket;
    private ConcurrentLinkedQueue<Packet> packetConcurrentLinkedQueue;

    public StreamReceiverTCP(int port, ConcurrentLinkedQueue<Packet> queue) {
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException exception) {
            System.err.println("Socket with port: " + port + " creation failed.");
            exception.printStackTrace();
        }
        packetConcurrentLinkedQueue = queue;
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Socket socket = serverSocket.accept();
                InputStream in = socket.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                do {
                    int current = in.read(buffer);
                    if (current >= 0) {
                        byteArrayOutputStream.write(buffer, 0, current);
                    } else {
                        break;
                    }
                } while (true);

                byte trimmedBytes[] = byteArrayOutputStream.toByteArray();
                Packet packet = Packet.deserialisePacket(trimmedBytes);
                packet.setSenderIpAddress(socket.getInetAddress().getHostAddress());
                packetConcurrentLinkedQueue.add(packet);
                socket.close();
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
    }
}