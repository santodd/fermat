import com.bitdubai.fermat_api.layer._12_world.wallet.exceptions.CantStartAgentException;
import com.bitdubai.fermat_api.layer.osa_android.file_system.PluginFileSystem;
import com.bitdubai.fermat_api.layer._3_platform_service.error_manager.ErrorManager;
import com.bitdubai.fermat_cry_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.structure.BitcoinCryptoNetworkMonitoringAgent;
import com.bitdubai.fermat_cry_plugin.layer.crypto_network.bitcoin.developer.bitdubai.version_1.structure.BitcoinNetworkConfiguration;

import org.bitcoinj.core.Wallet;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.UUID;

/**
 * Created by rodrigo on 09/06/15.
 */
@RunWith(MockitoJUnitRunner.class)
public class BitcoinNetworkConnectionTest {

    @Mock PluginFileSystem pluginFileSystem;
    @Mock ErrorManager errorManager;

    @Test
    public void startConnection() throws InterruptedException {
        Wallet wallet = new Wallet(BitcoinNetworkConfiguration.getNetworkConfiguration());
        UUID userId = UUID.randomUUID();



        BitcoinCryptoNetworkMonitoringAgent bitcoin = new BitcoinCryptoNetworkMonitoringAgent(wallet, userId);
        bitcoin.setPluginId(UUID.randomUUID());
        bitcoin.setPluginFileSystem(pluginFileSystem);
        bitcoin.setErrorManager(errorManager);

        try {
            bitcoin.start();
            while(true){
                System.out.println(bitcoin.getConnectedPeers());
                Thread.sleep(10000);
                Assert.assertTrue(bitcoin.isRunning());
                bitcoin.stop();
                Assert.assertFalse(bitcoin.isRunning());
                if (!bitcoin.isRunning()){
                    break;
                }

            }
        } catch (CantStartAgentException e) {
            e.printStackTrace();
        }

    }
}
