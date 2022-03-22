package com.czm.wormforwb.service;

import com.czm.wormforwb.pojo.User;

import java.util.List;

public interface UserService {

    List<User> getAllUser();

    User getUserById(String uid);

}
