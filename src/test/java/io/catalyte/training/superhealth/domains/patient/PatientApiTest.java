package io.catalyte.training.superhealth.domains.patient;

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
import io.catalyte.training.superhealth.data.PatientFactory;
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
public class PatientApiTest {

  @Autowired
  PatientRepository patientRepository;
  PatientFactory patientFactory = new PatientFactory();
  Patient testPatient1;
  Patient testPatient2;

  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    setTestPatients();
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  private void setTestPatients() {
    testPatient1 = patientFactory.createRandomPatient();
    testPatient2 = patientFactory.createRandomPatient();
    testPatient1.setEmail("test1@test.com");
    testPatient2.setEmail("test2@test.com");
    patientRepository.save(testPatient1);
    patientRepository.save(testPatient2);
  }

  @After
  public void removeTestPatients() {
    patientRepository.deleteAll();
  }

  @Test
  public void getPatientsReturns200() throws Exception {
    mockMvc.perform(get(Paths.PATIENTS_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void getPatientByIdReturnsPatientWith200() throws Exception {

   mockMvc.perform(get(Paths.PATIENTS_PATH + "/" + testPatient1.getId()))
        .andExpect(status().isOk());


  }

  @Test
  public void getPatientEmailsReturns200() throws Exception {
    mockMvc.perform(get(Paths.PATIENTS_PATH + "/emails"))
        .andExpect(status().isOk());
  }
  @Test
  public void savePatientReturns201() throws Exception {
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(testPatient1)))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    Patient returnedPatient = mapper.readValue(response.getContentAsString(), Patient.class);

    assertNotNull(returnedPatient.getId());
  }

  @Test
  public void savePatientReturns400IfNameInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setFirstName("1234");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NAME_INVALID));
  }

  @Test
  public void savePatientReturns400IfSsnInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setSsn("XX-12");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.SSN_INVALID));
  }

  @Test
  public void savePatientReturns400IfEmailFormatInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail("1234abcd");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.EMAIL_INVALID));
  }

  @Test
  public void savePatientReturns400IfStateFormatInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setState("ab3");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.STATE_INVALID));
  }

  @Test
  public void savePatientReturns400IfPostalCodeInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setPostal("ab3ed-3f");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.POSTAL_CODE_INVALID));
  }

  @Test
  public void savePatientReturns400IfAgeInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setAge(-6);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Age")));
  }

  @Test
  public void savePatientReturns400IfHeightInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setHeight(-6);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Height")));
  }

  @Test
  public void savePatientReturns400IfWeightInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setWeight(-6);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Weight")));
  }

  @Test
  public void savePatientReturns400IfGenderInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setGender("Not a gender");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.GENDER_INVALID));
  }

  @Test
  public void savePatientReturns409IfEmailAlreadyExists() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail(testPatient1.getEmail());
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
        .contentType("application/json")
        .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isConflict())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.EMAIL_ALREADY_EXISTS));

  }

  @Test
  public void savePatientReturns400IfFieldsAreNull() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setFirstName(null);
    newPatient.setCity(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .content(mapper.writeValueAsString(newPatient))
            .contentType("application/json"))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.FIELDS_NULL(Arrays.asList("firstName", "city"))));
  }

  @Test
  public void savePatientReturns400IfFieldsAreEmpty() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setFirstName("");
    newPatient.setCity("");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(Paths.PATIENTS_PATH)
            .content(mapper.writeValueAsString(newPatient))
            .contentType("application/json"))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.FIELDS_EMPTY(Arrays.asList("firstName", "city"))));
  }

  @Test
  public void savePatientReturns400WithListOfAllErrors() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail(null);
    newPatient.setSsn("");
    newPatient.setHeight(-2);
    newPatient.setPostal("XX-BB");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
            post(Paths.PATIENTS_PATH)
                .contentType("application/json")
                .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
    List<String> errorsList = Arrays.asList(responseErrors);
    assertTrue(errorsList.containsAll(Arrays.asList(
        StringConstants.NUMBER_INVALID("Height"),
        StringConstants.POSTAL_CODE_INVALID,
        StringConstants.FIELDS_NULL(Arrays.asList("email")),
        StringConstants.FIELDS_EMPTY(Arrays.asList("ssn")))));
  }

  @Test
  public void updatePatientReturns200WithPatientObject() throws Exception {
    Patient updatedPatient = patientFactory.createRandomPatient();
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId().toString())
            .contentType("application/json")
            .content(mapper.writeValueAsString(updatedPatient)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Patient returnedPatient = mapper.readValue(response.getContentAsString(), Patient.class);

    assertNotNull(returnedPatient.getId());
  }

  @Test
  public void updatePatientReturns400IfNameInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setLastName("1234");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NAME_INVALID));
  }

  @Test
  public void updatePatientReturns400IfSsnInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setSsn("XX-12");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.SSN_INVALID));
  }

  @Test
  public void updatePatientReturns400IfEmailFormatInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail("1234abcd");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.EMAIL_INVALID));
  }

  @Test
  public void updatePatientReturns400IfStateFormatInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setState("ab3");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.STATE_INVALID));
  }

  @Test
  public void updatePatientReturns400IfPostalCodeInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setPostal("ab3ed-3f");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.POSTAL_CODE_INVALID));
  }

  @Test
  public void updatePatientReturns400IfAgeInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setAge(-6);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Age")));
  }

  @Test
  public void updatePatientReturns400IfHeightInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setHeight(-6);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Height")));
  }

  @Test
  public void updatePatientReturns400IfWeightInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setWeight(-6);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.NUMBER_INVALID("Weight")));
  }

  @Test
  public void updatePatientReturns400IfGenderInvalid() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setGender("Not a gender");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.GENDER_INVALID));
  }

  @Test
  public void updatePatientReturns409IfEmailAlreadyExists() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail(testPatient2.getEmail());
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isConflict())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage").equals(StringConstants.EMAIL_ALREADY_EXISTS));

  }

  @Test
  public void updatePatientReturns400IfFieldsAreNull() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setPostal(null);
    newPatient.setState(null);
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .content(mapper.writeValueAsString(newPatient))
            .contentType("application/json"))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.FIELDS_NULL(Arrays.asList("state", "postal"))));
  }

  @Test
  public void updatePatientReturns400IfFieldsAreEmpty() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail("");
    newPatient.setInsurance("");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
            .content(mapper.writeValueAsString(newPatient))
            .contentType("application/json"))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    assertTrue(responseMap.get("errorMessage")
        .equals(StringConstants.FIELDS_EMPTY(Arrays.asList("email", "insurance"))));
  }

  @Test
  public void updatePatientReturns400WithListOfAllErrors() throws Exception {
    Patient newPatient = patientFactory.createRandomPatient();
    newPatient.setEmail(null);
    newPatient.setSsn("");
    newPatient.setHeight(-2);
    newPatient.setPostal("XX-BB");
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
            put(Paths.PATIENTS_PATH + "/" + testPatient1.getId())
                .contentType("application/json")
                .content(mapper.writeValueAsString(newPatient)))
        .andExpect(status().isBadRequest())
        .andReturn().getResponse();
    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
    List<String> errorsList = Arrays.asList(responseErrors);
    assertTrue(errorsList.containsAll(Arrays.asList(
        StringConstants.NUMBER_INVALID("Height"),
        StringConstants.POSTAL_CODE_INVALID,
        StringConstants.FIELDS_NULL(Arrays.asList("email")),
        StringConstants.FIELDS_EMPTY(Arrays.asList("ssn")))));
  }

  @Test
  public void deletePatientReturns204() throws Exception {
    mockMvc.perform(delete(Paths.PATIENTS_PATH + "/" + testPatient1.getId()))
        .andExpect(status().isNoContent());
  }


}
