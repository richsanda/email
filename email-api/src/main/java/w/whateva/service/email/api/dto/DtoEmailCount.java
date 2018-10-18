package w.whateva.service.email.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class DtoEmailCount {

    private String name;
    private Long count;
    private List<String> emails;
}
