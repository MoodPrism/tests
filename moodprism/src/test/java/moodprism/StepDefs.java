package moodprism;


import static org.junit.Assert.assertThat;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;


import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.rule.EmbeddedKafkaRule;
import org.springframework.kafka.test.utils.ContainerTestUtils;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.stereotype.Service;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {MoodprismController.class})
@DirtiesContext
public class StepDefs extends CucumberTest{

	private static Logger log = LoggerFactory.getLogger(StepDefs.class);
	
	private static String TOPIC_NAME = "moodprismTopic";
	
	@Autowired
	private KafkaMessageProducerService kafkaMessageProducerService;
	
	private KafkaMessageListenerContainer<String, String> container;
	
	private BlockingQueue<ConsumerRecord<String, String>> consumerRecords;
	
	@ClassRule
	public static EmbeddedKafkaRule embeddedKafka = new EmbeddedKafkaRule(1, true, TOPIC_NAME);
	
	@Before
	public void setup()
	{
		consumerRecords = new LinkedBlockingQueue<>();
		
		ContainerProperties containerProperties = new ContainerProperties(TOPIC_NAME);
		
		Map<String, Object> consumerProperties = KafkaTestUtils.consumerProps("sender", "false", embeddedKafka.getEmbeddedKafka());
		
		DefaultKafkaConsumerFactory<String, String> consumer = new DefaultKafkaConsumerFactory<>(consumerProperties);
		container = new KafkaMessageListenerContainer<>(consumer, containerProperties);
		container.setupMessageListener((MessageListener<String, String>) record -> {
			log.debug("ListenedMessage = '{}'", record.toString());
			consumerRecords.add(record);
		});
		container.start();
		
	}
	
	@After
	public void tearDown()
	{
		container.stop();
	}
	
	
	
	
	
	
	@Test
	@Given("^I have the application running$")
	public void i_have_the_application_running() throws Throwable {
		System.out.print("Passo 1");
	 }
	
	@When("^I press the key \"([^\"]*)\"$")
	public void i_press_the_key(String word) throws Throwable {
		System.out.println(word);
		kafkaMessageProducerService.send(word);
	}
	
	@And("^The page refreshes$")
	public void the_page_refreshes() throws Throwable {
		System.out.println("Passo 3");
		
	}

	@Then("^The key should appear on the screen$")
	public void the_key_should_appear_on_the_screen() throws Throwable {
		ConsumerRecord<String, String> received = consumerRecords.poll(10, TimeUnit.SECONDS);
	    
	    
	}
	
	
	
	@Configuration
	public class KafkaProducerConfiguration
	{
		@Value("${kafka.bootstrap-servers}")
		private String bootstrapServers;
		
		
		
		public ProducerFactory<String, String> producerFactory()
		{
			Map<String, Object> configProps = new HashMap<>();
			configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
			configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
			configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
			return new DefaultKafkaProducerFactory<>(configProps);
			
		}
		
		
		public KafkaTemplate<String, String> kafkaTemplate()
		{
			return new KafkaTemplate<>(producerFactory());
		}
	}
	
	@Service
	public class KafkaMessageProducerService
	{
		private static final String TOPIC_NAME = "moodprismTopic";
		
		private final KafkaTemplate<String, String> kafkaTemplate;
		
		@Autowired
		public KafkaMessageProducerService(KafkaTemplate<String, String> kafkaTemplate)
		{
			this.kafkaTemplate = kafkaTemplate;
		}
		
		public void send(String msg)
		{
			kafkaTemplate.send(TOPIC_NAME, msg);
		}
	}
}
