package org.kylelauhk;
import io.pkts.PacketHandler;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TcpUdpPacketHandler implements PacketHandler {
    private static Logger logger = LogManager.getLogger(TcpUdpPacketHandler.class.getName());

    int desPort;
    String desIP;
    @Override
    public boolean nextPacket(Packet packet) throws IOException {
        // Check the packet protocol
        if (packet.hasProtocol(Protocol.TCP)) {
            // Cast the packet to subclass
            TCPPacket tcpPacket = (TCPPacket) packet.getPacket(Protocol.TCP);

            // Explore the available methods.
            // This sample code prints the payload, but you can get other attributes as well
            Buffer buffer = tcpPacket.getPayload();
            desPort = tcpPacket.getDestinationPort();
            desIP = tcpPacket.getParentPacket().getDestinationIP();

            if (buffer != null) {
                logger.info("TCP {}:{}, {}", desIP, desPort, buffer);
            }
        } else if (packet.hasProtocol(Protocol.UDP)) {
            // Cast the packet to subclass
            UDPPacket udpPacket = (UDPPacket) packet.getPacket(Protocol.UDP);

            desPort = udpPacket.getDestinationPort();
            desIP = udpPacket.getParentPacket().getDestinationIP();
            // Explore the available methods.
            // This sample code prints the payload, but you can get other attributes as well
            Buffer buffer = udpPacket.getPayload();
            if (buffer != null) {
                logger.info("UDP {}:{}, {}", desIP, desPort, buffer);
            }
        }

        // Return true if you want to keep receiving next packet.
        // Return false if you want to stop traversal
        return true;
    }
}
