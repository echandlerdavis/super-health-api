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
import io.catalyte.training.superhealth.data.EncounterFactory;
import io.catalyte.training.superhealth.data.PatientFactory;
import io.catalyte.training.superhealth.domains.patient.Patient;
import io.catalyte.training.superhealth.domains.patient.PatientRepository;
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
   * Helper method initializes a test purchase with billing address, delivery address, credit card
   * info, and product with id of 1 to be sent in POST method
   */
  private void setTestEncounters() {
    testPatient1 = patientFactory.createRandomPatient();

    patientRepository.save(testPatient1);

    randomEncounterList = encounterFactory.generateRandomEncounterList(testPatient1);
    testPatient1.setEncounters(randomEncounterList);
    encounterRepository.saveAll(randomEncounterList);


  }

  /**
   * Remove rentals that were added in setup.
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
    EncounterDTO newEncounter = new EncounterDTO(
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
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(post(ENCOUNTERS_PATH(testPatient1.getId()))
          .contentType("application/json")
          .content(mapper.writeValueAsString(newEncounter)))
        .andExpect(status().isCreated())
        .andReturn().getResponse();

    Encounter returnedEncounter = mapper.readValue(response.getContentAsString(), Encounter.class);
    assertNotNull(returnedEncounter.getId());
  }
//
//  @Test
//  public void saveRentalReturns400WhenTotalRentalCostIsNegativeNumber() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRental.setRentedMovies(newRentedMovies);
//    newRental.setRentalTotalCost(-1.00);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTAL_TOTAL_COST_INVALID));
//  }
//
//  @DirtiesContext
//  @Test
//  public void saveRentalReturns400WhenTotalRentalDateIsInvalid() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRental.setRentedMovies(newRentedMovies);
//    newRental.setRentalDate("Invalid");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTAL_DATE_STRING_INVALID));
//  }
//
//  @Test
//  public void saveRentalReturns400WhenRentedMoviesAreNull() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    newRental.setRentedMovies(null);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    String[] responseErrors = responseMap.get("errorMessage").toString().split("\n");
//    List<String> errorsList = Arrays.asList(responseErrors);
//    assertTrue(errorsList.containsAll(Arrays.asList(
//        StringConstants.RENTAL_HAS_NO_RENTED_MOVIE,
//        StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("rentedMovies"))
//    )));
//  }
//
//  @Test
//  public void saveRentalReturns400WhenRentedMoviesAreEmpty() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    newRental.setRentedMovies(new ArrayList<>());
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTAL_HAS_NO_RENTED_MOVIE));
//  }
//
//  @Test
//  public void saveRentalReturns400WhenFieldsEmpty() throws Exception {
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRental.setRentedMovies(newRentedMovies);
//    newRental.setRentalDate("");
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.MOVIE_FIELDS_EMPTY(Arrays.asList("rentalDate"))));
//  }
//
//  @DirtiesContext
//  @Test
//  public void saveRentalReturns400WhenFieldsNull() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRental.setRentedMovies(newRentedMovies);
//    newRental.setRentalDate(null);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.MOVIE_FIELDS_NULL(Arrays.asList("rentalDate"))));
//  }
//  @DirtiesContext
//  @Test
//  public void saveRentalReturns400WhenRentedMovieDaysRentedInvalid() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRentedMovies.get(0).setDaysRented(-1);
//    newRental.setRentedMovies(newRentedMovies);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage").equals(StringConstants.RENTED_MOVIE_DAYS_RENTED_INVALID));
//  }
//
//  @DirtiesContext
//  @Test
//  public void saveRentalReturns400WhenRentedMovieFieldsNull() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRentedMovies.get(0).setMovieId(null);
//    newRental.setRentedMovies(newRentedMovies);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants.RENTED_MOVIE_FIELDS_NULL(new HashSet<>(Arrays.asList("movieId")))));
//  }
//
//  @Test
//  public void saveRentalReturns400WhenRentedMovieMovieIdInvalid() throws Exception{
//    Patient newRental = encounterFactory.createRandomRental();
//    List<RentedMovie> newRentedMovies = rentedMovieFactory.generateRandomRentedMovies(newRental);
//    newRentedMovies.get(0).setMovieId(50L);
//    newRental.setRentedMovies(newRentedMovies);
//    ObjectMapper mapper = new ObjectMapper();
//    MockHttpServletResponse response = mockMvc.perform(post(RENTALS_PATH)
//            .contentType("application/json")
//            .content(mapper.writeValueAsString(newRental)))
//        .andExpect(status().isBadRequest())
//        .andReturn().getResponse();
//
//    HashMap responseMap = mapper.readValue(response.getContentAsString(), HashMap.class);
//    assertTrue(responseMap.get("errorMessage")
//        .equals(StringConstants
//            .RENTED_MOVIEID_INVALID(Arrays.asList(newRental.getRentedMovies().get(0).getMovieId()))));
//  }

  @Test
  public void updateEncounterReturns200WithMovieObject() throws Exception {
    EncounterDTO updatedEncounter = new EncounterDTO(
        testPatient1.getId(),
        "updated encounter",
        "U7I 3C3",
        "Updated Hospital",
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
    ObjectMapper mapper = new ObjectMapper();
    MockHttpServletResponse response = mockMvc.perform(put(ENCOUNTERS_PATH(testPatient1.getId()) + "/" + randomEncounterList.get(0).getId())
            .contentType("application/json")
            .content(mapper.writeValueAsString(updatedEncounter)))
        .andExpect(status().isOk())
        .andReturn().getResponse();

    Encounter returnedEncounter = mapper.readValue(response.getContentAsString(), Encounter.class);

    assertNotNull(returnedEncounter.getId());
  }


//  @Test
//  public void DeleteEncounterReturns204() throws Exception {
//    mockMvc.perform(delete(ENCOUNTERS_PATH(te) + "/" + testRental1.getId().toString()))
//        .andExpect(status().isNoContent());
//  }


}
