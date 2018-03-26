package w.whateva.service.email.job.beans;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.util.StringUtils;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.api.dto.DtoGroupMessage;

import javax.mail.internet.InternetAddress;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class EmailProcessor implements ItemProcessor<DtoEmail, DtoEmail> {

    private final String emailAddressParserType;
    private final String defaultTo;

    public EmailProcessor(String emailAddressParserType, String defaultTo) {
        this.emailAddressParserType = emailAddressParserType;
        this.defaultTo = defaultTo;
    }

    public DtoEmail process(DtoEmail dtoEmail) {

        if (null == dtoEmail.getTo()) {
            dtoEmail.setTo(defaultTo);
        }

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

        if (dtoEmail instanceof DtoGroupMessage && null == dtoEmail.getSubject()) {
            dtoEmail.setSubject(dtoEmail.getSubject());
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
