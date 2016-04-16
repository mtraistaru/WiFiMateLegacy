package Network;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

public class StreamSenderTCP {

    public boolean sendPacket(String ip, int port, byte[] data) {
        try {
            InetAddress address = InetAddress.getByName(ip);
            Socket socket = new Socket(address, port);
            OutputStream outputStream = socket.getOutputStream();
            outputStream.write(data);
            outputStream.flush();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }
}
