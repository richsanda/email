package w.whateva.service.email.api.dto;

import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GroupMessage")
@XmlAccessorType(XmlAccessType.FIELD)
@Getter
@Setter
public class DtoGroupMessage extends DtoEmail {

    @XmlElement(name = "Topic")
    private String topic;
}
