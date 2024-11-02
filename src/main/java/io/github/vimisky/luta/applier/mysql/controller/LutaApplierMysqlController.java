package io.github.vimisky.luta.applier.mysql.controller;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierMapping;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTask;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTransaction;
import io.github.vimisky.luta.applier.mysql.service.LutaApplierService;
import io.github.vimisky.luta.applier.mysql.service.LutaApplierTaskManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class LutaApplierMysqlController {

    private static final Logger logger = LoggerFactory.getLogger(LutaApplierMysqlController.class);

    @Autowired
    private LutaApplierService lutaApplierService;
    @Autowired
    private LutaApplierTaskManager lutaApplierTaskManager;

    @RequestMapping("/task/list")
    public LutaApiResult<ArrayList<LutaApplierTask>> taskList(){
        return LutaApiResultBuilder.ok(lutaApplierService.getApplierTaskList());
    }

    @RequestMapping("/task/remove")
    public LutaApiResult taskRemove(String taskUUID){
        LutaApplierTask lutaApplierTask = lutaApplierService.getApplierTask(taskUUID);
        if (lutaApplierTaskManager.isRunning(lutaApplierTask)){
            lutaApplierTaskManager.stop(lutaApplierTask);
        }
        lutaApplierService.deleteApplierTask(taskUUID);
        return LutaApiResultBuilder.ok();
    }

    @RequestMapping("/task/start")
    public LutaApiResult taskStart(String taskUUID){
        LutaApplierTask lutaApplierTask = lutaApplierService.getApplierTask(taskUUID);
        lutaApplierTaskManager.start(lutaApplierTask);
        return LutaApiResultBuilder.ok();
    }
    @RequestMapping("/task/stop")
    public LutaApiResult taskStop(String taskUUID){
        LutaApplierTask lutaApplierTask = lutaApplierService.getApplierTask(taskUUID);
        lutaApplierTaskManager.stop(lutaApplierTask);
        return LutaApiResultBuilder.ok();

    }

    @RequestMapping("/mapping/detail")
    public LutaApiResult mappingDetail(Long id){
        return LutaApiResultBuilder.ok(lutaApplierService.getApplierMapping(id));
    }
    @RequestMapping("/mapping/list")
    public LutaApiResult<List<LutaApplierMapping>> mappingList(Long applierChannelId){
        return LutaApiResultBuilder.ok(lutaApplierService.getApplierMappingList(applierChannelId));
    }

    @RequestMapping("/mapping/remove")
    public LutaApiResult mappingRemove(Long id){
        lutaApplierService.deleteApplierMapping(id);
        return LutaApiResultBuilder.ok();
    }
    @RequestMapping("/trx/list")
    public LutaApiResult<List<LutaApplierTransaction>> trxList(Long applierChannelId){
        return LutaApiResultBuilder.ok(lutaApplierService.getApplierTrxList(applierChannelId));
    }
    @RequestMapping(value = "/trx/status", method = RequestMethod.POST)
    public LutaApiResult setTrxStatus2Ignore(@RequestParam(required=true) Long id,
                                                      @RequestParam(required=true) Integer status){
        lutaApplierService.updateApplierTrxStatus(id,status);
        return LutaApiResultBuilder.ok();
    }
    @RequestMapping(value = "/trx/apply", method = RequestMethod.POST)
    public LutaApiResult applyApplierTrx(@RequestParam(required=true) Long id){
        try {
            lutaApplierService.applyApplierTrx(id);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return LutaApiResultBuilder.error(e.getMessage());
        }
        return LutaApiResultBuilder.ok();
    }
}
