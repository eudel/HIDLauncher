/**
 * 
 */
package at.hid.hidlauncher;

/**
 * @author dunkler_engel
 *
 */
public class PlayerProfile {
	private String username, accessToken, uuid, displayName;

	public PlayerProfile() {
		
	}
	
	public PlayerProfile(String username, String accessToken, String uuid, String displayName) {
		setUsername(username);
		setAccessToken(accessToken);
		setUuid(uuid);
		setDisplayName(displayName);
	}
	
	/**
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * @return the accessToken
	 */
	public String getAccessToken() {
		return accessToken;
	}

	/**
	 * @return the uuid
	 */
	public String getUuid() {
		return uuid;
	}

	/**
	 * @return the displayName
	 */
	public String getDisplayName() {
		return displayName;
	}

	/**
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * @param accessToken the accessToken to set
	 */
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	/**
	 * @param uuid the uuid to set
	 */
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	/**
	 * @param displayName the displayName to set
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	public PlayerProfile loadPlayerProfile(String uuid) {
		PlayerProfile profile = new PlayerProfile();
		for(int i = 0; i < HIDLauncher.profile.getAuthenticationDB().size(); i++) {
			if(HIDLauncher.profile.getAuthenticationDB().get(i).getUuid().equals(uuid)) {
				profile = HIDLauncher.profile.getAuthenticationDB().get(i);
			} else {
				profile = HIDLauncher.profile.getAuthenticationDB().get(0);
			}
		}
		return profile;
	}
	
	
}
