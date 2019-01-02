package w.whateva.service.email.data.repository.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.bson.json.JsonMode;
import org.bson.json.JsonWriterSettings;
import org.junit.Test;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.query.Criteria;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.*;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.DEFAULT_CONTEXT;

public class AggregationUtilityTest {

    @Test
    public void bson() {

        Map<String, String> map = new HashMap<>();
        map.put("email", "address");

        Document doc = new Document();
        doc.append("$let", map);
        doc.append("$pipeline", new EmailPipelineBuilder("address", LocalDateTime.now(), null).toDocument());

        System.out.println(doc.toJson());
    }

    /*

    {$lookup: {
        from: "email",
                as: "email",
                let: {address: "$emails"},
        pipeline: [
        {$match:
        {$expr: {$and: [
            {$in: ["$$address", "$tos"]},
            {$gt: ["$sent", ISODate("2013-01-01T00:00:00Z")]},
            {$lte: ["$sent", ISODate("2013-04-01T00:00:00Z")]}
           ]}}
        }
       ]
    }}

    */

    @Test
    public void pipelineBuilder() {

        Map<String, String> let = new HashMap<>();
        let.put("addresses", "$emails");

        Document pipeline = new EmailPipelineBuilder(
                "address",
                LocalDateTime.now().minusYears(8),
                LocalDateTime.now().minusYears(3)).toDocument();

        PipelineLookupOperation operation = AggregationUtility.lookup("email", "email", let, pipeline);

        Document doc = operation.toDocument(DEFAULT_CONTEXT);
        System.out.println(doc.toJson(JsonWriterSettings.builder().outputMode(JsonMode.SHELL).build()));
    }
}
