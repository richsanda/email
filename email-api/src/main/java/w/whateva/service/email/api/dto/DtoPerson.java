package w.whateva.service.email.api.dto;

import javax.xml.bind.annotation.*;
import java.util.Set;

@XmlRootElement(name = "person")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoPerson {

    @XmlAttribute
    private String name;
    @XmlElement(name = "email")
    private Set<String> emails;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getEmails() {
        return emails;
    }

    public void setEmails(Set<String> emails) {
        this.emails = emails;
    }
}
