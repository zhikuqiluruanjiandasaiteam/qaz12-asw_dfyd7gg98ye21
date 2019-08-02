package com.example.demo.controller;

import com.example.demo.config.ParameterConfiguration;

import com.example.demo.service.FilesService;
import com.example.demo.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping(value = "task")
public class TaskController {

    @Autowired
    private FilesService filesService;
    @Autowired
    private TaskService taskService;

    /**
     *创建任务
     * @param file
     * @param userId
     * @param imsId
     * @param ausId
     * @param clarity
     * @param type
     * @param estimateTime
     * @param isFrameSpeed 1表示true
     * @return
     * @throws Exception
     */
    @ResponseBody
    @RequestMapping(value = "create",method = RequestMethod.POST)
    public Map create(@RequestParam(value = "file", required = true) MultipartFile file,
                      @RequestParam(value ="usr_id", required = true) Integer userId,
                      @RequestParam(value ="ims_id", required = false) Integer imsId,
                      @RequestParam(value ="aus_id", required = false) Integer ausId,
                      @RequestParam(value ="clarity", required = false) Integer clarity,
                      @RequestParam(value ="type", required = true) String type,
                      @RequestParam(value ="estimate_time", required = true)Integer estimateTime,
                      @RequestParam(value ="is_frame_speed", required = false)Integer isFrameSpeed)
            throws Exception {
        HashMap<String,Object> re=new HashMap<>(  );
        re.put("data","");
//        if(userId==null){
//            re.put("error_code",1);
//            re.put("error_msg","没有用户id");
//            return re;
//        }
        if(type.equals( ParameterConfiguration.Type.video )){
            if(imsId==null||ausId==null||clarity==null||isFrameSpeed==null){
                re.put("error_code",1);
                re.put("error_msg","没有选择图片或音频风格或清晰度或是否补帧加速");
                return re;
            }
        }else if(type.equals( ParameterConfiguration.Type.image )){
            if(imsId==null||clarity==null){
                re.put("error_code",1);
                re.put("error_msg","没有选择图片风格或清晰度");
                return re;
            }
        }else if(type.equals( ParameterConfiguration.Type.audio )){
            if(ausId==null){
                re.put("error_code",1);
                re.put("error_msg","没有选择音频风格");
                return re;
            }
        }else{
            re.put("error_code",2);
            re.put("error_msg","没有这种模式");
            return re;
        }
        Integer filesId=filesService.createFile( type,file,userId );
        if(filesId==null){
            re.put("error_code",3);
            re.put("error_msg","上传失败或文件类型不符合选择转换模式要求");
            return re;
        }
        taskService.createTask( userId, filesId, imsId, ausId, clarity, estimateTime,
                isFrameSpeed==1, type);
        //开启任务

        re.put("error_code",0);
        re.put("error_msg","");

        return re;
    }

    /**
     * 获取任务列表
     * @param userId 用户id
     * @return map
     */
    @ResponseBody
    @RequestMapping(value = "list")
    public Map list(@RequestParam(value ="usr_id", required = true) Integer userId){
        HashMap<String,Object> re=new HashMap<>(  );
        Map[] data=taskService.getList( userId );
        if(data==null){
            re.put("error_code",3);
            re.put("error_msg","查询失败");
            re.put("data","");
            return re;
        }

        re.put("error_code",0);
        re.put("error_msg","");
        re.put("data",data);
        return re;
    }
}
