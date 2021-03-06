package w.whateva.service.email.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.data.domain.Person;
import w.whateva.service.email.data.repository.PersonRepository;
import w.whateva.service.email.api.PersonOperations;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonServiceImpl implements PersonOperations {

    private final PersonRepository personRepository;

    public PersonServiceImpl(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Override
    public void addPerson(DtoPerson dtoPerson) {
        Person person = new Person();
        BeanUtils.copyProperties(dtoPerson, person);
        personRepository.save(person);
    }

    @Override
    public DtoPerson readPerson(String key) {
        Person person = personRepository.findById(key).orElse(null);
        if (null == person) return null;
        DtoPerson dtoPerson = new DtoPerson();
        BeanUtils.copyProperties(person, dtoPerson);
        return dtoPerson;
    }

    public List<String> allPersonKeys() {
        return personRepository.findAll().stream().map(Person::getId).collect(Collectors.toList());
    }

    public List<String> allNames() {
        return personRepository.findAll().stream().map(Person::getName).collect(Collectors.toList());
    }

    @Override
    public List<DtoPerson> allPersons() {
        return personRepository.findAllByOrderByNameAsc().stream().map(PersonServiceImpl::toApi).collect(Collectors.toList());
    }

    private static DtoPerson toApi(Person person) {
        if (null == person) return null;
        DtoPerson dtoPerson = new DtoPerson();
        BeanUtils.copyProperties(person, dtoPerson);
        return dtoPerson;
    }
}
