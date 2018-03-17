package sfms.db;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

public class Database {

	public final static Database INSTANCE = new Database();

	private ConcurrentMap<Long, DbSpaceship> m_spaceships = new ConcurrentHashMap<Long, DbSpaceship>();
	private ConcurrentMap<Long, DbCrewMember> m_crewMembers = new ConcurrentHashMap<Long, DbCrewMember>();
	private ConcurrentMap<Long, DbCrewAssignment> m_crewAssignments = new ConcurrentHashMap<Long, DbCrewAssignment>();

	private AtomicLong m_id = new AtomicLong();

	private Database() {
		DatabaseGenerator generator = new DatabaseGenerator();
		generator.populate(this);
	}

	public ConcurrentMap<Long, DbSpaceship> getSpaceships() {
		return m_spaceships;
	}

	public ConcurrentMap<Long, DbCrewMember> getCrewMembers() {
		return m_crewMembers;
	}

	public ConcurrentMap<Long, DbCrewAssignment> getCrewAssignments() {
		return m_crewAssignments;
	}

	public long getNextId() {
		return m_id.getAndIncrement();
	}
}
