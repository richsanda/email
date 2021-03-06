package w.whateva.service.email.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.util.Set;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class DtoPerson {

    @XmlAttribute
    private String name;
    @XmlElement(name = "email")
    private Set<String> emails;
}
