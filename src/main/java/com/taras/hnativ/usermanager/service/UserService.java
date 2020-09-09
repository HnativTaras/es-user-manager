package com.taras.hnativ.usermanager.service;


import com.taras.hnativ.usermanager.controller.exception.NotFoundException;
import com.taras.hnativ.usermanager.model.User;
import com.taras.hnativ.usermanager.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public List<User> findAll() {
        Iterable<User> iterable = userRepository.findAll();
        List<User> users = new ArrayList<>();
        iterable.forEach(users::add);
        return users;
    }

    public Optional<User> findUserById(String id) {
        return userRepository.findById(id);
    }

    public List<User> findFirstPageOfUsersByName(String name) {
        Page<User> pageOfUsers = userRepository.findByName(name, PageRequest.of(0, 10));
        return pageOfUsers.getContent();
    }

    public List<User> findFirstPageOfUsersByName(String name, int page, int size) {
        Page<User> pageOfUsers = userRepository.findByName(name, PageRequest.of(page, size));
        return pageOfUsers.getContent();
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }


    public User deleteUserById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));
        userRepository.delete(user);
        return user;
    }
}
