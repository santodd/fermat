package com.bitdubai.fermat_cbp_core.layer.negotiation_transaction.customer_broker_update;

import com.bitdubai.fermat_core_api.layer.all_definition.system.abstract_classes.AbstractPluginSubsystem;
import com.bitdubai.fermat_core_api.layer.all_definition.system.exceptions.CantStartSubsystemException;
import com.bitdubai.fermat_api.layer.all_definition.common.system.utils.PluginReference;
import com.bitdubai.fermat_api.layer.all_definition.enums.Plugins;
import com.bitdubai.fermat_cbp_plugin.layer.negotiation_transaction.customer_broker_update.developer.bitdubai.DeveloperBitDubai;

/**
 * Created by Yordin Alayn on 22.11/15.
 */
public class CustomerBrokerUpdatePluginSubsystem extends AbstractPluginSubsystem {

    public CustomerBrokerUpdatePluginSubsystem() {
        super(new PluginReference(Plugins.CUSTOMER_BROKER_UPDATE));
    }

    @Override
    public void start() throws CantStartSubsystemException {
        try {
            registerDeveloper(new DeveloperBitDubai());
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            throw new CantStartSubsystemException(e, null, null);
        }
    }
}