package io.catalyte.training.superhealth.domains.encounter;

import java.util.List;

/**
 * This interface provides an abstraction layer for the Products Service
 */
public interface EncounterService {

  List<Encounter> getEncounters();

  Encounter getEncounterById(Long id);

  Encounter saveEncounter(Encounter encounter);

  Encounter updateEncounter(Long id, Encounter encounter);

  void deleteEncounter(Long id);


}
