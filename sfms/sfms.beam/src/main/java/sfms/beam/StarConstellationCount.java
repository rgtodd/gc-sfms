package sfms.beam;

import java.util.Map;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.transforms.Count;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.PDone;
import org.apache.beam.sdk.values.TypeDescriptors;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Query;
import com.google.datastore.v1.Value;
import com.google.datastore.v1.client.DatastoreHelper;

public class StarConstellationCount {

	private static final String STAR = "Star";
	private static final String STAR_CONSTELLATION = "con";;

	public interface StarConstellationCountOptions extends PipelineOptions {

		@Description("Google application project ID")
		@Required
		String getProjectId();

		void setProjectId(String value);

		@Description("Path of the file to write to")
		@Required
		String getOutput();

		void setOutput(String value);
	}

	public static void main(String[] args) {

		StarConstellationCountOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
				.as(StarConstellationCountOptions.class);

		Query.Builder dbStarQueryBuilder = Query.newBuilder();
		dbStarQueryBuilder.addKindBuilder().setName(STAR);
		Query dbStarQuery = dbStarQueryBuilder.build();

		Pipeline p = Pipeline.create(options);

		PCollection<Entity> entities = p.apply(
				DatastoreIO.v1().read()
						.withProjectId(options.getProjectId())
						.withQuery(dbStarQuery));

		PCollection<String> constellations = entities.apply(ParDo
				.of(new GetConstellationValueDoFn()));

		PCollection<KV<String, Long>> constellationCounts = constellations.apply(Count.perElement());

		MapElements<?, String> mapToString = MapElements.into(TypeDescriptors.strings());
		MapElements<KV<String, Long>, String> mapConstellationCountsToString = mapToString
				.via((KV<String, Long> wordCount) -> wordCount.getKey() + ": " + wordCount.getValue());

		PCollection<String> output = constellationCounts.apply(mapConstellationCountsToString);

		@SuppressWarnings("unused")
		PDone done = output.apply(TextIO.write().to(options.getOutput()));

		p.run().waitUntilFinish();
	}

	static class GetConstellationValueDoFn extends DoFn<Entity, String> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			Entity entity = c.element();
			Map<String, Value> properties = entity.getPropertiesMap();
			Value value = properties.get(STAR_CONSTELLATION);
			if (value != null) {
				String constellation = DatastoreHelper.getString(value);
				c.output(constellation);
			}
		}
	}
}
