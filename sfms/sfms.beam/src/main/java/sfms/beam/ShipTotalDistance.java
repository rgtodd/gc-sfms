package sfms.beam;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.Read;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1.Write;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TypeDescriptors;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Key.PathElement;
import com.google.datastore.v1.Query;
import com.google.datastore.v1.Value;
import com.google.datastore.v1.Value.ValueTypeCase;
import com.google.datastore.v1.client.DatastoreHelper;

public class ShipTotalDistance {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(ShipTotalDistance.class.getName());

	private static final String SHIP_STATUS = "ShpSte";
	private static final String SHIP_STATUS_DISTANCE = "d";

	public interface StarConstellationCountOptions extends PipelineOptions {

		@Description("Google application project ID")
		@Required
		ValueProvider<String> getProjectId();

		void setProjectId(ValueProvider<String> value);

		@Description("Path of the file to write to")
		@Required
		ValueProvider<String> getOutput();

		void setOutput(ValueProvider<String> value);
	}

	public static void main(String[] args) {

		StarConstellationCountOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
				.as(StarConstellationCountOptions.class);

		Pipeline p = Pipeline.create(options);

		// Read ship distances from Google Datastore and compute totals.
		//
		PCollection<KV<String, Double>> totalShipDistances;
		{
			Query.Builder dbShipStatusQueryBuilder = Query.newBuilder();
			dbShipStatusQueryBuilder.addKindBuilder().setName(SHIP_STATUS);
			Query dbShipStatusQuery = dbShipStatusQueryBuilder.build();

			Read read = DatastoreIO.v1().read()
					.withProjectId(options.getProjectId())
					.withQuery(dbShipStatusQuery);

			PCollection<Entity> entities = p.apply(read);

			PCollection<KV<String, Double>> shipDistances = entities.apply(ParDo
					.of(new GetShipDistanceDoFn()));

			totalShipDistances = shipDistances.apply(Sum.doublesPerKey());
		}

		// Write distances to text output
		{
			MapElements<?, String> mapToString = MapElements.into(TypeDescriptors.strings());
			MapElements<KV<String, Double>, String> mapTotalShipDistancesToString = mapToString
					.via((KV<String, Double> totalShipDistance) -> totalShipDistance.getKey() + ": "
							+ totalShipDistance.getValue());

			PCollection<String> output = totalShipDistances.apply(mapTotalShipDistancesToString);

			output.apply(TextIO.write().to(options.getOutput()));
		}

		// Write distances to Google Datastore
		{
			PCollection<Entity> totalShipDistanceEntities = totalShipDistances.apply(ParDo.of(new CreateEntityDoFn()));

			Write write = DatastoreIO.v1().write()
					.withProjectId(options.getProjectId());

			totalShipDistanceEntities.apply(write);
		}

		p.run().waitUntilFinish();
	}

	static class GetShipDistanceDoFn extends DoFn<Entity, KV<String, Double>> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			Entity entity = c.element();
			Map<String, Value> properties = entity.getPropertiesMap();
			Value value = properties.get(SHIP_STATUS_DISTANCE);
			if (value != null && value.getValueTypeCase().equals(ValueTypeCase.DOUBLE_VALUE)) {
				String key = entity.getKey().getPath(0).getName();
				String[] keyComponents = key.split("-");
				String shipId = keyComponents[0];
				Double distance = DatastoreHelper.getDouble(value);
				c.output(KV.of(shipId, distance));
			}
		}
	}

	static class CreateEntityDoFn extends DoFn<KV<String, Double>, Entity> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			KV<String, Double> element = c.element();

			Key.Builder keyBuilder = Key.newBuilder();
			PathElement pathElement = keyBuilder.addPathBuilder().setKind("ShipTotalDistance").setName(element.getKey())
					.build();
			Key key = keyBuilder.setPath(0, pathElement).build();

			Entity entity = Entity.newBuilder()
					.setKey(key)
					.putProperties("distance", Value.newBuilder().setDoubleValue(element.getValue()).build())
					.build();

			c.output(entity);
		}
	}
}
