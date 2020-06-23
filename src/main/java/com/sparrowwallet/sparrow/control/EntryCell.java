package com.sparrowwallet.sparrow.control;

import com.sparrowwallet.drongo.Utils;
import com.sparrowwallet.drongo.address.Address;
import com.sparrowwallet.drongo.wallet.BlockTransaction;
import com.sparrowwallet.sparrow.EventManager;
import com.sparrowwallet.sparrow.event.ReceiveActionEvent;
import com.sparrowwallet.sparrow.event.ReceiveToEvent;
import com.sparrowwallet.sparrow.event.ViewTransactionEvent;
import com.sparrowwallet.sparrow.glyphfont.FontAwesome5;
import com.sparrowwallet.sparrow.wallet.Entry;
import com.sparrowwallet.sparrow.wallet.HashIndexEntry;
import com.sparrowwallet.sparrow.wallet.NodeEntry;
import com.sparrowwallet.sparrow.wallet.TransactionEntry;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import org.controlsfx.glyphfont.FontAwesome;
import org.controlsfx.glyphfont.Glyph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

class EntryCell extends TreeTableCell<Entry, Entry> {
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm");

    public EntryCell() {
        super();
        setAlignment(Pos.CENTER_LEFT);
        setContentDisplay(ContentDisplay.RIGHT);
        getStyleClass().add("entry-cell");
    }

    @Override
    protected void updateItem(Entry entry, boolean empty) {
        super.updateItem(entry, empty);

        applyRowStyles(this, entry);

        if(empty) {
            setText(null);
            setGraphic(null);
        } else {
            if(entry instanceof TransactionEntry) {
                TransactionEntry transactionEntry = (TransactionEntry)entry;
                String date = DATE_FORMAT.format(transactionEntry.getBlockTransaction().getDate());
                setText(date);
                setContextMenu(new TransactionContextMenu(date, transactionEntry.getBlockTransaction()));
                Tooltip tooltip = new Tooltip();
                tooltip.setText(transactionEntry.getBlockTransaction().getHash().toString());
                setTooltip(tooltip);

                Button viewTransactionButton = new Button("");
                Glyph searchGlyph = new Glyph(FontAwesome5.FONT_NAME, FontAwesome5.Glyph.SEARCH);
                searchGlyph.setFontSize(12);
                viewTransactionButton.setGraphic(searchGlyph);
                viewTransactionButton.setOnAction(event -> {
                    EventManager.get().post(new ViewTransactionEvent(transactionEntry.getBlockTransaction()));
                });
                setGraphic(viewTransactionButton);
            } else if(entry instanceof NodeEntry) {
                NodeEntry nodeEntry = (NodeEntry)entry;
                Address address = nodeEntry.getAddress();
                setText(address.toString());
                setContextMenu(new AddressContextMenu(address, nodeEntry.getOutputDescriptor()));
                Tooltip tooltip = new Tooltip();
                tooltip.setText(nodeEntry.getNode().getDerivationPath());
                setTooltip(tooltip);
                getStyleClass().add("address-cell");

                Button receiveButton = new Button("");
                Glyph receiveGlyph = new Glyph("FontAwesome", FontAwesome.Glyph.ARROW_DOWN);
                receiveGlyph.setFontSize(12);
                receiveButton.setGraphic(receiveGlyph);
                receiveButton.setOnAction(event -> {
                    EventManager.get().post(new ReceiveActionEvent(nodeEntry));
                    Platform.runLater(() -> EventManager.get().post(new ReceiveToEvent(nodeEntry)));
                });
                setGraphic(receiveButton);
            } else if(entry instanceof HashIndexEntry) {
                HashIndexEntry hashIndexEntry = (HashIndexEntry)entry;
                setText(hashIndexEntry.getDescription());
                setContextMenu(new HashIndexEntryContextMenu(hashIndexEntry));
                Tooltip tooltip = new Tooltip();
                tooltip.setText(hashIndexEntry.getHashIndex().toString());
                setTooltip(tooltip);

                Button viewTransactionButton = new Button("");
                Glyph searchGlyph = new Glyph(FontAwesome5.FONT_NAME, FontAwesome5.Glyph.SEARCH);
                searchGlyph.setFontSize(12);
                viewTransactionButton.setGraphic(searchGlyph);
                viewTransactionButton.setOnAction(event -> {
                    EventManager.get().post(new ViewTransactionEvent(hashIndexEntry.getBlockTransaction(), hashIndexEntry));
                });
                setGraphic(viewTransactionButton);
            }
        }
    }

    private static class TransactionContextMenu extends ContextMenu {
        public TransactionContextMenu(String date, BlockTransaction blockTransaction) {
            MenuItem copyDate = new MenuItem("Copy Date");
            copyDate.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(date);
                Clipboard.getSystemClipboard().setContent(content);
            });

            MenuItem copyTxid = new MenuItem("Copy Transaction ID");
            copyTxid.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(blockTransaction.getHashAsString());
                Clipboard.getSystemClipboard().setContent(content);
            });

            MenuItem copyHeight = new MenuItem("Copy Block Height");
            copyTxid.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(Integer.toString(blockTransaction.getHeight()));
                Clipboard.getSystemClipboard().setContent(content);
            });

            getItems().addAll(copyDate, copyTxid, copyHeight);
        }
    }

    private static class AddressContextMenu extends ContextMenu {
        public AddressContextMenu(Address address, String outputDescriptor) {
            MenuItem copyAddress = new MenuItem("Copy Address");
            copyAddress.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(address.toString());
                Clipboard.getSystemClipboard().setContent(content);
            });

            MenuItem copyHex = new MenuItem("Copy Script Output Bytes");
            copyHex.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(Utils.bytesToHex(address.getOutputScriptData()));
                Clipboard.getSystemClipboard().setContent(content);
            });

            MenuItem copyOutputDescriptor = new MenuItem("Copy Output Descriptor");
            copyOutputDescriptor.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(outputDescriptor);
                Clipboard.getSystemClipboard().setContent(content);
            });

            getItems().addAll(copyAddress, copyHex, copyOutputDescriptor);
        }
    }

    private static class HashIndexEntryContextMenu extends ContextMenu {
        public HashIndexEntryContextMenu(HashIndexEntry hashIndexEntry) {
            String label = "Copy " + (hashIndexEntry.getType().equals(HashIndexEntry.Type.OUTPUT) ? "Transaction Output" : "Transaction Input");
            MenuItem copyHashIndex = new MenuItem(label);
            copyHashIndex.setOnAction(AE -> {
                hide();
                ClipboardContent content = new ClipboardContent();
                content.putString(hashIndexEntry.getHashIndex().toString());
                Clipboard.getSystemClipboard().setContent(content);
            });

            getItems().add(copyHashIndex);
        }
    }

    public static void applyRowStyles(TreeTableCell<?, ?> cell, Entry entry) {
        cell.getStyleClass().remove("transaction-row");
        cell.getStyleClass().remove("node-row");
        cell.getStyleClass().remove("address-cell");
        cell.getStyleClass().remove("hashindex-row");
        cell.getStyleClass().remove("spent");

        if(entry != null) {
            if(entry instanceof TransactionEntry) {
                cell.getStyleClass().add("transaction-row");
                TransactionEntry transactionEntry = (TransactionEntry)entry;
                if(transactionEntry.isConfirming()) {
                    cell.getStyleClass().add("confirming");
                    transactionEntry.confirmationsProperty().addListener((observable, oldValue, newValue) -> {
                        if(!transactionEntry.isConfirming()) {
                            cell.getStyleClass().remove("confirming");
                        }
                    });
                }
            } else if(entry instanceof NodeEntry) {
                cell.getStyleClass().add("node-row");
            } else if(entry instanceof HashIndexEntry) {
                cell.getStyleClass().add("hashindex-row");
                HashIndexEntry hashIndexEntry = (HashIndexEntry)entry;
                if(hashIndexEntry.isSpent()) {
                    cell.getStyleClass().add("spent");
                }
            }
        }
    }
}
