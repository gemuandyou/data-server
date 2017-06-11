package com.gemu.dataserver.window;

import com.gemu.dataserver.entity.Friend;
import com.gemu.dataserver.entity.auxiliary.param.LoginParam;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import com.gemu.dataserver.service.FriendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户 接口
 * Created by gemu on 11/06/2017.
 */
@RestController
@RequestMapping("friend")
public class FriendWin {

    @Autowired
    FriendService friendService;

    /**
     * 登陆
     * @param loginParam
     * @return
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Map<String, Object> login(@RequestBody LoginParam loginParam) {
        Map<String, Object> odAndFriend = new HashMap();
        try {
            odAndFriend = friendService.login(loginParam.getUsername(), loginParam.getPasscode());
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
        } catch (DataAssetsNotFoundException e) {
            e.printStackTrace();
        } catch (SourceNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return odAndFriend;
    }

    /**
     * 添加用户
     * @param friend
     */
    @RequestMapping(value = "add", method = RequestMethod.POST)
    public void add(Friend friend) {
        friendService.addFriend(friend);
    }

    @RequestMapping(value = "test", method = RequestMethod.POST)
    public void test(String a) {
        System.out.println(a);
    }

}
