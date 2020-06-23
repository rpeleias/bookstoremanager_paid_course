package com.rodrigopeleias.bookstoremanager.controller;

import com.rodrigopeleias.bookstoremanager.builder.AuthorDTOBuilder;
import com.rodrigopeleias.bookstoremanager.dto.AuthorDTO;
import com.rodrigopeleias.bookstoremanager.exception.AuthorNotFoundException;
import com.rodrigopeleias.bookstoremanager.service.AuthorService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static com.rodrigopeleias.bookstoremanager.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class AuthorControllerTest {

    private static final String AUTHOR_API_URL_PATH = "/api/v1/authors";

    @Mock
    private AuthorService authorService;

    @InjectMocks
    private AuthorController authorController;

    private MockMvc mockMvc;

    private AuthorDTOBuilder authorDTOBuilder;

    @BeforeEach
    void setUp() {
        authorDTOBuilder = AuthorDTOBuilder.builder().build();
        mockMvc = MockMvcBuilders.standaloneSetup(authorController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setViewResolvers((s, locale) -> new MappingJackson2JsonView())
                .build();
    }

    @Test
    void whenPOSTIsCalledThenCreatedStatusIsInformed() throws Exception {
        AuthorDTO expectedCreatedAuthorDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();

        when(authorService.create(expectedCreatedAuthorDTO)).thenReturn(expectedCreatedAuthorDTO);

        mockMvc.perform(post(AUTHOR_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(expectedCreatedAuthorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(expectedCreatedAuthorDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedCreatedAuthorDTO.getName())))
                .andExpect(jsonPath("$.age", is(expectedCreatedAuthorDTO.getAge())));
    }

    @Test
    void whenPOSTIsCalledWithoutRequiredFieldThenBadRequestStatusIsInformed() throws Exception {
        AuthorDTO expectedCreatedAuthorDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();
        expectedCreatedAuthorDTO.setName(null);

        mockMvc.perform(post(AUTHOR_API_URL_PATH)
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(expectedCreatedAuthorDTO)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void whenGETWithValidNameIsCalledThenOkStatusIsReturned() throws Exception {
        AuthorDTO expectedCreatedAuthorDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();

        when(authorService.findByName(expectedCreatedAuthorDTO.getName())).thenReturn(expectedCreatedAuthorDTO);

        mockMvc.perform(get(AUTHOR_API_URL_PATH + "/" + expectedCreatedAuthorDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(expectedCreatedAuthorDTO.getId().intValue())))
                .andExpect(jsonPath("$.name", is(expectedCreatedAuthorDTO.getName())))
                .andExpect(jsonPath("$.age", is(expectedCreatedAuthorDTO.getAge())));
    }

    @Test
    void whenGETWithInvalidNameIsCalledThenNotFoundStatusIsReturned() throws Exception {
        AuthorDTO expectedCreatedAuthorDTO = AuthorDTOBuilder.builder().build().buildAuthorDTO();

        when(authorService.findByName(expectedCreatedAuthorDTO.getName())).thenThrow(AuthorNotFoundException.class);

        mockMvc.perform(get(AUTHOR_API_URL_PATH + "/" + expectedCreatedAuthorDTO.getName())
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}