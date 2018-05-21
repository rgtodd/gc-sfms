package hello;

import java.util.logging.Logger;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

@SpringBootApplication
@RestController
public class Application {

	private final Logger logger = Logger.getLogger(Application.class.getName());

	@RequestMapping("/")
	public String home() {
		return "Hello Docker World - Version 2";
	}

	@RequestMapping("/test")
	public String test() {

		Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
		Query<Entity> query = Query.newEntityQueryBuilder().setKind("Ship").build();

		int count = 0;
		QueryResults<Entity> entities = datastore.run(query);
		while (entities.hasNext()) {
			Entity entity = entities.next();
			logger.info(entity.getKey().toString());
			count += 1;
		}

		return String.valueOf(count) + " records processed.";
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}