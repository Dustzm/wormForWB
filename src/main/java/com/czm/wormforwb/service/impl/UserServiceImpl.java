package com.czm.wormforwb.service.impl;

import com.czm.wormforwb.mapper.UserMapper;
import com.czm.wormforwb.pojo.User;
import com.czm.wormforwb.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    @Resource
    private UserMapper userMapper;

    @Override
    public List<User> getAllUser() {
        return userMapper.queryAllUserInfo();
    }

    @Override
    public User getUserById(String uid) {
        return userMapper.queryUserById(uid);
    }
}
