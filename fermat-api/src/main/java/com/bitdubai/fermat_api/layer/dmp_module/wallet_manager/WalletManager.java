package com.bitdubai.fermat_api.layer.dmp_module.wallet_manager;

import com.bitdubai.fermat_api.layer.dmp_middleware.wallet_manager.CantCreateNewWalletException;
import com.bitdubai.fermat_api.layer.modules.common_classes.ActiveActorIdentityInformation;
import com.bitdubai.fermat_api.layer.modules.interfaces.FermatSettings;
import com.bitdubai.fermat_api.layer.modules.interfaces.ModuleManager;

import java.util.List;

/**
 * Created by ciencias on 25.01.15.
 */
public interface WalletManager extends ModuleManager<FermatSettings, ActiveActorIdentityInformation> {

    List<InstalledWallet> getUserWallets() throws CantGetUserWalletException;

    void loadUserWallets (String deviceUserPublicKey) throws CantLoadWalletsException;

    void createDefaultWallets (String deviceUserPublicKey) throws CantCreateDefaultWalletsException;
    
    void enableWallet() throws CantEnableWalletException;

    InstalledWallet getInstalledWallet(String walletPublicKey) throws CantCreateNewWalletException;

    /**
     * This method create a new Intra User Identity
     *
     * @param alias
     * @param profileImage
     * @return
     * @throws WalletCreateNewIntraUserIdentityException
     */

     void createNewIntraWalletUser(String alias, String phrase, byte[] profileImage) throws WalletCreateNewIntraUserIdentityException;


    /**
     * This method returns if exists a Intra User Identity
     * @return
     * @throws CantGetIfIntraWalletUsersExistsException
     */
    boolean hasIntraUserIdentity() throws CantGetIfIntraWalletUsersExistsException;

}