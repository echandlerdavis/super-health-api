package io.catalyte.training.sportsproducts.domains.promotions;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;


import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


/**
 * Test class for {@link PromotionalCodeController}.
 */
public class PromotionsAPITest {

    private MockMvc mockMvc;

    @Mock
    private PromotionalCodeService promotionalCodeService;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(new PromotionalCodeController(promotionalCodeService)).build();
    }


    /**
     * Tests the {@link PromotionalCodeController#createPromotionalCode(PromotionalCodeDTO)} endpoint
     * for a valid promotional code.
     *
     * @throws Exception if any error occurs while performing the test
     */
    @Test
    public void createPromotionalCode_ShouldReturn201Status_WhenGivenValidInput() throws Exception {
        String title = "SUMMER2015";
        String description = "Our summer discount for the Q3 2015 campaign";
        String type = "FLAT";
        double rate = 10.00;

        PromotionalCode promotionalCode = new PromotionalCode(title, description, PromotionalCodeType.FLAT, BigDecimal.valueOf(rate));

        when(promotionalCodeService.createPromotionalCode(any())).thenReturn(promotionalCode);

        mockMvc.perform(MockMvcRequestBuilders.post("/promotionalCodes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"SUMMER2015\", \"description\": \"Our summer discount for the Q3 2015 campaign\", \"type\": \"FLAT\", \"rate\": 10.00}"))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.content().json("{\"title\": \"SUMMER2015\", \"description\": \"Our summer discount for the Q3 2015 campaign\", \"type\": \"FLAT\", \"rate\": 10.00}"));
    }
}


