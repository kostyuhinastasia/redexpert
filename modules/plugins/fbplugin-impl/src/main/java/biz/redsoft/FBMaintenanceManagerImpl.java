package biz.redsoft;

import org.firebirdsql.gds.ServiceRequestBuffer;
import org.firebirdsql.gds.ng.FbService;
import org.firebirdsql.management.FBMaintenanceManager;
import org.firebirdsql.management.FBServiceManager;

import java.io.OutputStream;
import java.sql.SQLException;
import java.util.List;

import static org.firebirdsql.gds.ISCConstants.*;

public class FBMaintenanceManagerImpl extends FBServiceManager implements IFBMaintenanceManager {

    FBMaintenanceManager fbMaintenanceManager;

    public FBMaintenanceManagerImpl() {
        super();
        fbMaintenanceManager = new FBMaintenanceManager();
    }

    @Override
    public void setDatabaseAccessMode(int var1) throws SQLException {
        fbMaintenanceManager.setDatabaseAccessMode(var1);
    }

    @Override
    public void setDatabaseDialect(int var1) throws SQLException {
        fbMaintenanceManager.setDatabaseDialect(var1);
    }

    @Override
    public void setDefaultCacheBuffer(int var1) throws SQLException {
        fbMaintenanceManager.setDefaultCacheBuffer(var1);
    }

    @Override
    public void setForcedWrites(boolean var1) throws SQLException {
        fbMaintenanceManager.setForcedWrites(var1);
    }

    @Override
    public void setPageFill(int var1) throws SQLException {
        fbMaintenanceManager.setPageFill(var1);
    }

    @Override
    public void shutdownDatabase(int var1, int var2) throws SQLException {
        fbMaintenanceManager.shutdownDatabase(var1, var2);
    }

    @Override
    public void shutdownDatabase(byte var1, int var2, int var3) throws SQLException {
        fbMaintenanceManager.shutdownDatabase(var1, var2, var3);
    }

    @Override
    public void bringDatabaseOnline() throws SQLException {
        fbMaintenanceManager.bringDatabaseOnline();
    }

    @Override
    public void bringDatabaseOnline(byte var1) throws SQLException {
        fbMaintenanceManager.bringDatabaseOnline(var1);
    }

    @Override
    public void markCorruptRecords() throws SQLException {
        fbMaintenanceManager.markCorruptRecords();
    }

    @Override
    public void validateDatabase() throws SQLException {
        fbMaintenanceManager.validateDatabase();
    }

    @Override
    public void validateDatabase(int options) throws SQLException {
        fbMaintenanceManager.validateDatabase(options);
    }

    @Override
    public void validateTable(String var1, String var2, String var3, String var4) throws SQLException {
        try (FbService service = fbMaintenanceManager.attachServiceManager()) {

            ServiceRequestBuffer srb = service.createServiceRequestBuffer();
            srb.addArgument(isc_action_svc_validate);

            if (getDatabase() != null)
                srb.addArgument(isc_spb_dbname, getDatabase());
            if (var1 != null)
                srb.addArgument(isc_spb_val_tab_incl, var1);
            if (var2 != null)
                srb.addArgument(isc_spb_val_idx_incl, var2);
            if (var3 != null)
                srb.addArgument(isc_spb_val_tab_excl, var3);
            if (var4 != null)
                srb.addArgument(isc_spb_val_idx_excl, var4);

            executeServicesOperation(service, srb);
        }
    }

    @Override
    public void setParallelWorkers(int var1) {
        fbMaintenanceManager.setParallelWorkers(var1);
    }

    @Override
    public void setSweepThreshold(int var1) throws SQLException {
        fbMaintenanceManager.setSweepThreshold(var1);
    }

    @Override
    public void sweepDatabase() throws SQLException {
        fbMaintenanceManager.sweepDatabase();
    }

    @Override
    public void sweepDatabase(int var1) throws SQLException {
        fbMaintenanceManager.sweepDatabase(var1);
    }

    @Override
    public void activateShadowFile() throws SQLException {
        fbMaintenanceManager.activateShadowFile();
    }

    @Override
    public void killUnavailableShadows() throws SQLException {
        fbMaintenanceManager.killUnavailableShadows();
    }

    @Override
    public void listLimboTransactions() throws SQLException {
        fbMaintenanceManager.limboTransactionsAsList();
    }

    @Override
    public List<Long> limboTransactionsAsList() throws SQLException {
        return fbMaintenanceManager.limboTransactionsAsList();
    }

    @Override
    public long[] getLimboTransactions() throws SQLException {
        return fbMaintenanceManager.getLimboTransactions();
    }

    @Override
    public void commitTransaction(long var1) throws SQLException {
        fbMaintenanceManager.commitTransaction(var1);
    }

    @Override
    public void rollbackTransaction(long var1) throws SQLException {
        fbMaintenanceManager.rollbackTransaction(var1);
    }

    @Override
    public String getCharSet() {
        return fbMaintenanceManager.getCharSet();
    }

    @Override
    public void setCharSet(String var1) {
        fbMaintenanceManager.setCharSet(var1);
    }

    @Override
    public String getUser() {
        return fbMaintenanceManager.getUser();
    }

    @Override
    public void setUser(String var1) {
        fbMaintenanceManager.setUser(var1);
    }

    @Override
    public String getPassword() {
        return fbMaintenanceManager.getPassword();
    }

    @Override
    public void setPassword(String var1) {
        fbMaintenanceManager.setPassword(var1);
    }

    @Override
    public String getDatabase() {
        return fbMaintenanceManager.getDatabase();
    }

    @Override
    public void setDatabase(String var1) {
        fbMaintenanceManager.setDatabase(var1);
    }

    @Override
    public String getHost() {
        return fbMaintenanceManager.getHost();
    }

    @Override
    public void setHost(String var1) {
        fbMaintenanceManager.setHost(var1);
    }

    @Override
    public int getPort() {
        return fbMaintenanceManager.getPort();
    }

    @Override
    public void setPort(int var1) {
        fbMaintenanceManager.setPort(var1);
    }

    @Override
    public OutputStream getLogger() {
        return fbMaintenanceManager.getLogger();
    }

    @Override
    public void setLogger(OutputStream var1) {
        fbMaintenanceManager.setLogger(var1);
    }

}
