package moodprism;

import java.util.Properties;
import org.json.simple.JSONObject;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;

@EnableScheduling
@SpringBootApplication
@ComponentScan({"moodprism"})
@EntityScan({"moodprism"})
public class MoodprismApplication
{
	public static void main(String[] args) {newTopic(); SpringApplication.run(MoodprismApplication.class, args);}
	
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {return builder.build();}

	private static void newTopic()
	{
		Properties properties = new Properties();
        properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "192.167.0.3:29092");
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		JSONObject obj = new JSONObject();
		obj.put("name", "Test_input");
       	obj.put("keys", "None");
		String recordValue = obj.toString();
        Producer<String, String> producer = new KafkaProducer<>(properties);
        ProducerRecord<String, String> record = new ProducerRecord<>("moodprismTopic", null, recordValue);

        producer.send(record);
        producer.flush();
        producer.close();
	}
}
