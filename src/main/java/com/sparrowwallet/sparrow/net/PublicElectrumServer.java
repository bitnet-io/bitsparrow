package com.sparrowwallet.sparrow.net;

import com.sparrowwallet.drongo.Network;
import com.sparrowwallet.sparrow.io.Server;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public enum PublicElectrumServer {
    BLOCKSTREAM_INFO("bitexplorer.io", "ssl://bitexplorer.io:50002", Network.MAINNET),
    ELECTRUM_BLOCKSTREAM_INFO("bitchair.io", "ssl://bitchair.io:50002", Network.MAINNET),
    LUKECHILDS_CO("194.163.178.204", "ssl://194.163.178.204:50002", Network.MAINNET),
    EMZY_DE("194.163.176.151", "ssl://194.163.176.151:50002", Network.MAINNET),
    BITAROO_NET("194.163.178.99", "ssl://194.163.178.99:50002", Network.MAINNET),
//    DIYNODES_COM("electrum.diynodes.com", "ssl://electrum.diynodes.com:50022", Network.MAINNET),
 //   SETHFORPRIVACY_COM("fulcrum.sethforprivacy.com", "ssl://fulcrum.sethforprivacy.com:50002", Network.MAINNET),


    TESTNET_ARANGUREN_ORG("testnet.aranguren.org", "ssl://testnet.aranguren.org:51002", Network.TESTNET),
    TESTNET_QTORNADO_COM("testnet.qtornado.com", "ssl://testnet.qtornado.com:51002", Network.TESTNET);

    PublicElectrumServer(String name, String url, Network network) {
        this.server = new Server(url, name);
        this.network = network;
    }

    public static final List<Network> SUPPORTED_NETWORKS = List.of(Network.MAINNET, Network.TESTNET);

    private final Server server;
    private final Network network;

    public Server getServer() {
        return server;
    }

    public String getUrl() {
        return server.getUrl();
    }

    public Network getNetwork() {
        return network;
    }

    public static List<PublicElectrumServer> getServers() {
        return Arrays.stream(values()).filter(server -> server.network == Network.get()).collect(Collectors.toList());
    }

    public static boolean supportedNetwork() {
        return SUPPORTED_NETWORKS.contains(Network.get());
    }

    public static PublicElectrumServer fromServer(Server server) {
        for(PublicElectrumServer publicServer : values()) {
            if(publicServer.getServer().equals(server)) {
                return publicServer;
            }
        }

        return null;
    }

    @Override
    public String toString() {
        return server.getAlias();
    }
}
