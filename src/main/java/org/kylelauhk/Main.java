package org.kylelauhk;

import io.pkts.Pcap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main {
    private static Logger logger = LogManager.getLogger(Main.class.getName());
    public static void main(String[] args) throws IOException {
        String fileName = System.getProperty("pcap");
        logger.info("pcap={}", fileName);
        if(fileName == null) {
            throw new RuntimeException("Property[pcap] not found");
        }

        Pcap pcap = Pcap.openStream(fileName);
        pcap.loop(new TcpUdpPacketHandler());
        pcap.close();
        logger.info("done");
    }
}