package org.kylelauhk;

import io.pkts.PacketHandler;
import io.pkts.buffer.Buffer;
import io.pkts.packet.Packet;
import io.pkts.packet.TCPPacket;
import io.pkts.packet.UDPPacket;
import io.pkts.protocol.Protocol;

import java.io.IOException;
import java.util.concurrent.locks.LockSupport;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

public class TcpUdpPacketHandler implements PacketHandler {
    private static final Logger logger = LogManager.getLogger(TcpUdpPacketHandler.class.getName());
    private static final boolean isDebug = logger.isDebugEnabled();

    int desPort;
    String desIP;
    long startPcapTimeNs = 0;
    long startSystemTimeNs = 0;
    long multiplier;

    TcpUdpPacketHandler(long multiplier) {
        this.multiplier = multiplier;
    }

    @Override
    public boolean nextPacket(Packet packet) throws IOException {
        // Check the packet protocol
        long arrTimeNs = packet.getArrivalTime() * 1000;
        long nowNs = System.nanoTime();
        if (startPcapTimeNs == 0) {
            startPcapTimeNs = arrTimeNs;
            startSystemTimeNs = nowNs;
        }
        long timeToSleepNs = (arrTimeNs - startPcapTimeNs) / multiplier - (nowNs - startSystemTimeNs);
        if (multiplier > 0 && timeToSleepNs > 0) {
            if (isDebug) {
                logger.debug("arrTimeNs={}, startPcapTimeNs={}", arrTimeNs, startPcapTimeNs);
                logger.debug("nowNs={}, startSystemTimeNs={}", nowNs, startSystemTimeNs);
                logger.debug("timeToSleepNs {}", timeToSleepNs);
            }
            LockSupport.parkNanos(timeToSleepNs);
        }
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
                // Todo: sendPacket
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
                UdpSocketHandler.sendPacket(desIP, desPort, buffer.getArray());
            }
        }

        // Return true if you want to keep receiving next packet.
        // Return false if you want to stop traversal
        return true;
    }
}
