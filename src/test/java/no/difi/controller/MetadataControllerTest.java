package no.difi.controller;

import no.difi.domain.ValidationResult;
import no.difi.service.ValidatorService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class MetadataControllerTest {
    private static final String ROOT_TEMPLATE = "/";

    private MockMvc mvc;
    @Mock
    private ValidatorService validatorServiceMock;

    @Before
    public void setUp() {
        initMocks(this);
        mvc = MockMvcBuilders.standaloneSetup(new MetadataController(validatorServiceMock)).build();
    }

    @Test
    public void should_get_ok_with_mediatype_text_html_when_requesting_root() throws Exception {
        mvc.perform(MockMvcRequestBuilders.get(ROOT_TEMPLATE).accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk());
    }

    @Test
    public void should_redirect_and_get_ok_when_file_upload_fails_with_ioexception() throws Exception {
        MockMultipartFile fileMock = Mockito.mock(MockMultipartFile.class);
        when(fileMock.getName()).thenReturn("file");
        when(fileMock.getInputStream()).thenThrow(new IOException("hoho"));
        when(validatorServiceMock.validate(any(MockMultipartFile.class))).thenReturn(new ValidationResult.Builder().build());

        mvc.perform(MockMvcRequestBuilders.fileUpload(ROOT_TEMPLATE)
                .file(fileMock)
                .accept(MediaType.ALL))
                .andExpect(status().isFound());
    }

    @Test
    public void should_redirect_and_get_ok_when_file_upload_is_successful() throws Exception {
        MockMultipartFile mockMultipartFile = new MockMultipartFile("file", "ToreTang", "multipart/form-data", new byte[]{});

        mvc.perform(MockMvcRequestBuilders.fileUpload(ROOT_TEMPLATE)
                .file(mockMultipartFile)
                .accept(MediaType.ALL))
                .andExpect(status().isFound());
    }
}