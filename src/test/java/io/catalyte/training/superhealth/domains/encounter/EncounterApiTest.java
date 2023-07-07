package io.catalyte.training.superhealth.domains.encounter;

import static io.catalyte.training.superhealth.constants.Paths.ENCOUNTERS_PATH;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.superhealth.constants.Paths;
import io.catalyte.training.superhealth.constants.StringConstants;
import io.catalyte.training.superhealth.data.EncounterFactory;
import io.catalyte.training.superhealth.data.PatientFactory;
import io.catalyte.training.superhealth.domains.patient.Patient;
import io.catalyte.training.superhealth.domains.patient.PatientRepository;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest
public class EncounterApiTest {

  private final EncounterFactory encounterFactory = new EncounterFactory();
  private final PatientFactory patientFactory = new PatientFactory();
  Patient testPatient1;
  List<Encounter> randomEncounterList;

  EncounterDTO encounterDTO;
  @Autowired
  public PatientRepository patientRepository;
  @Autowired
  public EncounterRepository encounterRepository;
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    setTestEncounters();
  }

  /**
   * Helper method initializes a test encounter with random info and testPatient as patient,
   * initializes a DTO to be saved.
   *
   */
  private void setTestEncounters() {
    testPatient1 = patientFactory.createRandomPatient();

    patientRepository.save(testPatient1);

    randomEncounterList = encounterFactory.generateRandomEncounterList(testPatient1);
    testPatient1.setEncounters(randomEncounterList);
    encounterRepository.saveAll(randomEncounterList);

    encounterDTO = new EncounterDTO(
        testPatient1.getId(),
        "new encounter",
        "N3W 3C3",
        "New Hospital",
        "123.456.789-00",
        "Z99",
        0.11,
        0.11,
        "new complaint",
        78,
        120,
        80,
        "2020-08-04"
    );


  }

  /**
   * Remove patients and encounters that were added in setup.
   */
  @After
  public void tearDown() {
    //delete encounters
    encounterRepository.deleteAll();

    //delete patients
    patientRepository.deleteAll();

  }

  @Test
  public void getEncounterByIdReturns200() throws Exception {
    mockMvc.perform(get(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId()))
        .andExpect(status().isOk());
  }


  @Test
  public void saveEncounterReturns201WithEncounterObject() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
          .contentType("application/json")
          .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    Encounter returnedEncounter = mapper.readValue(response.getContentAsString(), Encounter.class);
    assertNotNull(returnedEncounter.getId());
  }

  @Test
  public void saveEncounterReturns400WhenVisitCodeIsInvalid() throws Exception{
    encounterDTO.setVisitCode("n0+ V@l1d");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.VISIT_CODE_INVALID));
  }

  @Test
  public void saveEncounterReturns400WhenBillingCodeIsInvalid() throws Exception{
    encounterDTO.setBillingCode("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.BILLING_CODE_INVALID));
  }

  @Test
  public void saveEncounterReturns400WhenIcd10Invalid() throws Exception{
    encounterDTO.setIcd10("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.ICD10_INVALID));
  }

  @Test
  public void saveEncounterReturns400WhenTotalCostInvalid() throws Exception{
    encounterDTO.setTotalCost(-1.00);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.COST_INVALID("Total cost")));
  }

  @Test
  public void saveEncounterReturns400WhenCopayInvalid() throws Exception{
    encounterDTO.setCopay(-1.00);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.COST_INVALID("Copay")));
  }

  @Test
  public void saveEncounterReturns400PulseNotNullButInvalid() throws Exception{
    encounterDTO.setPulse(-100);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Pulse")));
  }

  @Test
  public void saveEncounterReturns400SystolicNotNullButInvalid() throws Exception{
    encounterDTO.setSystolic(-100);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Systolic")));
  }

  @Test
  public void saveEncounterReturns400DiastolicNotNullButInvalid() throws Exception{
    encounterDTO.setDiastolic(-100);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Diastolic")));
  }

  @Test
  public void saveEncounterReturns400WhenDateInvalid() throws Exception{
    encounterDTO.setDate("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.DATE_INVALID));
  }

  @Test
  public void saveEncountersReturns400WhenRequiredFieldsAreNull() throws Exception{
    encounterDTO.setVisitCode(null);
    encounterDTO.setTotalCost(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(
        StringConstants.FIELDS_NULL(Arrays.asList("visitCode", "totalCost")))
    );
  }

  @Test
  public void saveEncountersReturns400WhenRequiredFieldsAreEmpty() throws Exception{
    encounterDTO.setVisitCode("");
    encounterDTO.setDate("");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(
        StringConstants.FIELDS_EMPTY(Arrays.asList("visitCode", "date")))
    );
  }

  @Test
  public void saveEncounterReturns400WithListOfAllErrors() throws Exception {
    encounterDTO.setVisitCode(null);
    encounterDTO.setBillingCode("");
    encounterDTO.setPulse(-2);
    encounterDTO.setDate("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
            post(ENCOUNTERS_PATH(testPatient1.getId()))
                .contentType("application/json")
                .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
    List<String> errorsList = Arrays.asList(responseErrors);
    assertTrue(errorsList.containsAll(Arrays.asList(
        StringConstants.NUMBER_INVALID("Pulse"),
        StringConstants.DATE_INVALID,
        StringConstants.FIELDS_NULL(Arrays.asList("visitCode")),
        StringConstants.FIELDS_EMPTY(Arrays.asList("billingCode")))));
  }

  @Test
  public void updateEncounterReturns200WithEncounterObject() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Encounter returnedEncounter = mapper.readValue(response.getContentAsString(), Encounter.class);

    assertNotNull(returnedEncounter.getId());
  }

  @Test
  public void updateEncounterReturns400WhenVisitCodeIsInvalid() throws Exception{
    encounterDTO.setVisitCode("n0+ V@l1d");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.VISIT_CODE_INVALID));
  }

  @Test
  public void updateEncounterReturns400WhenBillingCodeIsInvalid() throws Exception{
    encounterDTO.setBillingCode("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.BILLING_CODE_INVALID));
  }

  @Test
  public void updateEncounterReturns400WhenIcd10Invalid() throws Exception{
    encounterDTO.setIcd10("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.ICD10_INVALID));
  }

  @Test
  public void updateEncounterReturns400WhenTotalCostInvalid() throws Exception{
    encounterDTO.setTotalCost(-1.00);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.COST_INVALID("Total cost")));
  }

  @Test
  public void updateEncounterReturns400WhenCopayInvalid() throws Exception{
    encounterDTO.setCopay(-1.00);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.COST_INVALID("Copay")));
  }

  @Test
  public void updateEncounterReturns400PulseNotNullButInvalid() throws Exception{
    encounterDTO.setPulse(-100);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Pulse")));
  }

  @Test
  public void updateEncounterReturns400SystolicNotNullButInvalid() throws Exception{
    encounterDTO.setSystolic(-100);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Systolic")));
  }

  @Test
  public void updateEncounterReturns400DiastolicNotNullButInvalid() throws Exception{
    encounterDTO.setDiastolic(-100);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Diastolic")));
  }

  @Test
  public void updateEncounterReturns400WhenDateInvalid() throws Exception{
    encounterDTO.setDate("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.DATE_INVALID));
  }

  @Test
  public void updateEncountersReturns400WhenRequiredFieldsAreNull() throws Exception{
    encounterDTO.setVisitCode(null);
    encounterDTO.setTotalCost(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(
        StringConstants.FIELDS_NULL(Arrays.asList("visitCode", "totalCost")))
    );
  }

  @Test
  public void updateEncountersReturns400WhenRequiredFieldsAreEmpty() throws Exception{
    encounterDTO.setVisitCode("");
    encounterDTO.setDate("");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();

    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(
        StringConstants.FIELDS_EMPTY(Arrays.asList("visitCode", "date")))
    );
  }

  @Test
  public void updateEncounterReturns400WithListOfAllErrors() throws Exception {
    encounterDTO.setVisitCode(null);
    encounterDTO.setBillingCode("");
    encounterDTO.setPulse(-2);
    encounterDTO.setDate("Invalid");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
            put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(encounterDTO)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
    List<String> errorsList = Arrays.asList(responseErrors);
    assertTrue(errorsList.containsAll(Arrays.asList(
        StringConstants.NUMBER_INVALID("Pulse"),
        StringConstants.DATE_INVALID,
        StringConstants.FIELDS_NULL(Arrays.asList("visitCode")),
        StringConstants.FIELDS_EMPTY(Arrays.asList("billingCode")))));
  }


}
