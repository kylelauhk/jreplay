package org.kylelauhk;

import io.pkts.Pcap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class Main {
    private static final Logger logger = LogManager.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException {
        String fileName = System.getProperty("pcap");
        logger.info("pcap={}", fileName);
        if (fileName == null) {
            throw new RuntimeException("Property[pcap] not found");
        }

        boolean isTopSpeed = System.getProperty("topSpeed") != null;
        long multiplier = isTopSpeed ? -1 : Long.parseLong(System.getProperty("multiplier", "1"));

        Pcap pcap = Pcap.openStream(fileName);
        pcap.loop(new TcpUdpPacketHandler(multiplier));
        pcap.close();
        logger.info("done");
        // Todo:
        //Statistics for network device: lo
        //Successful packets:        17
        //Failed packets:            0
        //Truncated packets:         0
        //Retried packets (ENOBUFS): 0
        //Retried packets (EAGAIN):  0
    }
}