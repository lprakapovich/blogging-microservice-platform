//package com.lprakapovich.blog.publicationservice.api;
//
//import com.fasterxml.jackson.core.JsonProcessingException;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.lprakapovich.blog.publicationservice.api.dto.CreateBlogDto;
//import com.lprakapovich.blog.publicationservice.service.BlogService;
//import com.lprakapovich.blog.publicationservice.service.CategoryService;
//import com.lprakapovich.blog.publicationservice.service.SubscriptionService;
//import com.lprakapovich.blog.publicationservice.util.BlogOwnershipValidator;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.cloud.openfeign.FeignAutoConfiguration;
//import org.springframework.http.MediaType;
//import org.springframework.test.context.junit4.SpringRunner;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
//import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@RunWith(SpringRunner.class)
//@WebMvcTest(controllers = BlogRestEndpoint.class)
//@ImportAutoConfiguration({FeignAutoConfiguration.class})
//class BlogRestEndpointTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private BlogService service;
//
//    @MockBean
//    private CategoryService categoryService;
//
//    @MockBean
//    private SubscriptionService subscriptionService;
//
//    @MockBean
//    private BlogOwnershipValidator blogOwnershipValidator;
//
//    @Autowired
//    private ObjectMapper mapper;
//
//    @Test
//    void whenValidBlogDtoIsPassed_validationSucceedsAndReturns200() {
//
//    }
//
//    @Test
//    void whenMissingBlogId_validationFailsAndReturns400() throws Exception {
//
//        // given
//        CreateBlogDto dto = new CreateBlogDto();
//
//        // when
//        // then
//        mockMvc.perform(post("/publication-service/blogs")
//                .contentType(MediaType.APPLICATION_JSON)
//                .content(mapper.writeValueAsString(dto)))
//                .andDo(print())
//                .andExpect(status().isBadRequest());
//    }
//}
