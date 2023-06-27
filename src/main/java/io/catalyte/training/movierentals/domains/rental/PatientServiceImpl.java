package io.catalyte.training.movierentals.domains.rental;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.catalyte.training.movierentals.constants.LoggingConstants;
import io.catalyte.training.movierentals.constants.StringConstants;
import io.catalyte.training.movierentals.domains.movie.Encounter;
import io.catalyte.training.movierentals.domains.movie.EncounterRepository;
import io.catalyte.training.movierentals.exceptions.BadRequest;
import io.catalyte.training.movierentals.exceptions.ResourceNotFound;
import io.catalyte.training.movierentals.exceptions.ServiceUnavailable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

@Service
public class PatientServiceImpl implements PatientService {

  private final Logger logger = LogManager.getLogger(PatientServiceImpl.class);

  PatientRepository patientRepository;
  RentedMovieRepository rentedMovieRepository;
  EncounterRepository movieRepository;

  @Autowired
  public PatientServiceImpl(PatientRepository patientRepository,
      RentedMovieRepository rentedMovieRepository, EncounterRepository movieRepository) {
    this.patientRepository = patientRepository;
    this.rentedMovieRepository = rentedMovieRepository;
    this.movieRepository = movieRepository;
  }

  /**
   * Retrieves all rentals from the database
   *
   * @return list of rentals
   */
  public List<Patient> getRentals() {
    try {
      return patientRepository.findAll();
    } catch (DataAccessException e) {
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Search for single rental by rental id.
   *
   * @param id Long id
   * @return a single rental object.
   */
  public Patient getRentalById(Long id) {
    Patient rental;

    try{
      rental = patientRepository.findById(id).orElse(null);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }

    if(rental !=null){
      return rental;
    } else {
      logger.info(LoggingConstants.GET_BY_ID_FAILURE(id));
      throw new ResourceNotFound(LoggingConstants.GET_BY_ID_FAILURE(id));
    }
  }

  /**
   * Persists a rental to the database
   *
   * @param newRental - the rental to persist
   * @return the persisted rental object
   */
  public Patient saveRental(Patient newRental) {
    List<String> rentalErrors = getRentalErrors(newRental);

    if(!rentalErrors.isEmpty()){
      throw new BadRequest(String.join("\n", rentalErrors));
    }

    Patient savedRental;

    try {
      savedRental = patientRepository.save(newRental);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }

    newRental.setId(savedRental.getId());
    newRental.setRentedMovies(savedRental.getRentedMovies());
    handleRentedMovies(newRental);
    savedRental.setRentedMovies(rentedMovieRepository.findByRental(newRental));

    return savedRental;
  }

  /**
   * Persists nested rented movie list to the database from a rental request object.
   * @param rental rental to be saved or updated
   */
  private void handleRentedMovies(Patient rental){
    List<RentedMovie> rentedMovieSet = rental.getRentedMovies();
    List<RentedMovie> findRentedMovies = rentedMovieRepository.findByRental(rental);

    if(findRentedMovies != null){
      rentedMovieRepository.deleteAll(findRentedMovies);
    }

    if(rentedMovieSet != null){
      rentedMovieSet.forEach(rentedMovie -> {

        rentedMovie.setRental(rental);
        rentedMovie.setId(null);

        try {
          rentedMovieRepository.save(rentedMovie);
        } catch (DataAccessException e) {
          logger.error(e.getMessage());
          throw new ServiceUnavailable(e.getMessage());
        }
      });
    }
  }

  /**
   * Updates and existing rental in the database
   * @param id - id of the existing rental to be updated
   * @param updatedRental - updated rental request object
   * @return updated Rental object
   */
  public Patient updateRental(Long id, Patient updatedRental){
    Patient findRental = patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound(LoggingConstants.UPDATE_RENTAL_FAILURE));

    List<String> rentalErrors = getRentalErrors(updatedRental);

    if(!rentalErrors.isEmpty()){
      throw new BadRequest(String.join("\n", rentalErrors));
    }

    Patient savedRental;
    findRental.setId(id);
    findRental.setRentalDate(updatedRental.getRentalDate());
    findRental.setRentedMovies(null);
    findRental.setRentedMovies(updatedRental.getRentedMovies());
    handleRentedMovies(findRental);
    findRental.setRentalTotalCost(updatedRental.getRentalTotalCost());
    try {
      savedRental = patientRepository.save(findRental);
    }catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
    return savedRental;
  }

  /**
   * Deletes rental in the database.
   * @param id - id of the rental to be deleted
   */
  public void deleteRentalById(Long id){
    patientRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFound(LoggingConstants.DELETE_RENTAL_FAILURE));

    try {
      patientRepository.deleteById(id);
    } catch (DataAccessException e){
      logger.error(e.getMessage());
      throw new ServiceUnavailable(e.getMessage());
    }
  }

  /**
   * Helper method that reads a rental and validates its properties
   *
   * @param rental rental to be validated
   * @return a list of errors
   */
  public List<String> getRentalErrors(Patient rental) {
    List<String> errors = new ArrayList<>();
    List<String> emptyFields = getRentalFieldsEmptyOrNull(rental).get("emptyFields");
    List<String> nullFields = getRentalFieldsEmptyOrNull(rental).get("nullFields");
    Set<String> rentedMovieErrors = getRentedMovieErrors(rental);

    if (!nullFields.isEmpty()) {
      errors.add(StringConstants.MOVIE_FIELDS_NULL(nullFields));
    }

    if (!emptyFields.isEmpty()) {
      errors.add(StringConstants.MOVIE_FIELDS_EMPTY(emptyFields));
    }

    if (validateTotalRentalCost(rental)) {
      errors.add(StringConstants.RENTAL_TOTAL_COST_INVALID);
    }

    if(!validateDateStringFormat(rental) &&
        !emptyFields.contains("rentalDate") &&
        !nullFields.contains("rentalDate")){
      errors.add(StringConstants.RENTAL_DATE_STRING_INVALID);
    }

    if(!rentedMovieErrors.isEmpty()){
      errors.addAll(rentedMovieErrors);
    }

    return errors;
  }

  /**
   * Checks that total rental cost is a double value greater than zero and does not have more than 2 digits after the
   * decimal
   * <p>
   * Because price is stored as a double, regardless of input the product will always have 1 digit
   * after the decimal even if input as an integer, or with 2 zeros after decimal
   *
   * @param rental movie to be validated
   * @return boolean if dailyRentalCost is valid
   */
  public Boolean validateTotalRentalCost(Patient rental) {
    if (rental.getRentalTotalCost() != null) {
      //Split price by the decimal
      String[] rentalCostString = String.valueOf(rental.getRentalTotalCost()).split("\\.");
      Boolean priceMoreThan2Decimals = rentalCostString[1].length() > 2;
      Boolean priceLessThanZero = rental.getRentalTotalCost() < 0;
      return priceLessThanZero || priceMoreThan2Decimals;
    }
    return false;
  }

  public Boolean validateDateStringFormat(Patient rental) {
    String regex = "^\\d{4}-\\d{2}-\\d{2}$";
    Pattern pattern = Pattern.compile(regex);
    if (rental.getRentalDate() != null) {
      Matcher matcher = pattern.matcher(rental.getRentalDate());
      return matcher.matches();
    }
    return false;
  }

  /**
   * Reads rental fields and checks for fields that are empty or null
   *
   * @param rental movie to be validated
   * @return A Hashmap {"emptyFields": List of empty fields, "nullFields": list of null fields}
   */
  public HashMap<String, List<String>> getRentalFieldsEmptyOrNull(Patient rental) {
    List<Field> rentalFields = Arrays.asList(Patient.class.getDeclaredFields());
    List<String> rentalFieldNames = new ArrayList<>();
    List<String> emptyFields = new ArrayList<>();
    List<String> nullFields = new ArrayList<>();
    HashMap<String, List<String>> results = new HashMap<>();
    //Get product field names
    rentalFields.forEach((field -> rentalFieldNames.add(field.getName())));
    //Remove id as product will not have an id before it is saved
    rentalFieldNames.remove("id");
    //Convert rental to a HashMap
    ObjectMapper mapper = new ObjectMapper();
    Map rentalMap = mapper.convertValue(rental, HashMap.class);
    //Loop through each fieldName to retrieve each rental mapping value of the field
    rentalFieldNames.forEach((field) -> {
      //Check if the value for the rental's field is null or empty and place in the corresponding list
      if (rentalMap.get(field) == null) {
        nullFields.add(field);
      } else if (rentalMap.get(field).toString().trim() == "") {
        emptyFields.add(field);
      }
    });

    //place each list in the results
    results.put("emptyFields", emptyFields);
    results.put("nullFields", nullFields);
    return results;
  }

  /**
   * Reads and validates nested rented movie objects from a rental attempting to be saved to the
   * database.
   * @param rental - rental to be saved or updated
   * @return list of errors pertaining to the nested rented movie list.
   */
  private Set<String> getRentedMovieErrors(Patient rental) {
    // Get rentedMovies from each Rental
    List<RentedMovie> rentedMovieSet = rental.getRentedMovies();
    Set<String> rentedMovieErrors = new HashSet<>();

    //Goes through each and adds invalid movie ids to error list.
    List<Long> invalidMovieIds = new ArrayList<>();
    Set<String> emptyFields = new HashSet<>();
    Set<String> nullFields = new HashSet<>();
    // If no rentedMovies add to error list
    if (rentedMovieSet == null || rentedMovieSet.size() == 0) {
      rentedMovieErrors.add(StringConstants.RENTAL_HAS_NO_RENTED_MOVIE);
    }else {
      rentedMovieSet.forEach(rentedMovie -> {
        if (!validateMovieIdExists(rentedMovie) && rentedMovie.getMovieId() != null) {
          invalidMovieIds.add(rentedMovie.getMovieId());
        }else if (rentedMovie.getMovieId() == null) {
          nullFields.add("movieId");
        } else if (rentedMovie.getMovieId().toString().trim() == "") {
          emptyFields.add("movieId");
        }
        if (rentedMovie.getDaysRented() <= 0) {
          rentedMovieErrors.add(StringConstants.RENTED_MOVIE_DAYS_RENTED_INVALID);
        }
      });
    }

    if(!invalidMovieIds.isEmpty()){
      rentedMovieErrors.add(StringConstants.RENTED_MOVIEID_INVALID(invalidMovieIds));
    }

    if(!emptyFields.isEmpty()){
      rentedMovieErrors.add(StringConstants.RENTED_MOVIE_FIELDS_EMPTY(emptyFields));
    }

    if(!nullFields.isEmpty()){
      rentedMovieErrors.add(StringConstants.RENTED_MOVIE_FIELDS_NULL(nullFields));
    }

    return rentedMovieErrors;
    }

  /**
   * Checks the movie repository for a valid movieId from a nested rented movie object.
   * @param rentedMovie - request object with a movie Id
   * @return Boolean if the movieId exists
   */
  private Boolean validateMovieIdExists(RentedMovie rentedMovie){
      List<Encounter> allMovies = movieRepository.findAll();
      for(Encounter movie : allMovies){
        if(movie.getId() == rentedMovie.getMovieId()){
          return true;
        }
      }
      return false;
    }

  }





