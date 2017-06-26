package com.gemu.dataserver.service;

import com.gemu.dataserver.core.ModifyData;
import com.gemu.dataserver.core.ReadData;
import com.gemu.dataserver.core.WriteData;
import com.gemu.dataserver.entity.BaseData;
import com.gemu.dataserver.entity.Friend;
import com.gemu.dataserver.entity.auxiliary.EntityPage;
import com.gemu.dataserver.exception.DataAssetsNotFoundException;
import com.gemu.dataserver.exception.EntityNotFoundException;
import com.gemu.dataserver.exception.SourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import sun.misc.BASE64Encoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户 业务
 * Created by gemu on 11/06/2017.
 */
@Service
public class FriendService {

    @Autowired
    WriteData writeData;
    @Autowired
    ReadData readData;
    @Autowired
    ModifyData modifyData;

    /**
     * 登陆
     * @param username
     * @param passcode
     * @return 通关文牒（token）和用户信息
     */
    public Map<String, Object> login(String username, String passcode) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException, NoSuchAlgorithmException {
        Map<String, Object> odAndFriend = new HashMap<String, Object>();
        Map<String, String> filter = new HashMap<String, String>();
        filter.put("userName", username);
        EntityPage<BaseData> data = readData.filterRead(1, "friends", "friend", filter);
        if (data.getTotalCount() > 0) {
            if (data.getEntries() == null || data.getEntries().size() == 0) return odAndFriend;
            Friend friend = (Friend) data.getEntries().get(0);
            String userName = friend.getUserName();
            String password = friend.getPassword();
            if (!password.equals(passcode)) {
                return odAndFriend;
            }
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            BASE64Encoder base64Encoder = new BASE64Encoder();
            odAndFriend.put("OD", base64Encoder.encode(md5.digest((userName + ":" + password).getBytes())));
            friend.setPassword("");
            odAndFriend.put("friend", friend);
            // 更新最后一次登录时间
            String id = friend.getId();
            friend = new Friend();
            friend.setLastLoginTime(new Date().getTime());
            try {
                updFriend(id, friend);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        return odAndFriend;
    }

    /**
     * 添加用户
     * @param friend
     */
    public void addFriend(Friend friend) {
        writeData.write("friends", "friend", friend);
    }

    /**
     * 修改用户
     * @param friendId
     * @param friend
     */
    public boolean updFriend(String friendId, Friend friend) throws SourceNotFoundException, EntityNotFoundException, DataAssetsNotFoundException, NoSuchFieldException {
        Map<String, Object> friendMap = new HashMap<String, Object>();
        if (friend.getAge() != null) {
            friendMap.put("age", friend.getAge());
        }
        if (friend.getUserName() != null && !"".equals(friend.getUserName())) {
            friendMap.put("userName", friend.getUserName());
        }
        if (friend.getRealName() != null && !"".equals(friend.getRealName())) {
            friendMap.put("realName", friend.getRealName());
        }
        if (friend.getPassword() != null && !"".equals(friend.getPassword())) {
            friendMap.put("password", friend.getPassword());
        }
        boolean success = modifyData.update("friends", "friend", friendId, Friend.class, friendMap);
        return success;
    }

    /**
     * 获取用户
     * @return
     */
    public EntityPage<BaseData> list(int pageNo) throws EntityNotFoundException, DataAssetsNotFoundException, SourceNotFoundException {
        EntityPage<BaseData> page = readData.read(pageNo, "friends", "friend");
        return page;
    }
}
