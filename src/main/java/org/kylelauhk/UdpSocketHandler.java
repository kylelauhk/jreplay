package org.kylelauhk;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.*;

public class UdpSocketHandler {
    private static final Logger logger = LogManager.getLogger(TcpUdpPacketHandler.class.getName());
    static InetAddress addr;
    static DatagramSocket socket;

    public static void initSocket() throws UnknownHostException, SocketException {
        if (socket == null) {
            String destInterface = System.getProperty("InetAddr", "127.0.0.1");
            int localPort = Integer.parseInt(System.getProperty("localPort", "12345"));
            logger.info("init UDP socket with destInterface={}, localPort={}", destInterface, localPort);
            addr = InetAddress.getByName(destInterface);
            socket = new DatagramSocket(localPort, addr);
            socket.setBroadcast(true);
            // Todo: port map
        }
    }

    static void sendPacket(String ip, int port, byte[] buffer) throws IOException {
        initSocket();
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), port);
        socket.send(packet);
    }
}
