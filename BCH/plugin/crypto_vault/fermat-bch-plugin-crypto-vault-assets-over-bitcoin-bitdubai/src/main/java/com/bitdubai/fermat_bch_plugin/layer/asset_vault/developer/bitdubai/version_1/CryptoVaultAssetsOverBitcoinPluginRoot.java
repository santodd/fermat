package com.bitdubai.fermat_bch_plugin.layer.asset_vault.developer.bitdubai.version_1;

import com.bitdubai.fermat_api.CantStartPluginException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.abstract_classes.AbstractPlugin;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededAddonReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.annotations.NeededPluginReference;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginVersionReference;
import com.bitdubai.fermat_api.layer.all_definition.developer.DatabaseManagerForDevelopers;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabase;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTable;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperDatabaseTableRecord;
import com.bitdubai.fermat_api.layer.all_definition.developer.DeveloperObjectFactory;
import com.bitdubai.fermat_api.layer.all_definition.enums.Addons;
import com.bitdubai.fermat_api.layer.all_definition.enums.CryptoCurrency;
import com.bitdubai.fermat_api.layer.all_definition.enums.Layers;
import com.bitdubai.fermat_api.layer.all_definition.enums.Platforms;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_api.layer.all_definition.enums.ServiceStatus;
import com.bitdubai.fermat_api.layer.all_definition.money.CryptoAddress;
import com.bitdubai.fermat_api.layer.all_definition.util.Version;
import com.bitdubai.fermat_api.layer.osa_android.database_system.PluginDatabaseSystem;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer.all_definition.enums.BlockchainNetworkType;
import com.bitdubai.fermat_bch_api.layer.crypto_network.bitcoin.interfaces.BitcoinNetworkManager;

import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.exceptions.CantGetActiveRedeemPointAddressesException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.exceptions.CantGetActiveRedeemPointsException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.exceptions.CantGetExtendedPublicKeyException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.interfaces.AssetVaultManager;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.asset_vault.exceptions.CantSendAssetBitcoinsToUserException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.HierarchyAccount.HierarchyAccount;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.classes.HierarchyAccount.HierarchyAccountType;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.exceptions.CantAddHierarchyAccountException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.exceptions.CantDeriveNewKeysException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.exceptions.GetNewCryptoAddressException;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.interfaces.PlatformCryptoVault;
import com.bitdubai.fermat_bch_api.layer.crypto_vault.watch_only_vault.ExtendedPublicKey;
import com.bitdubai.fermat_bch_plugin.layer.asset_vault.developer.bitdubai.version_1.database.AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory;
import com.bitdubai.fermat_bch_plugin.layer.asset_vault.developer.bitdubai.version_1.structure.AssetCryptoVaultManager;
import com.bitdubai.fermat_pip_api.layer.user.device_user.interfaces.DeviceUserManager;
import com.bitdubai.fermat_pip_api.layer.platform_service.error_manager.interfaces.ErrorManager;

import org.bitcoinj.crypto.DeterministicKey;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

/**
 * The Class <code>com.bitdubai.fermat_bch_plugin.layer.cryptovault.assetsoverbitcoin.developer.bitdubai.version_1.CryptoVaultAssetsOverBitcoinPluginRoot</code>
 * is the root plugin of the Assets over bitcoin Crypto Vault.
 * <p/>
 *
 * Created by Rodrigo Acosta - (acosta_rodrigo@hotmail.com) on 06/10/15.
 *
 * @version 1.0
 * @since Java JDK 1.7
 */
public class CryptoVaultAssetsOverBitcoinPluginRoot extends AbstractPlugin implements
        AssetVaultManager,
        PlatformCryptoVault,
        DatabaseManagerForDevelopers {

    @NeededAddonReference (platform = Platforms.PLUG_INS_PLATFORM   , layer = Layers.USER            , addon  = Addons .DEVICE_USER           )
    private DeviceUserManager deviceUserManager;

    @NeededAddonReference (platform = Platforms.PLUG_INS_PLATFORM   , layer = Layers.PLATFORM_SERVICE, addon  = Addons .ERROR_MANAGER         )
    private ErrorManager errorManager;

    @NeededAddonReference (platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon  = Addons .PLUGIN_DATABASE_SYSTEM)
    private PluginDatabaseSystem pluginDatabaseSystem;

    @NeededAddonReference (platform = Platforms.OPERATIVE_SYSTEM_API, layer = Layers.SYSTEM          , addon  = Addons .PLUGIN_FILE_SYSTEM    )
    private PluginFileSystem pluginFileSystem;

    @NeededPluginReference(platform = Platforms.BLOCKCHAINS         , layer = Layers.CRYPTO_NETWORK  , plugin = Plugins.BITCOIN_NETWORK       )
    private BitcoinNetworkManager bitcoinNetworkManager;


    private AssetCryptoVaultManager assetCryptoVaultManager;


    public CryptoVaultAssetsOverBitcoinPluginRoot() {
        super(new PluginVersionReference(new Version()));
    }

    /**
     * DatabaseManagerForDevelopers interface implementation
     */

    /**
     * Gets the plugin database List for the Developer sub app
     * @param developerObjectFactory
     * @return
     */
    @Override
    public List<DeveloperDatabase> getDatabaseList(DeveloperObjectFactory developerObjectFactory) {
        AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory developerDatabaseFactory = new AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return developerDatabaseFactory.getDatabaseList(developerObjectFactory);
    }

    /**
     * Gets the plugin table list for the Developer sub app
     * @param developerObjectFactory
     * @param developerDatabase
     * @return
     */
    @Override
    public List<DeveloperDatabaseTable> getDatabaseTableList(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase) {
        AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory developerDatabaseFactory = new AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return developerDatabaseFactory.getDatabaseTableList(developerObjectFactory);
    }

    /**
     * Gets the records from the table for the Developer sub app
     * @param developerObjectFactory
     * @param developerDatabase
     * @param developerDatabaseTable
     * @return
     */
    @Override
    public List<DeveloperDatabaseTableRecord> getDatabaseTableContent(DeveloperObjectFactory developerObjectFactory, DeveloperDatabase developerDatabase, DeveloperDatabaseTable developerDatabaseTable) {
        AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory developerDatabaseFactory = new AssetsOverBitcoinCryptoVaultDeveloperDatabaseFactory(this.pluginDatabaseSystem, this.pluginId);
        return developerDatabaseFactory.getDatabaseTableContent(developerObjectFactory, developerDatabaseTable);
    }

    @Override
    public void start() throws CantStartPluginException {

        /**
         * The Asset vault works by creating a HD tree of keys.
         * 1) A seed is generated, or loaded by the VaultSeedGenerator class. If the device User logged is new then it will create it
         * or loaded if it exists from before.
         * 2) The AssetCryptoVaultManager will create a Master key (m) with that seed and will create key hierarchies (VaultKeyHierarchy)
         * with the VaultKeyHierarchyGenerator in a separate thread to reduce start time of the platform.
         * 3) for all the accounts that I have configured on this device (Account zero is the vault, but I may have many redeem points) I generate keys
         * for each of them
         * 4) I pass the entire set of keys to the bitcoin network so we start listening the network with those keys.
         * 5) An Agent (VaultKeyhierarchyMaintainer) will monitor the usage of keys to generate new ones when needed.
         */
        try{
            // the DeviceUserLogged
            String deviceUserLoggedPublicKey = deviceUserManager.getLoggedInDeviceUser().getPublicKey();

            assetCryptoVaultManager= new AssetCryptoVaultManager(this.pluginId,
                    pluginFileSystem,
                    pluginDatabaseSystem,
                    deviceUserLoggedPublicKey,
                    bitcoinNetworkManager);
        } catch (Exception e){
            throw new CantStartPluginException(CantStartPluginException.DEFAULT_MESSAGE, e, "couldn't start plugin because seed creation/loading failed. Key hierarchy not created.", "");
        }

        /**
         * Test
         */
        //generateAddress();
        //sendBitcoinsTest();

        /**
         * Nothing left to do.
         */
        this.serviceStatus = ServiceStatus.STARTED;
    }



    /**
     * Test Method to generate an address at startup
     */
    private void generateAddress() {
        try{
            Thread.sleep(5000);
            System.out.println("Asset vault address: " + this.getNewAssetVaultCryptoAddress(BlockchainNetworkType.DEFAULT).getAddress());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (GetNewCryptoAddressException e) {
            e.printStackTrace();
        }
    }

    @Override
    public CryptoAddress getNewAssetVaultCryptoAddress(BlockchainNetworkType blockchainNetworkType) throws GetNewCryptoAddressException {
        return assetCryptoVaultManager.getNewAssetVaultCryptoAddress(blockchainNetworkType);
    }

    @Override
    public long getAvailableBalanceForTransaction(String genesisTransaction) {
        return assetCryptoVaultManager.getAvailableBalanceForTransaction(genesisTransaction);
    }

    /**
     * Sends the bitcoins generated from the genesisTransactionId to the specified User Actor addres.
     * @param genesisTransactionId
     * @param genesisBlock
     * @param addressTo
     * @return
     * @throws CantSendAssetBitcoinsToUserException
     */
    @Override
    public String sendAssetBitcoins(String genesisTransactionId, String genesisBlock, CryptoAddress addressTo) throws CantSendAssetBitcoinsToUserException {
        return assetCryptoVaultManager.sendAssetBitcoins(genesisTransactionId, genesisBlock, addressTo);
    }

    /**
     * PlatformCryptoVault interface implementation.
     * Generates a new Crypto Address by getting next available key path, derive it, and generate it in the specified network.
     * @param blockchainNetworkType DEFAULT if null value is passed.
     * @return the newly created crypto address
     * @throws GetNewCryptoAddressException
     */
    @Override
    public CryptoAddress getCryptoAddress(@Nullable BlockchainNetworkType blockchainNetworkType) throws GetNewCryptoAddressException {
        return getNewAssetVaultCryptoAddress(blockchainNetworkType);
    }

    /**
     * PlatformCryptoVault interface implementation-
     * @return DAP
     */
    @Override
    public Platforms getPlatform() {
        return Platforms.DIGITAL_ASSET_PLATFORM;
    }

    /**
     * Gets the amount of unused keys that are available from the passed account.
     * @return
     */
    @Override
    public int getAvailableKeyCount(){
        HierarchyAccount masterAccount = new HierarchyAccount(0, "Asset Vault", HierarchyAccountType.MASTER_ACCOUNT);
        return assetCryptoVaultManager.getAvailableKeyCount(masterAccount);
    }


    /**
     * Derives the specified amount of keys in the selected account. Only some plugins can execute this method.
     * @param plugin the pluginId invoking this call. Might not have permissions to create new keys.
     * @param keysToDerive thre amount of keys to derive.
     * @throws CantDeriveNewKeysException
     */
    @Override
    public void deriveKeys(Plugins plugin,  int keysToDerive) throws CantDeriveNewKeysException{
        assetCryptoVaultManager.deriveKeys(plugin, keysToDerive);
    }


    /**
     * Gets the Extended Public Key from the specified account. Can't be from a master account.
     * @param redeemPointPublicKey a Redeem Point publicKey
     * @return the DeterministicKey that will be used by the redeem Points.
     * @throws CantGetExtendedPublicKeyException
     */
    @Override
    public ExtendedPublicKey getRedeemPointExtendedPublicKey(String redeemPointPublicKey) throws CantGetExtendedPublicKeyException {
        return assetCryptoVaultManager.getRedeemPointExtendedPublicKey(redeemPointPublicKey);
    }

    /**
     * If the redeem point keys are initialized, will return all the generated addresses
     * @param redeemPointPublicKey
     * @return
     * @throws CantGetActiveRedeemPointAddressesException
     */
    @Override
    public List<CryptoAddress> getActiveRedeemPointAddresses(String redeemPointPublicKey) throws CantGetActiveRedeemPointAddressesException {
        return assetCryptoVaultManager.getActiveRedeemPointAddresses(redeemPointPublicKey);
    }

    /**
     * Returns the private Keys of all the active Redeem Points hierarchies in the asset vault
     * @return
     */
    @Override
    public List<String> getActiveRedeemPoints() throws CantGetActiveRedeemPointsException {
        return assetCryptoVaultManager.getActiveRedeemPoints();
    }

    /**
     * When we receive assets from a Redeemption processes, the Issuer that granted the extended public key to the redeem point
     * needs to inform us when an address is used, so we can generate more if needed.
     * @param cryptoAddress
     * @param redeemPointPublicKey
     */
    @Override
    public void notifyUsedRedeemPointAddress(CryptoAddress cryptoAddress, String redeemPointPublicKey) {
        assetCryptoVaultManager.notifyUsedRedeemPointAddress(cryptoAddress, redeemPointPublicKey);
    }
}
