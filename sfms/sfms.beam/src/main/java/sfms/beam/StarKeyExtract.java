package sfms.beam;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Query;

public class StarKeyExtract {

	public static void main(String[] args) {

		String projectId = "rgt-ssms";

		Query.Builder dbStarQueryBuilder = Query.newBuilder();
		dbStarQueryBuilder.addKindBuilder().setName("Star");
		Query dbStarQuery = dbStarQueryBuilder.build();

		PipelineOptions options = PipelineOptionsFactory.create();

		Pipeline p = Pipeline.create(options);

		PCollection<Entity> entities = p.apply(
				DatastoreIO.v1().read()
						.withProjectId(projectId)
						.withQuery(dbStarQuery));

		PCollection<String> keyValues = entities.apply(ParDo
				.of(new GetKeyValueDoFn()));

		@SuppressWarnings("unused")
		PDone done = keyValues.apply(TextIO.write().to("starKeys"));

		p.run().waitUntilFinish();
	}

	static class GetKeyValueDoFn extends DoFn<Entity, String> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			Entity entity = c.element();
			c.output(entity.getKey().toString());
		}
	}
}
