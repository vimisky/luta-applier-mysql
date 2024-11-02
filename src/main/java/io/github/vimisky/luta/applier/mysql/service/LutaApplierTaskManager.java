package io.github.vimisky.luta.applier.mysql.service;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTask;
import io.github.vimisky.luta.applier.mysql.processor.MySQLApplier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class LutaApplierTaskManager {

    @Autowired
    private LutaApplierService lutaApplierService;

    Map<String, MySQLApplier> mySQLApplierMap = new HashMap<>();

    public boolean start(LutaApplierTask lutaApplierTask){
        this.stop(lutaApplierTask);
        MySQLApplier mySQLApplier = new MySQLApplier(lutaApplierTask.getUuid(), lutaApplierService);
        this.mySQLApplierMap.put(lutaApplierTask.getUuid(), mySQLApplier);

        return mySQLApplier.start();
    }

    public boolean stop(LutaApplierTask lutaApplierTask){
        boolean ret = false;
        MySQLApplier mySQLApplier = mySQLApplierMap.get(lutaApplierTask.getUuid());
        if (mySQLApplier != null ){
            mySQLApplier.stop();
            mySQLApplierMap.remove(lutaApplierTask.getUuid());
            ret = true;
        }

        return ret;
    }

    public boolean isRunning(LutaApplierTask lutaApplierTask){
        boolean isRunning = false;
        MySQLApplier mySQLApplier = mySQLApplierMap.get(lutaApplierTask.getUuid());
        if (mySQLApplier != null ){
            isRunning = true;
        }
        return isRunning;
    }

}
