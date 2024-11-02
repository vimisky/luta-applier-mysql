package io.github.vimisky.luta.applier.mysql.controller;

import io.github.vimisky.luta.applier.mysql.entity.LutaApplierChannel;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierMapping;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTask;
import io.github.vimisky.luta.applier.mysql.entity.LutaApplierTransaction;
import io.github.vimisky.luta.applier.mysql.service.LutaApplierService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Controller
@RequestMapping("/console")
public class LutaApplierMySqlViewController {

    private static final Logger logger = LoggerFactory.getLogger(LutaApplierMySqlViewController.class);

    @Autowired
    private LutaApplierService lutaApplierService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String login(String msg){
        return "login";
    }
    @RequestMapping(value = "/logout", method = RequestMethod.GET)
    public String logout(RedirectAttributes redirectAttr){
        redirectAttr.addAttribute("msg", "已退出");
        return "login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String login(@RequestParam(required=true) String username,
                        @RequestParam(required=true) String password,
                        RedirectAttributes redirectAttr){

        Authentication authenticationRequest =
                UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        Authentication authenticationResponse =
                this.authenticationManager.authenticate(authenticationRequest);
        if (authenticationResponse.isAuthenticated()){

            return "redirect:/console/task/list";
        }else {
            redirectAttr.addAttribute("msg", "登录失败");

            return "redirect:/console/login";
        }
    }

    @RequestMapping(value = "/task/create", method = RequestMethod.GET)
    public String taskCreate(){
        return "taskCreate";
    }

    @RequestMapping(value = "/task/create", method = RequestMethod.POST)
    public String taskCreate(@RequestParam(required=true) String name,
                             String description,
                             @RequestParam(required=true) String srcHost,
                             @RequestParam(required=true) Integer srcPort,
                             @RequestParam(required=true) String srcVhost,
                             @RequestParam(required=true) String srcUsername,
                             @RequestParam(required=true) String srcPassword,
                             @RequestParam(required=true) String srcQueueName,
                             @RequestParam(required=true) String dstDriverClassName,
                             @RequestParam(required=true) String dstHost,
                             @RequestParam(required=true) Integer dstPort,
                             @RequestParam(required=true) String dstUsername,
                             @RequestParam(required=true) String dstPassword,
                             RedirectAttributes redirectAttr){

        LutaApplierChannel lutaApplierChannel = new LutaApplierChannel();
        lutaApplierChannel.setName(name);
        lutaApplierChannel.setDescription(description);
        lutaApplierChannel.setSrcHost(srcHost);
        lutaApplierChannel.setSrcPort(srcPort);
        lutaApplierChannel.setSrcVhost(srcVhost);
        lutaApplierChannel.setSrcQueueName(srcQueueName);
        lutaApplierChannel.setSrcUsername(srcUsername);
        lutaApplierChannel.setSrcPassword(srcPassword);
        lutaApplierChannel.setDstDriverClassName(dstDriverClassName);
        lutaApplierChannel.setDstHost(dstHost);
        lutaApplierChannel.setDstPort(dstPort);
        lutaApplierChannel.setDstUsername(dstUsername);
        lutaApplierChannel.setDstPassword(dstPassword);

        LutaApplierTask lutaApplierTask = lutaApplierService.createApplierTask(lutaApplierChannel);

        redirectAttr.addAttribute("taskUUID", lutaApplierTask.getUuid());

        return "redirect:/console/task/detail";
    }

    @RequestMapping(value = "/task/edit", method = RequestMethod.GET)
    public String taskEdit(String taskUUID, Model model){
        LutaApplierTask lutaApplierTask = lutaApplierService.getApplierTask(taskUUID);
        model.addAttribute("taskDetail", lutaApplierTask);
        return "taskEdit";
    }

    @RequestMapping(value = "/task/edit", method = RequestMethod.POST)
    public String taskEdit(
                            @RequestParam(required = true) Long id,
                            @RequestParam(required = true) String taskUUID,
                            @RequestParam(required = true) Long applierChannelId,
                            @RequestParam(required=true) String name,
                             String description,
                             @RequestParam(required=true) String srcHost,
                             @RequestParam(required=true) Integer srcPort,
                             @RequestParam(required=true) String srcVhost,
                             @RequestParam(required=true) String srcUsername,
                             @RequestParam(required=true) String srcPassword,
                             @RequestParam(required=true) String srcQueueName,
                             @RequestParam(required=true) String dstDriverClassName,
                             @RequestParam(required=true) String dstHost,
                             @RequestParam(required=true) Integer dstPort,
                             @RequestParam(required=true) String dstUsername,
                             @RequestParam(required=true) String dstPassword,
                             RedirectAttributes redirectAttr){

        LutaApplierChannel lutaApplierChannel = new LutaApplierChannel();
        lutaApplierChannel.setName(name);
        lutaApplierChannel.setDescription(description);
        lutaApplierChannel.setSrcHost(srcHost);
        lutaApplierChannel.setSrcPort(srcPort);
        lutaApplierChannel.setSrcVhost(srcVhost);
        lutaApplierChannel.setSrcQueueName(srcQueueName);
        lutaApplierChannel.setSrcUsername(srcUsername);
        lutaApplierChannel.setSrcPassword(srcPassword);
        lutaApplierChannel.setDstDriverClassName(dstDriverClassName);
        lutaApplierChannel.setDstHost(dstHost);
        lutaApplierChannel.setDstPort(dstPort);
        lutaApplierChannel.setDstUsername(dstUsername);
        lutaApplierChannel.setDstPassword(dstPassword);

        lutaApplierService.updateApplierTaskConfig(taskUUID, lutaApplierChannel);

        redirectAttr.addAttribute("taskUUID", taskUUID);

        return "redirect:/console/task/detail";
    }

    @RequestMapping("/task/detail")
    public String taskDetail(String taskUUID, Model model){
        LutaApplierTask lutaApplierTask = lutaApplierService.getApplierTask(taskUUID);
        model.addAttribute("taskDetail", lutaApplierTask);
        return "taskDetail";
    }

    @RequestMapping("/task/list")
    public String taskList(Model model){
        return "taskList";
    }

    @RequestMapping("/mapping/list")
    public String mappingList(@RequestParam(required=true) Long applierChannelId, Model model){
        model.addAttribute("applierChannelId", applierChannelId);
        return "mappingList";
    }
    @RequestMapping("/mapping/create")
    public String mappingCreate(@RequestParam(required=true) Long applierChannelId, Model model){
        model.addAttribute("applierChannelId", applierChannelId);
        return "mappingCreate";
    }
    @RequestMapping(value = "/mapping/create", method = RequestMethod.POST)
    public String mappingCreate(
            @RequestParam(required=true) Long applierChannelId,
            @RequestParam(required=true) Integer type,
            @RequestParam(required=true) String srcSchemaName,
            @RequestParam(required=true) String srcTableName,
            @RequestParam(required=true) String dstSchemaName,
            @RequestParam(required=true) String dstTableName,
            RedirectAttributes redirectAttr
    ){
        LutaApplierMapping lutaApplierMapping = new LutaApplierMapping();
        lutaApplierMapping.setApplierChannelId(applierChannelId);
        lutaApplierMapping.setType(type);
        lutaApplierMapping.setSrcSchemaName(srcSchemaName);
        lutaApplierMapping.setSrcTableName(srcTableName);
        lutaApplierMapping.setDstSchemaName(dstSchemaName);
        lutaApplierMapping.setDstTableName(dstTableName);

        lutaApplierService.createApplierMapping(lutaApplierMapping);

        redirectAttr.addAttribute("id", lutaApplierMapping.getId());

        return "redirect:/console/mapping/detail";
    }

    @RequestMapping("/mapping/detail")
    public String mappingDetail(@RequestParam(required=true) Long id, Model model){
        LutaApplierMapping lutaApplierMapping = lutaApplierService.getApplierMapping(id);
        model.addAttribute("applierMapping", lutaApplierMapping);
        return "mappingDetail";
    }

    @RequestMapping("/mapping/edit")
    public String mappingEdit(@RequestParam(required=true) Long id, Model model){
        LutaApplierMapping lutaApplierMapping = lutaApplierService.getApplierMapping(id);
        model.addAttribute("applierMapping", lutaApplierMapping);
        return "mappingEdit";
    }
    @RequestMapping(value = "/mapping/edit", method = RequestMethod.POST)
    public String mappingEdit(
            @RequestParam(required=true) Long id,
            @RequestParam(required=true) Long applierChannelId,
            @RequestParam(required=true) Integer type,
            @RequestParam(required=true) String srcSchemaName,
            @RequestParam(required=true) String srcTableName,
            @RequestParam(required=true) String dstSchemaName,
            @RequestParam(required=true) String dstTableName,
            RedirectAttributes redirectAttr
    ){
        LutaApplierMapping lutaApplierMapping = new LutaApplierMapping();
        lutaApplierMapping.setId(id);
        lutaApplierMapping.setApplierChannelId(applierChannelId);
        lutaApplierMapping.setType(type);
        lutaApplierMapping.setSrcSchemaName(srcSchemaName);
        lutaApplierMapping.setSrcTableName(srcTableName);
        lutaApplierMapping.setDstSchemaName(dstSchemaName);
        lutaApplierMapping.setDstTableName(dstTableName);

        lutaApplierService.updateApplierMapping(lutaApplierMapping);

        redirectAttr.addAttribute("id", lutaApplierMapping.getId());

        return "redirect:/console/mapping/detail";
    }
    @RequestMapping("/trx/list")
    public String trxList(@RequestParam(required=true) Long applierChannelId, Model model){
        model.addAttribute("applierChannelId", applierChannelId);
        return "trxList";
    }
    @RequestMapping("/trx/detail")
    public String trxDetail(@RequestParam(required=true) Long id, Model model){
        LutaApplierTransaction applierTransaction = lutaApplierService.getApplierTransaction(id);
        model.addAttribute("trx", applierTransaction);
        return "trxDetail";
    }
}
