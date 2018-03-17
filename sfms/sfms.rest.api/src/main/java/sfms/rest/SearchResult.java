package sfms.rest;

import java.util.List;

public class SearchResult<TEntity> {

	private List<TEntity> m_entities;

	public List<TEntity> getEntities() {
		return m_entities;
	}

	public void setEntities(List<TEntity> entities) {
		m_entities = entities;
	}

}
