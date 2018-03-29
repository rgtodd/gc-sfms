package sfms.web.models;

public class PagingModel {

	private Integer m_currentPageNumber;
	private Integer m_nextPageNumber;
	private String m_nextBookmark;
	private Boolean m_endOfResults;

	public Integer getCurrentPageNumber() {
		return m_currentPageNumber;
	}

	public void setCurrentPageNumber(Integer currentPageNumber) {
		m_currentPageNumber = currentPageNumber;
	}

	public Integer getNextPageNumber() {
		return m_nextPageNumber;
	}

	public void setNextPageNumber(Integer nextPageNumber) {
		m_nextPageNumber = nextPageNumber;
	}

	public String getNextBookmark() {
		return m_nextBookmark;
	}

	public void setNextBookmark(String nextBookmark) {
		m_nextBookmark = nextBookmark;
	}

	public Boolean getEndOfResults() {
		return m_endOfResults;
	}

	public void setEndOfResults(Boolean endOfResults) {
		m_endOfResults = endOfResults;
	}

}
