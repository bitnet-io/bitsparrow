package com.sparrowwallet.sparrow.event;

import com.sparrowwallet.drongo.KeyPurpose;
import com.sparrowwallet.drongo.wallet.Wallet;
import com.sparrowwallet.drongo.wallet.WalletNode;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This is posted by WalletForm once the history of the wallet has been refreshed, and new transactions detected
 * Extends WalletChangedEvent so also saves the wallet.
 */
public class WalletHistoryChangedEvent extends WalletBlockHeightChangedEvent {
    private final List<WalletNode> historyChangedNodes;

    public WalletHistoryChangedEvent(Wallet wallet, Integer blockHeight, List<WalletNode> historyChangedNodes) {
        super(wallet, blockHeight);
        this.historyChangedNodes = historyChangedNodes;
    }

    public List<WalletNode> getHistoryChangedNodes() {
        return historyChangedNodes;
    }

    public List<WalletNode> getReceiveNodes() {
        return getWallet().getNode(KeyPurpose.RECEIVE).getChildren().stream().filter(historyChangedNodes::contains).collect(Collectors.toList());
    }

    public List<WalletNode> getChangeNodes() {
        return getWallet().getNode(KeyPurpose.CHANGE).getChildren().stream().filter(historyChangedNodes::contains).collect(Collectors.toList());
    }
}
