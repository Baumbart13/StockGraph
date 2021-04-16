package at.htlAnich.tools.database;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database {
	protected Connection mConnection = null;
	protected String mHostname = "";
	protected String mUser = "";
	protected String mPassword = "";
	protected String mDatabase = "";

	public Database(String hostname, String user, String password, String database){
		this.mHostname = hostname;
		this.mUser = user;
		this.mPassword = password;
		this.mDatabase = database;
	}

	public abstract void connect() throws SQLException;
	public abstract void disconnect() throws SQLException;
	public abstract void createDatabase(String database) throws SQLException;

	@Override
	public String toString(){
		return String.format("Hostname:%s;User:%s;Password:%s;Database:%s",
				mHostname, mUser, mPassword, mDatabase);
	}

	@Override
	public abstract Database clone();

	public String getHostname() {
		return mHostname;
	}

	public String getUser() {
		return mUser;
	}

	public String getDatabase() {
		return mDatabase;
	}
}
