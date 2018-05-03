package sfms.rest.api;

import java.util.List;

/**
 * Defines the response returned by REST service search methods.
 *
 * @param <TEntity>
 *            the type of the entities returned by the service.
 * 
 */
public class SearchResult<TEntity> {

	private String m_startingBookmark;
	private String m_endingBookmark;
	private Long m_pageSize;
	private Long m_pageIndex;
	private String m_sort;
	private String m_filter;
	private Boolean m_endOfResults;
	private List<TEntity> m_entities;

	public String getStartingBookmark() {
		return m_startingBookmark;
	}

	public void setStartingBookmark(String startingBookmark) {
		m_startingBookmark = startingBookmark;
	}

	public String getEndingBookmark() {
		return m_endingBookmark;
	}

	public void setEndingBookmark(String endingBookmark) {
		m_endingBookmark = endingBookmark;
	}

	public Long getPageSize() {
		return m_pageSize;
	}

	public void setPageSize(Long pageSize) {
		m_pageSize = pageSize;
	}

	public Long getPageIndex() {
		return m_pageIndex;
	}

	public void setPageIndex(Long pageIndex) {
		m_pageIndex = pageIndex;
	}

	public String getSort() {
		return m_sort;
	}

	public void setSort(String sort) {
		m_sort = sort;
	}

	public String getFilter() {
		return m_filter;
	}

	public void setFilter(String filter) {
		m_filter = filter;
	}

	public List<TEntity> getEntities() {
		return m_entities;
	}

	public void setEntities(List<TEntity> entities) {
		m_entities = entities;
	}

	public Boolean getEndOfResults() {
		return m_endOfResults;
	}

	public void setEndOfResults(Boolean endOfResults) {
		m_endOfResults = endOfResults;
	}

}
