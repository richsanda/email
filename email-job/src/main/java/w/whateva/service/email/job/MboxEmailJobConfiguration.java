package w.whateva.service.email.job;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import w.whateva.service.email.api.dto.DtoEmail;
import w.whateva.service.email.api.dto.DtoGroupMessage;
import w.whateva.service.email.api.dto.DtoPerson;
import w.whateva.service.email.job.beans.MboxReader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Configuration
@ConditionalOnProperty(name = "email.mbox.file")
public class MboxEmailJobConfiguration {

    private final JobBuilderFactory jobs;
    private final StepBuilderFactory steps;
    private final MboxEmailBatchConfiguration config;

    @Value("${email.mbox.file}")
    private String emailMboxFile;

    @Value("${person.xml.file}")
    private String personXmlFile;

    @Autowired
    public MboxEmailJobConfiguration(JobBuilderFactory jobs, StepBuilderFactory steps, MboxEmailBatchConfiguration config) {
        this.jobs = jobs;
        this.steps = steps;
        this.config = config;
    }

    @Bean
    public Job loadEmailJob() throws Exception {
        return this.jobs.get("loadEmailJob")
                .start(loadEmailStep())
                .next(loadPersonStep())
                .build();
    }

    @Bean
    public Step loadEmailStep() throws Exception {
        return this.steps.get("loadEmailStep")
                .<DtoEmail, DtoEmail>chunk(10)
                .reader(emailReader())
                .processor(config.emailProcessor())
                .writer(config.emailWriter())
                .build();
    }

    @Bean
    public Step loadPersonStep() throws Exception {
        return this.steps.get("loadPersonStep")
                .<DtoPerson, DtoPerson>chunk(10)
                .reader(personReader())
                .processor(config.personProcessor())
                .writer(config.personWriter())
                .build();
    }

    @Bean
    @StepScope
    public ItemReader<DtoEmail> emailReader() throws IOException {
        InputStream input = new FileInputStream(emailMboxFile);
        MboxReader reader = new MboxReader(input);
        return reader;
    }

    @Bean
    public Jaxb2Marshaller emailUnmarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setClassesToBeBound(DtoEmail.class, DtoPerson.class, DtoGroupMessage.class);
        marshaller.setCheckForXmlRootElement(true);
        return marshaller;
    }

    @Bean
    @StepScope
    public ResourceAwareItemReaderItemStream<DtoPerson> personReader() {
        StaxEventItemReader<DtoPerson> reader = new StaxEventItemReader<>();
        reader.setFragmentRootElementName("person");
        reader.setResource(new FileSystemResource(personXmlFile));
        reader.setUnmarshaller(emailUnmarshaller());
        return reader;
    }
}