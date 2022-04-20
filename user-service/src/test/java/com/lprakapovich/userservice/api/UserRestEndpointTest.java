package com.lprakapovich.userservice.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lprakapovich.userservice.domain.User;
import com.lprakapovich.userservice.exception.UserNotFoundException;
import com.lprakapovich.userservice.service.UserService;
import com.lprakapovich.userservice.util.UserFactory;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest
class UserRestEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private UserService userService;

    @Test
    void whenUserIsFound_200OkIsReturnedWithUserInBody() throws Exception {

        // given
        User user = UserFactory.createDefaultUser();
        String expectedUsername = user.getUsername();
        String expectedUserStringRepresentation = mapper.writeValueAsString(user);
        given(userService.getByUsername(expectedUsername)).willReturn(user);

        // when
        // then
        mockMvc.perform(get("/user-service/{username}", expectedUsername))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(expectedUserStringRepresentation));
    }

    @Test
    void whenUserNotFound_404NotFoundIsReturned() throws Exception {

        // given
        User user = UserFactory.createDefaultUser();
        String expectedUsername = user.getUsername();
        UserNotFoundException exception = new UserNotFoundException();
        String errorResponseStringRepresentation = mapper.writeValueAsString(exception.getErrorEnvelope());
        given(userService.getByUsername(anyString())).willThrow(exception);

        // when
        // then
        mockMvc.perform(get("/user-service/{username}", expectedUsername))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(errorResponseStringRepresentation));
    }

    @Test
    void whenUsernameIsUnique_200OkIsReturned() throws Exception {

        // given
        String username = UserFactory.USERNAME;
        given(userService.existsByUsername(username)).willReturn(false);

        // when
        // then
        mockMvc.perform(get("/user-service/check").param("username", username))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void whenUsernameIsNotUnique_406ConflictIsReturned() throws Exception {

        // given
        String username = UserFactory.USERNAME;
        given(userService.existsByUsername(username)).willReturn(true);

        // when
        // then
        mockMvc.perform(get("/user-service/check").param("username", username))
                .andDo(print())
                .andExpect(status().isConflict());
    }

    @Test
    void whenDtoWithMissingUsernameIsPassed_validationFails() throws Exception {

        // given
        UserDto dto = new UserDto();
        dto.setPassword(UserFactory.PASSWORD);

        // when
        // given
        mockMvc.perform(post("/user-service")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenDtoWithMissingPasswordIsPassed_validationFails() throws Exception {

        // given
        UserDto dto = new UserDto();
        dto.setUsername(UserFactory.USERNAME);

        // when
        // given
        mockMvc.perform(post("/user-service")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isBadRequest());
    }


    @Test
    void whenValidDtoIsPassed_userIsCreated() throws Exception {

        // given
        UserDto dto = new UserDto();
        dto.setUsername(UserFactory.USERNAME);
        dto.setPassword(UserFactory.PASSWORD);
        ArgumentCaptor<User> userArgumentCaptor = ArgumentCaptor.forClass(User.class);

        // when
        // then
        mockMvc.perform(post("/user-service")
                .content(mapper.writeValueAsString(dto))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(userService, times(1)).createUser(userArgumentCaptor.capture());
        User capturedUser = userArgumentCaptor.getValue();
        assertThat(capturedUser.getPassword()).isEqualTo(dto.getPassword());
        assertThat(capturedUser.getUsername()).isEqualTo(dto.getUsername());

    }
}
