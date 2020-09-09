package com.taras.hnativ.usermanager.controller;


import com.taras.hnativ.usermanager.controller.exception.NotFoundException;
import com.taras.hnativ.usermanager.model.User;
import com.taras.hnativ.usermanager.model.UserModelAssembler;
import com.taras.hnativ.usermanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RequestMapping(path = "/api/users")
@RestController
public class UserController {
    @Autowired
    UserService userService;

    @Autowired
    UserModelAssembler assembler;

    @GetMapping()
    public CollectionModel<EntityModel<User>> getAllUsers() {
        List<EntityModel<User>> users = userService.findAll().stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return CollectionModel.of(users, linkTo(methodOn(UserController.class).getAllUsers()).withSelfRel());
    }

    @GetMapping(path = "/{id}")
    public EntityModel<User> getUserById(@PathVariable String id) {

        User user = userService.findUserById(id).orElseThrow(() -> new NotFoundException("User not found with id " + id));

        return assembler.toModel(user);
    }

    @GetMapping(path = "/search")
    public List<User> getFirstPageOfUsersByName(@RequestParam(name = "name", required = true) String name) {
        return userService.findFirstPageOfUsersByName(name);
    }

    @PostMapping(path = "/save", consumes = "application/json")
    public User saveUser(@RequestBody User user) {

        return userService.saveUser(user);
    }


    @DeleteMapping(path = "/{id}", produces = "application/json")
    public User deleteUser(@PathVariable String id) {

        return userService.deleteUserById(id);
    }

}
