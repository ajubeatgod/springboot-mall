package com.example.springbootmall.service.impl;

import com.example.springbootmall.dao.UserDao;
import com.example.springbootmall.dto.UserLoginRequest;
import com.example.springbootmall.dto.UserRegisterRequest;
import com.example.springbootmall.model.User;
import com.example.springbootmall.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.server.ResponseStatusException;

import java.util.Objects;

@Component
public class UserServiceImpl implements UserService {

    private final static Logger log = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserDao userDao;

    @Override
    public User getUserById(Integer userId) {
        return userDao.getUserById(userId);
    }

    @Override
    public Integer register(UserRegisterRequest userRegisterRequest) {
        String email = userRegisterRequest.getEmail();

        // check duplicate email
        User user = userDao.getUserByEmail(email);

        if (Objects.nonNull(user)) {
            log.warn("Duplicate email: {}", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // use MD5 to generate password's hash value
        String hashedPassword = DigestUtils.md5DigestAsHex(userRegisterRequest.getPassword().getBytes());
        userRegisterRequest.setPassword(hashedPassword);


        // create user
        return userDao.createUser(userRegisterRequest);
    }

    @Override
    public User login(UserLoginRequest userLoginRequest) {
        String email = userLoginRequest.getEmail();

        User user = userDao.getUserByEmail(email);

        // check user exists
        if (Objects.isNull(user)) {
            log.warn("{} has not been registered", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }

        // use MD5 to generate password's hash value
        String hashedPassword = DigestUtils.md5DigestAsHex(userLoginRequest.getPassword().getBytes());

        // check password
        if (user.getPassword().equals(hashedPassword)) {
            return user;
        } else {
            log.warn("The password for {} is incorrect", email);
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
    }
}
