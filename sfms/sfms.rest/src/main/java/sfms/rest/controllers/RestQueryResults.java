package sfms.rest.controllers;

import java.util.Iterator;

import com.google.cloud.datastore.BaseEntity;
import com.google.cloud.datastore.Cursor;
import com.google.cloud.datastore.Entity;
import com.google.cloud.datastore.Key;
import com.google.cloud.datastore.ProjectionEntity;
import com.google.cloud.datastore.QueryResults;

public class RestQueryResults implements Iterator<BaseEntity<Key>> {

	private QueryResults<Entity> m_entityResults;
	private QueryResults<ProjectionEntity> m_projectionEntityResults;
	private Iterator<BaseEntity<Key>> m_iterator;

	private RestQueryResults(QueryResults<Entity> entityResults,
			QueryResults<ProjectionEntity> projectionEntityResults) {
		if (entityResults != null) {
			m_entityResults = entityResults;
			m_iterator = new EntityIterator(m_entityResults);
		} else {
			m_projectionEntityResults = projectionEntityResults;
			m_iterator = new ProjectionEntityIterator(m_projectionEntityResults);
		}
	}

	public static RestQueryResults createEntityResults(QueryResults<Entity> results) {
		return new RestQueryResults(results, null);
	}

	public static RestQueryResults createProjectionEntityResults(QueryResults<ProjectionEntity> results) {
		return new RestQueryResults(null, results);
	}

	@Override
	public boolean hasNext() {
		return m_iterator.hasNext();
	}

	@Override
	public BaseEntity<Key> next() {
		return m_iterator.next();
	}

	public Cursor getCursorAfter() {
		if (m_entityResults != null) {
			return m_entityResults.getCursorAfter();
		} else {
			return m_projectionEntityResults.getCursorAfter();
		}
	}

	private static class EntityIterator implements Iterator<BaseEntity<Key>> {

		private QueryResults<Entity> m_results;

		public EntityIterator(QueryResults<Entity> results) {
			m_results = results;
		}

		@Override
		public boolean hasNext() {
			return m_results.hasNext();
		}

		@Override
		public BaseEntity<Key> next() {
			return m_results.next();
		}
	}

	private static class ProjectionEntityIterator implements Iterator<BaseEntity<Key>> {

		private QueryResults<ProjectionEntity> m_results;

		public ProjectionEntityIterator(QueryResults<ProjectionEntity> results) {
			m_results = results;
		}

		@Override
		public boolean hasNext() {
			return m_results.hasNext();
		}

		@Override
		public BaseEntity<Key> next() {
			return m_results.next();
		}
	}
}
