package sfms.rest.db;

import com.google.cloud.datastore.Datastore;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.Query;

public class RestQuery {

	private Query<Entity> m_entityQuery;
	private Query<ProjectionEntity> m_projectionEntityQuery;

	private RestQuery(Query<Entity> entityQuery, Query<ProjectionEntity> projectionEntityQuery) {
		m_entityQuery = entityQuery;
		m_projectionEntityQuery = projectionEntityQuery;
	}

	public static RestQuery createEntityRestQuery(Query<Entity> entityQuery) {
		return new RestQuery(entityQuery, null);
	}

	public static RestQuery createProjectionEntityRestQuery(Query<ProjectionEntity> projectionEntityQuery) {
		return new RestQuery(null, projectionEntityQuery);
	}

	public RestQueryResults run(Datastore datastore) {
		if (m_entityQuery != null) {
			return RestQueryResults.createEntityResults(datastore.run(m_entityQuery));
		} else {
			return RestQueryResults.createProjectionEntityResults(datastore.run(m_projectionEntityQuery));
		}
	}
}
