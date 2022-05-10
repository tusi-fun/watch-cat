package com.cat.example.web;

import com.cat.example.bean.Aa;
import com.cat.example.bean.User;
import com.cat.result.ResultData;
import com.cat.watchcat.log.annotation.LogCat;
import com.cat.watchcat.secret.annotation.SecretCat;
import com.cat.watchcat.sensitive.annotation.SensitiveCat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author hudongshan
 * @version 2021/11/22
 */
@Slf4j
@RestController
@RequestMapping(value = "example", produces = MediaType.APPLICATION_JSON_VALUE)
public class TestController {

    @LogCat(actionGroup = "example", action = "logCatExample", enableEvent = false)
    @PostMapping("logCatExample")
    public String logCatExample(String a) {
        return "ok1";
    }

    @LogCat(actionGroup = "example", action = "secretCatExample", enableEvent = false)
    @SecretCat(pongEncrypt = true, enableValid = true, enableReplay = false)
    @PostMapping("secretCatExample")
    public ResultData secretCatExample(String a, String b, Aa aa) {
        aa.setPhone("18582461287");
        aa.setAddress("成都市武侯区广安大厦185号7楼708");
        aa.setEmail("524712128@qq.com");
        aa.setName("流畅度");
        aa.setIdCard("511234520119652147");
        return new ResultData(200, "操作成功").data(aa);
    }

    @LogCat(actionGroup = "example", action = "sensitiveCatExample", enableEvent = false)
    @PostMapping("sensitiveCatExample")
    @SensitiveCat
    public ResultData sensitiveCatExample() {

        User user1 = new User();
        user1.setIdCard("51138119910256117X");
        user1.setRealName("倪大爷");
        user1.setPassword("acbdfsdf12321312");
        user1.setPhone("15184433169");

        User user2 = new User();
        user2.setIdCard("511381198902561171");
        user2.setRealName("张三");
        user2.setPassword("123456789");
        user2.setPhone("15184433179");

        // List 测试数据
        List<User> users = new ArrayList<User>();
        users.add(user1);
        users.add(user2);

        // Map 测试数据
        Map<String,User> userMap = new HashMap<>();
        userMap.put("user1",user1);
        userMap.put("user2",user2);

        return new ResultData(200, "操作成功").data(userMap);
    }
//
//    @LogCat(actionGroup = "example", action = "areaCatExample", enableEvent = false)
//    @PostMapping("areaCatExample")
//    public ResultData areaCatExample(@RequestParam String areaCodes1,
//                                     @RequestParam String areaCodes2,
//                                     @AreaCat(areaCodesField = "areaCodes1") AreaInfo areaInfo1,
//                                     @AreaCat(areaCodesField = "areaCodes2") AreaInfo areaInfo2) {
//
//        System.out.println(areaInfo1);
//
//        System.out.println(areaInfo2);
//
//        return new ResultData(200, "操作成功").data(areaInfo1);
//    }

}