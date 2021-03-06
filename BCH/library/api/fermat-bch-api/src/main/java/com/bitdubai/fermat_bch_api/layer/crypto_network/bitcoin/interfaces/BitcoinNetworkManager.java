package com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces;

import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_api.layer.all_definition.enums.VaultType;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.TransactionSender;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoStatus;
import com.bitdubai.fermat_api.layer.all_definition.transaction_transference_protocol.crypto_transactions.CryptoTransaction;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.BroadcastStatus;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.BlockchainConnectionStatus;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantBroadcastTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantCancellBroadcastTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetBlockchainConnectionStatusException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetBroadcastStatusException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetCryptoTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetTransactionCryptoStatusException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantGetTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantMonitorBitcoinNetworkException;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.exceptions.CantStoreBitcoinTransactionException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.enums.CryptoVaults;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.UTXOProvider;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * Created by rodrigo on 9/30/15.
 */
public interface BitcoinNetworkManager extends TransactionSender<CryptoTransaction> {

    /**
     * Starts monitoring the network active networks with the list of keys passed.
     * @param keyList
     * @throws CantMonitorBitcoinNetworkException
     */
    void monitorNetworkFromKeyList(CryptoVaults vault, List<BlockchainNetworkType> blockchainNetworkTypes,List<ECKey> keyList) throws CantMonitorBitcoinNetworkException;

    /**
     * Gests all the CryptoTransactions that matchs this transaction Hash
     * @param txHash
     * @return
     * @throws CantGetCryptoTransactionException
     */
    List<CryptoTransaction> getCryptoTransactions(String txHash) throws CantGetCryptoTransactionException;


    /**
     * Will get the CryptoTransaction directly from the blockchain by requesting it to a peer.
     * If the transaction is not part of any of our vaults, we will ask it to a connected peer to retrieve it.
     * @param txHash the Hash of the transaction we are going to look for.
     * @param blockHash the Hash of block where this transaction was stored..
     * @return a CryptoTransaction with the information of the transaction.
     * @throws CantGetCryptoTransactionException
     */
    CryptoTransaction getCryptoTransactionFromBlockChain(String txHash, String blockHash) throws CantGetCryptoTransactionException;

    /**
     * Broadcast a well formed, commited and signed transaction into the specified network
     * @param blockchainNetworkType
     * @param tx
     * @param transactionId the internal Fermat Transaction
     * @throws CantBroadcastTransactionException
     */
    void broadcastTransaction(BlockchainNetworkType blockchainNetworkType, Transaction tx, UUID transactionId) throws CantBroadcastTransactionException;

    /**
     * Broadcast a well formed, commited and signed transaction into the network.
     * @param txHash
     * @throws CantBroadcastTransactionException
     */
    void broadcastTransaction (String txHash) throws CantBroadcastTransactionException;


    /**
     * Will mark the passed transaction as cancelled, and it won't be broadcasted again.
     * @param txHash
     * @throws CantCancellBroadcastTransactionException
     */
    void cancelBroadcast(String txHash) throws CantCancellBroadcastTransactionException;

    /**
     * Returns the broadcast Status for a specified transaction.
     * @param txHash
     * @return
     * @throws CantGetBroadcastStatusException
     */
    BroadcastStatus getBroadcastStatus (String txHash) throws CantGetBroadcastStatusException;

    /**
     * Gets the UTXO provider from the CryptoNetwork on the specified Network
     * @param blockchainNetworkType
     * @return
     */
    UTXOProvider getUTXOProvider(BlockchainNetworkType blockchainNetworkType);


    /**
     * Get the bitcoin transaction stored by the CryptoNetwork
     * @param blockchainNetworkType the network type
     * @param transactionHash the transsaction hash
     * @return the bitcoin transaction
     */
    Transaction getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, String transactionHash);

    /**
     * Gets the bitcoin transactions stored by the CryptoNetwork
     * @param blockchainNetworkType     the network type
     * @param ecKey the ECKey that is affected by the transaction
     * @return the bitcoin transaction
     */
    List<Transaction> getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, ECKey ecKey);

    /**
     * Gets the bitcoin transactions stored by the CryptoNetwork
     * @param blockchainNetworkType the network type.
     * @param ecKeys the list of ECKeys affected by the transactions returned.
     * @return the bitcoin transaction
     */
    List<Transaction> getBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, List<ECKey> ecKeys);

    /**
     * Get the Unspent bitcoin transaction stored by the CryptoNetwork
     * @param blockchainNetworkType the network type
     * @return the bitcoin transaction
     */
    List<Transaction> getUnspentBitcoinTransactions(BlockchainNetworkType blockchainNetworkType);


    /**
     * Get the bitcoin transactions stored by the CryptoNetwork
     * @param blockchainNetworkType the network type
     * @return the bitcoin transaction
     */
    List<Transaction> getBitcoinTransactions(BlockchainNetworkType blockchainNetworkType);


    /**
     * Will get all the CryptoTransactions stored in the CryptoNetwork which are a child of a parent Transaction
     * @param parentHash
     * @return
     * @throws CantGetCryptoTransactionException
     */
    List<CryptoTransaction> getChildCryptoTransaction(String parentHash) throws CantGetCryptoTransactionException;

    /**
     * Will get all the CryptoTransactions stored in the CryptoNetwork which are a child of a parent Transaction
     * @param parentHash the parent transaction
     * @param depth the depth of how many transactions we will navigate until we reach the parent transaction. Max is 10
     * @return
     * @throws CantGetCryptoTransactionException
     */
    List<CryptoTransaction> getChildCryptoTransaction(String parentHash, int depth) throws CantGetCryptoTransactionException;


    /**
     * gets the current Crypto Status for the specified Transaction ID
     * @param txHash the Bitcoin transaction hash
     * @return the last crypto status
     * @throws CantGetTransactionCryptoStatusException
     */
    CryptoStatus getCryptoStatus(String txHash) throws CantGetTransactionCryptoStatusException;


    /**
     * Stores a Bitcoin Transaction in the CryptoNetwork to be broadcasted later
     * @param blockchainNetworkType
     * @param tx
     * @param transactionId
     * @throws CantStoreBitcoinTransactionException
     */
    void storeBitcoinTransaction(BlockchainNetworkType blockchainNetworkType, Transaction tx, UUID transactionId) throws CantStoreBitcoinTransactionException;


    /**
     * Will get the BlockchainConnectionStatus for the specified network.
     * @param blockchainNetworkType the Network type we won't to get info from. If the passed network is not currently activated,
     *                              then we will receive null.
     * @return BlockchainConnectionStatus with information of amount of peers currently connected, etc.
     * @exception CantGetBlockchainConnectionStatusException
     */
    BlockchainConnectionStatus getBlockchainConnectionStatus(BlockchainNetworkType blockchainNetworkType)  throws CantGetBlockchainConnectionStatusException;

    /**
     * Starting from the parentTransaction, I will navigate up until the last transaction, and return it.
     * @blockchainNetworkType the network in which we will be executing this. If none provided, DEFAULT will be used.
     * @param parentTransactionHash The starting point transaction hash.
     * @param transactionBlockHash the block where this transaction is.
     * @return the Last child transaction.
     */
    Transaction getLastChildTransaction(@Nullable BlockchainNetworkType blockchainNetworkType, String parentTransactionHash, String transactionBlockHash) throws CantGetTransactionException;

    /**
     * Starting from the parentTransaction, I will navigate up until the last transaction, and return the CryptoTransaction
     * @blockchainNetworkType the network in which we will be executing this. If none provided, DEFAULT will be used.
     * @param parentTransactionHash The starting point transaction hash.
     * @param transactionBlockHash the block where this transaction is.
     * @return the Last child transaction.
     */
    CryptoTransaction getLastChildCryptoTransaction(@Nullable BlockchainNetworkType blockchainNetworkType, String parentTransactionHash, String transactionBlockHash) throws CantGetCryptoTransactionException;


    /**
     * Gets a stored CryptoTransaction in wathever network.
     * @param txHash the transaction hash we want to get the CryptoTransaction
     * @return the last recorded CryptoTransaction.
     * @throws CantGetCryptoTransactionException
     */
    CryptoTransaction getCryptoTransaction(String txHash) throws CantGetCryptoTransactionException;
}
