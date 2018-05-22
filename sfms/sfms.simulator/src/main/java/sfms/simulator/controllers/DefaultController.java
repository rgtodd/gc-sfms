package sfms.simulator.controllers;

import java.util.logging.Logger;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.DatastoreOptions;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Query;
import com.google.cloud.datastore.QueryResults;

@RestController
public class DefaultController {

	private final Logger logger = Logger.getLogger(DefaultController.class.getName());

	@RequestMapping("/")
	public String home() {
		return "Hello Docker World - Version 3";
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
}