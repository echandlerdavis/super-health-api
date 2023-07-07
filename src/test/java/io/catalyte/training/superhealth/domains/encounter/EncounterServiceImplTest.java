package io.catalyte.training.superhealth.domains.encounter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;


import io.catalyte.training.superhealth.data.PatientFactory;
import io.catalyte.training.superhealth.domains.patient.Patient;
import io.catalyte.training.superhealth.domains.patient.PatientRepository;
import io.catalyte.training.superhealth.domains.patient.PatientService;
import io.catalyte.training.superhealth.exceptions.ResourceNotFound;
import io.catalyte.training.superhealth.exceptions.ServiceUnavailable;
import java.util.ArrayList;
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
@WebMvcTest(EncounterServiceImpl.class)
public class EncounterServiceImplTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();
  Patient testPatient1;
  Encounter testEncounter1;

  EncounterDTO testEncounterDTO;
  PatientFactory patientFactory;
  List<Encounter> testEncounterList = new ArrayList<>();
  @InjectMocks
  private EncounterServiceImpl encounterServiceImpl;
  @Mock
  private PatientService patientService;
  @Mock
  private PatientRepository patientRepository;
  @Mock
  private EncounterRepository encounterRepository;

  @Before
  public void setUp() {
    MockitoAnnotations.initMocks(this);

    setTestEncounters();

    when(encounterRepository.findById(anyLong())).thenReturn(Optional.of(testEncounter1));
    when(encounterRepository.save(any())).thenReturn(testEncounter1);
  }

  private void setTestEncounters() {

    // Create Two Random Test Products
    patientFactory = new PatientFactory();
    testPatient1 = patientFactory.createRandomPatient();
    testEncounter1 = new Encounter(
        1L,
        testPatient1,
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
    testEncounterDTO = new EncounterDTO(
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

    testEncounterList.add(testEncounter1);
    testPatient1.setEncounters(testEncounterList);

  }

  @Test
  public void getEncounterById() {
    Encounter actual = encounterServiceImpl.getEncounterById(testEncounter1.getPatient().getId(),123L);
    assertEquals(testEncounter1, actual);
  }

  @Test
  public void getEncounterByIdThrowsErrorWhenNotFound() {
    when(encounterRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> encounterServiceImpl.getEncounterById(123L, 123L));
  }

  @Test
  public void getEncounterByIdThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(encounterRepository).findById(anyLong());
    assertThrows(ServiceUnavailable.class, () -> encounterServiceImpl.getEncounterById(123L,123L));
  }

  @Test
  public void saveValidEncounterReturnsEncounter() {
    assertEquals(testEncounter1, encounterServiceImpl.saveEncounter(testEncounter1.getPatient().getId(), testEncounterDTO));
  }
  @Test
  public void saveEncounterThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(encounterRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> encounterServiceImpl.saveEncounter(testEncounter1.getPatient().getId(), testEncounterDTO));
  }

  @Test
  public void updateValidEncounterReturnsEncounter(){
   assertEquals(testEncounter1, encounterServiceImpl.updateEncounter(testEncounter1.getPatient().getId(), testEncounter1.getId(), testEncounterDTO));
  }

  @Test
  public void updateEncounterThrowsServiceUnavailable() {
    doThrow(new DataAccessException("TEST EXCEPTION") {
    }).when(encounterRepository).save(any());
    assertThrows(ServiceUnavailable.class, () -> encounterServiceImpl.updateEncounter(testEncounter1.getPatient().getId(), 1L, testEncounterDTO));
  }

  @Test
  public void updateEncounterByIdThrowsErrorWhenNotFound() {
    when(encounterRepository.findById(anyLong())).thenReturn(Optional.empty());
    assertThrows(ResourceNotFound.class, () -> encounterServiceImpl.updateEncounter(testEncounter1.getPatient().getId(),123L, testEncounterDTO));
  }

  @Test
  public void validateVisitCodeFormatReturnsFalseIfInvalid(){
    testEncounterDTO.setVisitCode("n0+ v@l1d");
    assertEquals(false, encounterServiceImpl.validateVisitCodeFormat(testEncounterDTO));
  }

  @Test
  public void validateVisitCodeFormatReturnsTrueIfValid(){
    testEncounterDTO.setVisitCode("N3W 5C5");
    assertEquals(true, encounterServiceImpl.validateVisitCodeFormat(testEncounterDTO));
  }

  @Test
  public void validateVisitCodeFormatReturnsTrueIfNull(){
    testEncounterDTO.setVisitCode(null);
    assertEquals(true, encounterServiceImpl.validateVisitCodeFormat(testEncounterDTO));
  }

  @Test
  public void validateBillingCodeFormatReturnsFalseIfInvalid(){
    testEncounterDTO.setBillingCode("1nv@l1d");
    assertEquals(false, encounterServiceImpl.validateBillingCode(testEncounterDTO));
  }

  @Test
  public void validateBillingCodeFormatReturnsTrueIfValid(){
    testEncounterDTO.setBillingCode("123.456.789-11");
    assertEquals(true, encounterServiceImpl.validateBillingCode(testEncounterDTO));
  }
  @Test
  public void validateBillingCodeFormatReturnsTrueIfNull(){
    testEncounterDTO.setBillingCode(null);
    assertEquals(true, encounterServiceImpl.validateBillingCode(testEncounterDTO));
  }

  @Test
  public void validateIcd10ReturnsFalseIfInvalid(){
    testEncounterDTO.setIcd10("invalid");
    assertEquals(false, encounterServiceImpl.validateIcd10(testEncounterDTO));
  }
  @Test
  public void validateIcd10ReturnsTrueIfValid(){
    testEncounterDTO.setIcd10("D11");
    assertEquals(true, encounterServiceImpl.validateIcd10(testEncounterDTO));
  }
  @Test
  public void validateIcd10ReturnsTrueIfNull(){
    testEncounterDTO.setIcd10(null);
    assertEquals(true, encounterServiceImpl.validateIcd10(testEncounterDTO));
  }

  @Test
  public void validateCostReturnsFalseIfMoreThanTwoDecimals(){
    testEncounterDTO.setTotalCost(2.1234);
    assertEquals(false, encounterServiceImpl.validateCost(testEncounterDTO.getTotalCost()));
  }
  @Test
  public void validateCostReturnsFalseIfNegative(){
    testEncounterDTO.setCopay(-2.11);
    assertEquals(false, encounterServiceImpl.validateCost(testEncounterDTO.getCopay()));
  }
  @Test
  public void validateCostReturnsTrueIfValid(){
    testEncounterDTO.setCopay(2.11);
    assertEquals(true, encounterServiceImpl.validateCost(testEncounterDTO.getCopay()));
  }
  @Test
  public void validateCostReturnsTrueIfNull(){
    testEncounterDTO.setTotalCost(null);
    assertEquals(true, encounterServiceImpl.validateCost(testEncounterDTO.getTotalCost()));
  }

  @Test
  public void validateDateFormatReturnsFalseIfInvalid(){
    testEncounterDTO.setDate("1nV@l1d");
    assertEquals(false, encounterServiceImpl.validateDateFormat(testEncounterDTO));
  }
  @Test
  public void validateDateFormatReturnsTrueIfValid(){
    testEncounterDTO.setDate("1234-12-12");
    assertEquals(true, encounterServiceImpl.validateDateFormat(testEncounterDTO));
  }
  @Test
  public void validateDateFormatReturnsTrueIfNull(){
    testEncounterDTO.setDate(null);
    assertEquals(true, encounterServiceImpl.validateDateFormat(testEncounterDTO));
  }

  @Test
  public void validateNumberReturnsFalseForInvalidNumber(){
    testEncounterDTO.setPulse(-100);
    assertEquals(false, encounterServiceImpl.validateNumber(testEncounterDTO.getPulse()));
  }
  @Test
  public void validateNumberReturnsTrueForValidNumber(){
    testEncounterDTO.setDiastolic(200);
    assertEquals(true, encounterServiceImpl.validateNumber(testEncounterDTO.getDiastolic()));
  }
  @Test
  public void validateNumberReturnsTrueForNullValue(){
    testEncounterDTO.setSystolic(null);
    assertEquals(true, encounterServiceImpl.validateNumber(testEncounterDTO.getDiastolic()));
  }


}

