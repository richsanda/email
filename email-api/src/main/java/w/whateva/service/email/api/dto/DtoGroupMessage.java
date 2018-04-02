package w.whateva.service.email.api.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "GroupMessage")
@XmlAccessorType(XmlAccessType.FIELD)
public class DtoGroupMessage extends DtoEmail {

    @XmlElement(name = "Topic")
    private String topic;

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }
}
