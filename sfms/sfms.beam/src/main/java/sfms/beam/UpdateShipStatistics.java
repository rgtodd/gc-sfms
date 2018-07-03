package sfms.beam;

import java.util.Map;
import java.util.logging.Logger;

import org.apache.beam.sdk.Pipeline;
import org.apache.beam.sdk.io.TextIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreIO;
import org.apache.beam.sdk.io.gcp.datastore.DatastoreV1;
import org.apache.beam.sdk.options.Description;
import org.apache.beam.sdk.options.PipelineOptions;
import org.apache.beam.sdk.options.PipelineOptionsFactory;
import org.apache.beam.sdk.options.Validation.Required;
import org.apache.beam.sdk.options.ValueProvider;
import org.apache.beam.sdk.transforms.DoFn;
import org.apache.beam.sdk.transforms.MapElements;
import org.apache.beam.sdk.transforms.Max;
import org.apache.beam.sdk.transforms.ParDo;
import org.apache.beam.sdk.transforms.Sum;
import org.apache.beam.sdk.transforms.join.CoGbkResult;
import org.apache.beam.sdk.transforms.join.CoGroupByKey;
import org.apache.beam.sdk.transforms.join.KeyedPCollectionTuple;
import org.apache.beam.sdk.values.KV;
import org.apache.beam.sdk.values.PCollection;
import org.apache.beam.sdk.values.TupleTag;
import org.apache.beam.sdk.values.TypeDescriptors;

import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Key.PathElement;
import com.google.datastore.v1.Query;
import com.google.datastore.v1.Value;
import com.google.datastore.v1.Value.ValueTypeCase;
import com.google.datastore.v1.client.DatastoreHelper;

public class UpdateShipStatistics {

	@SuppressWarnings("unused")
	private static final Logger LOGGER = Logger.getLogger(UpdateShipStatistics.class.getName());

	private static final String SHIP_STATE = "ShpSte";
	private static final String SHIP_STATE_DISTANCE = "d";
	private static final String SHIP_STATE_SPEED = "s";

	private static final TupleTag<Double> SUM_SHIP_DISTANCE_TAG = new TupleTag<>();
	private static final TupleTag<Double> MAX_SHIP_SPEED_TAG = new TupleTag<>();

	public interface UpdateShipStatisticsOptions extends PipelineOptions {

		@Description("Google application project ID")
		@Required
		ValueProvider<String> getProjectId();

		void setProjectId(ValueProvider<String> value);

		@Description("Google Datastore emulator address")
		@Required
		ValueProvider<String> getLocalhost();

		void setLocalhost(ValueProvider<String> value);

		@Description("Path of the file to write to")
		@Required
		ValueProvider<String> getOutput();

		void setOutput(ValueProvider<String> value);
	}

	public static void main(String[] args) {

		UpdateShipStatisticsOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
				.as(UpdateShipStatisticsOptions.class);

		Pipeline p = Pipeline.create(options);

		// Read ship state records from Google Datastore.
		//
		PCollection<Entity> shipStateEntities;
		{
			Query.Builder dbShipStatusQueryBuilder = Query.newBuilder();
			dbShipStatusQueryBuilder.addKindBuilder().setName(SHIP_STATE);
			Query dbShipStatusQuery = dbShipStatusQueryBuilder.build();

			DatastoreV1.Read read = DatastoreIO.v1().read()
					.withProjectId(options.getProjectId())
					.withQuery(dbShipStatusQuery);

			if (options.getLocalhost().isAccessible()) {
				String localhost = options.getLocalhost().get();
				if (localhost != null) {
					read = read.withLocalhost(localhost);
				}
			}

			shipStateEntities = p.apply("readShipStateEntities", read);
		}

		// Read ship distances from ship state entities and compute totals.
		//
		PCollection<KV<String, Double>> totalShipDistances;
		{
			PCollection<KV<String, Double>> shipDistances = shipStateEntities.apply("extractShipDistances", ParDo
					.of(new GetShipDistanceDoFn()));

			totalShipDistances = shipDistances.apply("computeTotalShipDistances", Sum.doublesPerKey());
		}

		// Read ship distances from ship state entities and compute totals.
		//
		PCollection<KV<String, Double>> maxShipSpeeds;
		{
			PCollection<KV<String, Double>> shipSpeeds = shipStateEntities.apply("extractShipSpeeds", ParDo
					.of(new GetShipSpeedDoFn()));

			maxShipSpeeds = shipSpeeds.apply("computeMaxShipSpeeds", Max.doublesPerKey());
		}

		// Combine statistics together.
		//
		PCollection<KV<String, CoGbkResult>> shipStatistics = KeyedPCollectionTuple
				.of(SUM_SHIP_DISTANCE_TAG, totalShipDistances)
				.and(MAX_SHIP_SPEED_TAG, maxShipSpeeds)
				.apply("combineShipStatistics", CoGroupByKey.create());

		// Write statistics to Google Datastore
		//
		{
			PCollection<Entity> shipStatisticEntities = shipStatistics
					.apply("createShipStatisticEntities", ParDo.of(new CreateShipStaticsticsEntityDoFn()));

			DatastoreV1.Write write = DatastoreIO.v1().write()
					.withProjectId(options.getProjectId());

			shipStatisticEntities.apply("writeShipStatisticEntities", write);
		}

		// Write distances to text output
		{
			MapElements<?, String> mapToString = MapElements.into(TypeDescriptors.strings());
			MapElements<KV<String, Double>, String> mapTotalShipDistancesToString = mapToString
					.via((KV<String, Double> totalShipDistance) -> totalShipDistance.getKey() + ": "
							+ totalShipDistance.getValue());

			PCollection<String> output = totalShipDistances.apply("mapTotalShipDistancesToString",
					mapTotalShipDistancesToString);

			TextIO.Write write = TextIO.write().to(options.getOutput());

			output.apply("writeTotalShipDistancesText", write);
		}

		// Write distances to Google Datastore
		{
			PCollection<Entity> totalShipDistanceEntities = totalShipDistances
					.apply("createTotalShipDistanceEntities", ParDo.of(new CreateShipTotalDistanceEntityDoFn()));

			DatastoreV1.Write write = DatastoreIO.v1().write()
					.withProjectId(options.getProjectId());

			totalShipDistanceEntities.apply("writeTotalShipDistanceEntities", write);
		}

		p.run().waitUntilFinish();
	}

	static class GetShipDistanceDoFn extends DoFn<Entity, KV<String, Double>> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			Entity entity = c.element();
			Map<String, Value> properties = entity.getPropertiesMap();
			Value value = properties.get(SHIP_STATE_DISTANCE);
			if (value != null && value.getValueTypeCase().equals(ValueTypeCase.DOUBLE_VALUE)) {
				String key = entity.getKey().getPath(0).getName();
				String[] keyComponents = key.split("-");
				String shipId = keyComponents[0];
				Double distance = DatastoreHelper.getDouble(value);
				c.output(KV.of(shipId, distance));
			}
		}
	}

	static class GetShipSpeedDoFn extends DoFn<Entity, KV<String, Double>> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			Entity entity = c.element();
			Map<String, Value> properties = entity.getPropertiesMap();
			Value value = properties.get(SHIP_STATE_SPEED);
			if (value != null && value.getValueTypeCase().equals(ValueTypeCase.DOUBLE_VALUE)) {
				String key = entity.getKey().getPath(0).getName();
				String[] keyComponents = key.split("-");
				String shipId = keyComponents[0];
				Double distance = DatastoreHelper.getDouble(value);
				c.output(KV.of(shipId, distance));
			}
		}
	}

	static class CreateShipTotalDistanceEntityDoFn extends DoFn<KV<String, Double>, Entity> {

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

	static class CreateShipStaticsticsEntityDoFn extends DoFn<KV<String, CoGbkResult>, Entity> {

		private static final long serialVersionUID = 1L;

		@ProcessElement
		public void processElement(ProcessContext c) {
			KV<String, CoGbkResult> element = c.element();

			Double totalShipDistance = element.getValue().getOnly(SUM_SHIP_DISTANCE_TAG, null);
			Double maxShipSpeed = element.getValue().getOnly(MAX_SHIP_SPEED_TAG, null);

			Key.Builder keyBuilder = Key.newBuilder();
			PathElement pathElement = keyBuilder.addPathBuilder().setKind("ShpStat").setName(element.getKey())
					.build();
			Key key = keyBuilder.setPath(0, pathElement).build();

			Entity entity = Entity.newBuilder()
					.setKey(key)
					.putProperties("t_d", Value.newBuilder().setDoubleValue(totalShipDistance).build())
					.putProperties("mx_s", Value.newBuilder().setDoubleValue(maxShipSpeed).build())
					.build();

			c.output(entity);
		}
	}
}
