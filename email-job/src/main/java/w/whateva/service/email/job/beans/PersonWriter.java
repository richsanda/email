package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import w.whateva.service.email.api.PersonOperations;
import w.whateva.service.email.api.dto.DtoPerson;

import java.util.List;

public class PersonWriter implements ItemWriter<DtoPerson> {

    private final PersonOperations personService;

    @Autowired
    public PersonWriter(PersonOperations personService) {
        this.personService = personService;
    }

    public void write(List<? extends DtoPerson> dtoPersons) throws Exception {
        for (DtoPerson dtoPerson : dtoPersons) {
            personService.addPerson(dtoPerson);
        }
    }
}
