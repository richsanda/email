package w.whateva.service.email.api;

import w.whateva.service.email.api.dto.DtoPerson;

import java.util.List;

public interface PersonOperations {

    List<DtoPerson> allPersons();

    void addPerson(DtoPerson dtoPerson);

    DtoPerson readPerson(String key);
}
