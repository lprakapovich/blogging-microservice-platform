package com.lprakapovich.blog.publicationservice.api;

import com.lprakapovich.blog.publicationservice.service.PublicationService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@WebMvcTest
@AutoConfigureMockMvc
public class PublicationRestEndpointTest {

    @MockBean
    PublicationService publicationService;

    @Autowired
    PublicationRestEndpoint publicationRestEndpoint;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void whenPublicationRestEndpointInjected_thenNotNull() {
        assertThat(publicationRestEndpoint).isNotNull();
    }

    // todo:  change to created
    @Test
    public void whenPostRequestWithValidPublication_thenResponseStatusIsCreated() throws Exception {
        String publication = buildPublicationJson("Test header", "Test subheader");
        mockMvc.perform(MockMvcRequestBuilders.post("/publications")
                .content(publication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void whenPostRequestWithEmptyPublicationHeader_thenResponseStatusIsBadRequest() throws Exception {
        String publication = buildPublicationJson("", "Test subheader");
        mockMvc.perform(MockMvcRequestBuilders.post("/publications")
                .content(publication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    public void whenPostRequestWithNullPublicationHeader_thenResponseStatusIsBadRequest() throws Exception {
        String publication = buildPublicationJson(null, "Test subheader");
        mockMvc.perform(MockMvcRequestBuilders.post("/publications")
                .content(publication)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    private String buildPublicationJson(String header, String subheader) throws JSONException {
       return new JSONObject()
                .put("header", header)
                .put("subHeader", subheader)
                .toString();
    }
}
