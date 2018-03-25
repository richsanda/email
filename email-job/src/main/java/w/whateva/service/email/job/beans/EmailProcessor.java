package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.service.email.api.dto.DtoEmail;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailProcessor implements ItemProcessor<DtoEmail, DtoEmail> {

    private final String emailAddressParserType;

    public EmailProcessor(String emailAddressParserType) {
        this.emailAddressParserType = emailAddressParserType;
    }

    public DtoEmail process(DtoEmail dtoEmail) {

        switch (emailAddressParserType.toLowerCase()) {
            case "simple":
                dtoEmail.setTos(toSimpleAddresses(dtoEmail.getTo()));
                break;
            case "internet":
                dtoEmail.setTos(toEmailAddresses(dtoEmail.getTo()));
                break;
            default:
                throw new IllegalArgumentException("Unknown email address parser type");
        }

        return dtoEmail;
    }

    private static Set<String> toSimpleAddresses(String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        return Arrays
                .stream(addressList.split("\\s*[,;]\\s*"))
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    private static Set<String> toEmailAddresses (String addressList) {
        if (StringUtils.isEmpty(addressList)) return new HashSet<>();
        try {
            return Arrays
                    .stream(InternetAddress.parse(addressList))
                    .map(InternetAddress::getAddress)
                    .map(String::toLowerCase)
                    .collect(Collectors.toSet());
        } catch (Exception e) {
            System.out.println("Problem with: " + addressList);
            // e.printStackTrace();
        }
        return new HashSet<>();
    }
}
