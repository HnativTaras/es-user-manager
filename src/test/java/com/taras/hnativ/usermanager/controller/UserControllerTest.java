package com.taras.hnativ.usermanager.controller;

import com.taras.hnativ.usermanager.model.User;
import com.taras.hnativ.usermanager.model.UserModelAssembler;
import com.taras.hnativ.usermanager.repository.UserRepository;
import com.taras.hnativ.usermanager.service.UserService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.MediaTypes;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = UserController.class)
class UserControllerTest {
    @Autowired
    private MockMvc mvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserModelAssembler userModelAssembler;


    @Test
    public void getUserById() throws Exception {
        User user = new User("P0001", "Taras", 23, "tarashnativ@gmail.com");
        EntityModel<User> userEntityModel = EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));


        given(userService.findUserById("P0001")).willReturn(java.util.Optional.of(user));
        given(userModelAssembler.toModel(user)).willReturn(userEntityModel);

        mvc.perform(get("/api/users/P0001"))
                .andExpect(content().contentType(MediaTypes.HAL_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._links.self.href", is("/api/users/P0001")))
                .andExpect(jsonPath("$._links.users.href", is("/api/users")))
                .andExpect(jsonPath("$.id", is("P0001")))
                .andExpect(jsonPath("$.name", is("Taras")))
                .andExpect(jsonPath("$.age", is(23)))
                .andExpect(jsonPath("$.email", is("tarashnativ@gmail.com")));
    }

    @Test
    public void getAllUsers() throws Exception {
        User user = new User("P0001", "Taras", 23, "tarashnativ@gmail.com");
        EntityModel<User> userEntityModel = EntityModel.of(user,
                linkTo(methodOn(UserController.class).getUserById(user.getId())).withSelfRel(),
                linkTo(methodOn(UserController.class).getAllUsers()).withRel("users"));


        given(userService.findAll()).willReturn(Collections.singletonList(user));
        given(userModelAssembler.toModel(user)).willReturn(userEntityModel);

        mvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$._embedded.userList[0]._links.self.href", is("/api/users/P0001")))
                .andExpect(jsonPath("$._embedded.userList[0]._links.users.href", is("/api/users")))
                .andExpect(jsonPath("$._embedded.userList[0].id", is("P0001")))
                .andExpect(jsonPath("$._embedded.userList[0].name", is("Taras")))
                .andExpect(jsonPath("$._embedded.userList[0].age", is(23)))
                .andExpect(jsonPath("$._embedded.userList[0].email", is("tarashnativ@gmail.com")));
    }

}