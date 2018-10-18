package w.whateva.service.email.data.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.List;

@Getter
@Setter
public class EmailCount {

    @Id
    private String name;
    private Long count;
    private List<String> emails;
}
