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
  Patient testPatient1 = patientFactory.createRandomPatient();
  Patient testPatient2 = patientFactory.createRandomPatient();
  @Autowired
  private WebApplicationContext wac;
  private MockMvc mockMvc;

  @Before
  public void setUp() {
    setTestMovies();
    mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
  }

  private void setTestMovies() {
    patientRepository.save(testPatient1);
    patientRepository.save(testPatient2);
  }

  @After
  public void removeTestMovies() {
    patientRepository.delete(testPatient1);
    patientRepository.delete(testPatient2);
  }

  @Test
  public void getPatientsReturns200() throws Exception {
    mockMvc.perform(get(Paths.PATIENTS_PATH))
        .andExpect(status().isOk());
  }

  @Test
  public void getPatientByIdReturnsPatientWith200() throws Exception {
    ObjectMapper mapper = new ObjectMapper();

   MockHttpServletResponse response = mockMvc.perform(get(Paths.PATIENTS_PATH + "/" + testPatient1.getId().toString()))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Patient returnedPatient = mapper.readValue(response.getContentAsString(), Patient.class);

    assert (returnedPatient.equals(testPatient1));

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

    assert (returnedPatient.equals(testPatient1));
    assertNotNull(returnedPatient.getId());
  }

//  @Test
//  public void saveMovieReturns400IfDailyRentalCostIsNegativeNumber() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setDailyRentalCost(-1.00);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(MOVIES_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.MOVIE_RENTAL_COST_INVALID));
//  }

//  @Test
//  public void saveMovieReturns400IfSkuIsInvalid() throws Exception {
//    //This test fails when run with coverage
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setSku("XX-12");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(MOVIES_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.MOVIE_SKU_INVALID));
//  }
//
//  @Test
//  public void saveMovieReturns409IfSkuAlreadyExists() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setSku(testMovie1.getSku());
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(MOVIES_PATH)
//        .contentType("application/json")
//        .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isConflict())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.MOVIE_SKU_ALREADY_EXISTS));
//
//  }

//  @Test
//  public void saveMovieReturns400IfFieldsAreNull() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setTitle(null);
//    newMovie.setDirector(null);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(MOVIES_PATH)
//            .content(mapper.writeValueAsString(newMovie))
//            .contentType("application/json"))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("title", "director"))));
//  }

//  @Test
//  public void saveMovieReturns400IfFieldsAreEmpty() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setTitle("");
//    newMovie.setDirector("");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(MOVIES_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.MOVIE_FIELDS_EMPTY(Arrays.asList("title", "director"))));
//  }

//  @Test
//  public void saveMovieReturns400WithListOfAllErrors() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setGenre(null);
//    newMovie.setDirector("");
//    newMovie.setDailyRentalCost(-2.00);
//    newMovie.setSku("XX-BB");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//            post(MOVIES_PATH)
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
//    List<String> errorsList = Arrays.asList(responseErrors);
//    assertTrue(errorsList.containsAll(Arrays.asList(
//        StringConstants.MOVIE_RENTAL_COST_INVALID,
//        StringConstants.MOVIE_SKU_INVALID,
//        StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("genre")),
//        StringConstants.MOVIE_FIELDS_EMPTY(Arrays.asList("director")))));
//  }

  @Test
  public void updateMovieReturns200WithMovieObject() throws Exception {
    Patient updatedPatient = patientFactory.createRandomPatient();
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(
        put(Paths.PATIENTS_PATH + "/" + testPatient1.getId().toString())
            .contentType("application/json")
            .content(mapper.writeValueAsString(updatedPatient)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Patient returnedPatient = mapper.readValue(response.getContentAsString(), Patient.class);

    assert (returnedPatient.equals(updatedPatient));
    assertNotNull(returnedPatient.getId());
  }

//  @Test
//  public void updateMovieReturns400IfDailyRentalCostIsNegativeNumber() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setDailyRentalCost(-1.00);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//        put(MOVIES_PATH + "/" + testMovie1.getId().toString())
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.MOVIE_RENTAL_COST_INVALID));
//  }

//  @Test
//  public void updateMovieReturns400IfSkuIsInvalid() throws Exception {
//    //This test fails when run with coverage
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setSku("XX-12");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//        put(MOVIES_PATH + "/" + testMovie1.getId().toString())
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.MOVIE_SKU_INVALID));
//  }

//  @Test
//  public void updateMovieReturns409IfSkuAlreadyExists() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setSku(testMovie1.getSku());
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//        put(MOVIES_PATH + "/" + testMovie2.getId().toString())
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isConflict())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.MOVIE_SKU_ALREADY_EXISTS));
//
//  }

//  @Test
//  public void updateMovieReturns400IfFieldsAreNull() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setTitle(null);
//    newMovie.setDirector(null);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//        put(MOVIES_PATH + "/" + testMovie1.getId().toString())
//            .content(mapper.writeValueAsString(newMovie))
//            .contentType("application/json"))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("title", "director"))));
//  }

//  @Test
//  public void updateMovieReturns400IfFieldsAreEmpty() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setTitle("");
//    newMovie.setDirector("");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//        put(MOVIES_PATH + "/" + testMovie1.getId().toString())
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.MOVIE_FIELDS_EMPTY(Arrays.asList("title", "director"))));
//  }

//  @Test
//  public void updateMovieReturns400WithListOfAllErrors() throws Exception {
//    Encounter newMovie = patientFactory.createRandomMovie();
//    newMovie.setGenre(null);
//    newMovie.setDirector("");
//    newMovie.setDailyRentalCost(-2.00);
//    newMovie.setSku("XX-BB");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(
//        put(MOVIES_PATH + "/" + testMovie1.getId().toString())
//                .contentType("application/json")
//                .content(mapper.writeValueAsString(newMovie)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
//    List<String> errorsList = Arrays.asList(responseErrors);
//    assertTrue(errorsList.containsAll(Arrays.asList(
//        StringConstants.MOVIE_RENTAL_COST_INVALID,
//        StringConstants.MOVIE_SKU_INVALID,
//        StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("genre")),
//        StringConstants.MOVIE_FIELDS_EMPTY(Arrays.asList("director")))));
//  }

  @Test
  public void deleteMovieReturns204() throws Exception {
    mockMvc.perform(delete(Paths.PATIENTS_PATH + "/" + testPatient1.getId().toString()))
        .andExpect(status().isNoContent());
  }

}
