package io.catalyte.training.superhealth.domains.patient;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.catalyte.training.superhealth.data.PatientFactory;
import io.catalyte.training.superhealth.exceptions.ResourceNotFound;
import io.catalyte.training.superhealth.exceptions.ServiceUnavailable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.dao.DataAccessException;

@RunWith(MockitoJUnitRunner.class)
@WebMvcTest(PatientServiceImpl.class)
public class PatientServiceImplTest {
  @Rule
  public ExpectedException thrown = ExpectedException.none();
  Patient testPatient;
  List<Patient> testPatients = new ArrayList<>();

  HashMap<Long, String> patientEmails = new HashMap<>();
  PatientFactory patientFactory;
  @InjectMocks
  private PatientServiceImpl patientServiceImpl;
  @Mock
  private PatientRepository patientRepository;

  @Before
  public void setUp() {

    //Initialize Mocks
    MockitoAnnotations.initMocks(this);

    //Generate random movies to have movieIds to pull from;
    patientFactory = new PatientFactory();

    // Initialize a test purchase instance and list of purchases
    setTestPatient();
    testPatients.add(testPatient);
    testPatients.forEach(patient -> patientEmails.put(patient.getId(), patient.getEmail()));


    when(patientRepository.findAll()).thenReturn(testPatients);
    when(patientRepository.findById(anyLong())).thenReturn(Optional.of(testPatient));
    when(patientRepository.save(any())).thenReturn(testPatient);


  }

  /**
   * Helper Method to initialize a test purchase with a billing address, delivery address, credit
   * card info, and a random generated product
   */
  private void setTestPatient() {
    testPatient = new Patient(
        1L,
        "Test",
        "Patient",
        "123-45-6789",
        "test@test.com",
        "8430 W Sunset Blvd",
        "Los Angeles",
        "CA",
        "90049",
         30,
         68,
         147,
        "Self-Insured",
        "Other"
    );
  }

  @Test
  public void getAllPatientsReturnsAllPatients(){
    List<Patient> actual = patientServiceImpl.getPatients();
    assertEquals(testPatients, actual);
  }

  @Test
  public void getAllPatientsThrowsServiceUnavailable(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).findAll();
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.getPatients());
  }

  @Test
  public void getPatientByIdReturnsPatient() {
    Patient actual = patientServiceImpl.getPatientById(123L);
    assertEquals(testPatient, actual);
  }

  @Test
  public void getPatientByIdThrowsErrorWhenNotFound() {
    when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> patientServiceImpl.getPatientById(123L));
  }

  @Test
  public void getPatientByIdThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).findById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.getPatientById(123L));
  }

  @Test
  public void getPatientEmailsReturnsHashmap(){
    HashMap<Long, String> actual = patientServiceImpl.getPatientEmails();
    assertEquals(patientEmails, actual);
  }

  @Test
  public void getPatientEmailsThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).findAll();
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.getPatientEmails());
  }


  @Test
  public void saveValidPatientReturnsPatient() {
    testPatient.setEmail("newTest@test.com");
    assertEquals(testPatient, patientServiceImpl.savePatient(testPatient));
  }

  @Test
  public void savePatientThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.savePatient(testPatient));
  }

  @Test
  public void patientEmailAlreadyExistsThrowsServiceUnavailableWhenPatientSaved(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).findAll();
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.savePatient(testPatient));
  }

  @Test
  public void updateValidPatientReturnsRental(){
    assertEquals(testPatient, patientServiceImpl.updatePatient(1L, testPatient));
  }

  @Test
  public void updateRentalThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.updatePatient(1L, testPatient));
  }

  @Test
  public void updatePatientThrowsServiceUnavailableWhenFindingPatient() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).findById(any());
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.updatePatient(1L, testPatient));
  }

  @Test
  public void updatePatientThrowsResourceNotFound(){
    when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> patientServiceImpl.updatePatient(123L, testPatient));
  }
  @Test
  public void deletePatientReturnsVoid(){
    patientServiceImpl.deletePatientById(123L);
    verify(patientRepository).deleteById(anyLong());
  }

  @Test
  public void deletePatientThrowsServiceUnavailable(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).deleteById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.deletePatientById(123L));
  }

  @Test
  public void deletePatientThrowsServiceUnavailableWhenFindingPatient(){
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(patientRepository).findById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> patientServiceImpl.deletePatientById(123L));
  }

  @Test
  public void deletePatientThrowsResourceNotFound(){
    when(patientRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> patientServiceImpl.deletePatientById(123L));
  }

  @Test
  public void validateNameFormatReturnsFalseForInvalidFormat(){
    testPatient.setFirstName("1nv@l1d N@m3");
    assertEquals(false, patientServiceImpl.validateNameFormat(testPatient.getFirstName()));
  }

  @Test
  public void validateNameFormatReturnsTrueForValidFormat(){
    testPatient.setLastName("Valid");
    assertEquals(true, patientServiceImpl.validateNameFormat(testPatient.getLastName()));
  }

  @Test
  public void validateNameFormatReturnsTrueForNullValue(){
    testPatient.setFirstName(null);
    assertEquals(true, patientServiceImpl.validateNameFormat(testPatient.getFirstName()));
  }

  @Test
  public void validateSSNReturnsFalseForInvalidFormat(){
    testPatient.setSsn("N0+ V@l1d");
    assertEquals(false, patientServiceImpl.validateSSN(testPatient));
  }

  @Test
  public void validateSSNReturnsTrueForValidFormat(){
    testPatient.setSsn("123-45-6789");
    assertEquals(true, patientServiceImpl.validateSSN(testPatient));
  }

  @Test
  public void validateSSNReturnsTrueForNullValue(){
    testPatient.setSsn(null);
    assertEquals(true, patientServiceImpl.validateSSN(testPatient));
  }

  @Test
  public void validateEmailFormatReturnsFalseForInvalidFormat(){
    testPatient.setEmail("1nv@l1d 3m@1L");
    assertEquals(false, patientServiceImpl.validateEmailFormat(testPatient));
  }

  @Test
  public void validateEmailFormatReturnsTrueForValidFormat(){
    testPatient.setEmail("valid@valid.com");
    assertEquals(true, patientServiceImpl.validateEmailFormat(testPatient));
  }

  @Test
  public void validateEmailFormatReturnsTrueForNullValue(){
    testPatient.setEmail(null);
    assertEquals(true, patientServiceImpl.validateEmailFormat(testPatient));
  }

  @Test
  public void validateStateFormatReturnsFalseForInvalidState(){
    testPatient.setState("not valid");
    assertEquals(false, patientServiceImpl.validateStateFormat(testPatient));
  }

  @Test
  public void validateStateFormatReturnsTrueForValidState(){
    testPatient.setState("LA");
    assertEquals(true, patientServiceImpl.validateStateFormat(testPatient));
  }
  @Test
  public void validateStateFormatReturnsTrueForNullValue(){
    testPatient.setState(null);
    assertEquals(true, patientServiceImpl.validateStateFormat(testPatient));
  }
}
