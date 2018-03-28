package sfms.db;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Database {

	public final static Database INSTANCE = new Database();

	private ConcurrentMap<String, DbSpaceship> m_spaceships = new ConcurrentHashMap<String, DbSpaceship>();
	private ConcurrentMap<String, DbCrewMember> m_crewMembers = new ConcurrentHashMap<String, DbCrewMember>();
	private ConcurrentMap<String, DbCrewAssignment> m_crewAssignments = new ConcurrentHashMap<String, DbCrewAssignment>();

	private AtomicLong m_id = new AtomicLong();

	private Database() {
		DatabaseGenerator generator = new DatabaseGenerator();
		generator.populate(this);
	}

	public ConcurrentMap<String, DbSpaceship> getSpaceships() {
		return m_spaceships;
	}

	public ConcurrentMap<String, DbCrewMember> getCrewMembers() {
		return m_crewMembers;
	}

	public ConcurrentMap<String, DbCrewAssignment> getCrewAssignments() {
		return m_crewAssignments;
	}

	public String getNextId() {
		return String.valueOf(m_id.getAndIncrement());
	}
}
