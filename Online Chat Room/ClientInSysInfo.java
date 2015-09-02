
/*
 * attempt times, last active time, start lock time, log out time
 */
public class ClientInSysInfo {

	int  attemp;
	long lastActiveTime;
	long startLockTime;
	long logOutTime;
	
	public ClientInSysInfo( int attemp, long lastActiveTime, long startLockTime, long logOutTime){
		this.attemp = attemp;
		this.lastActiveTime = lastActiveTime;
		this.startLockTime = startLockTime;
		this.logOutTime = logOutTime;
	}
}
