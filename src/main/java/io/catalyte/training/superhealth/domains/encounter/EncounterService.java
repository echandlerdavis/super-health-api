package io.catalyte.training.superhealth.domains.encounter;


/**
 * This interface provides an abstraction layer for the Encounter Service
 */
public interface EncounterService {

  Encounter getEncounterById(Long patientId, Long id);

  Encounter saveEncounter(Long patientId, EncounterDTO encounterDTO);

  Encounter updateEncounter(Long patientId, Long id, EncounterDTO encounterDTO);


}
