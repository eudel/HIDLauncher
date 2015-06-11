/**
 * 
 */
package at.hid.hidlauncher;

/**
 * @author dunkler_engel
 *
 */
public class GameProfile {
	private String name, gameName, useVersion;
	private int launcherVisibility, resolutionX, resolutionY;
	private boolean askAssistance;

	public GameProfile() {
		
	}
	
	public GameProfile(String name) {
		setName(name);
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the launcherVisibility
	 */
	public int getLauncherVisibility() {
		return launcherVisibility;
	}

	/**
	 * @param launcherVisibility the launcherVisibility to set
	 */
	public void setLauncherVisibility(int launcherVisibility) {
		this.launcherVisibility = launcherVisibility;
	}

	/**
	 * @return the gameName
	 */
	public String getGameName() {
		return gameName;
	}

	/**
	 * @param gameName the gameName to set
	 */
	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	/**
	 * @return the useVersion
	 */
	public String getUseVersion() {
		return useVersion;
	}

	/**
	 * @param useVersion the useVersion to set
	 */
	public void setUseVersion(String useVersion) {
		this.useVersion = useVersion;
	}

	/**
	 * @return the resolutionX
	 */
	public int getResolutionX() {
		return resolutionX;
	}

	/**
	 * @param resolutionX the resolutionX to set
	 */
	public void setResolutionX(int resolutionX) {
		this.resolutionX = resolutionX;
	}

	/**
	 * @return the resolutionY
	 */
	public int getResolutionY() {
		return resolutionY;
	}

	/**
	 * @param resolutionY the resolutionY to set
	 */
	public void setResolutionY(int resolutionY) {
		this.resolutionY = resolutionY;
	}

	/**
	 * @return the askAssistance
	 */
	public boolean isAskAssistance() {
		return askAssistance;
	}

	/**
	 * @param askAssistance the askAssistance to set
	 */
	public void setAskAssistance(boolean askAssistance) {
		this.askAssistance = askAssistance;
	}

	public GameProfile loadGameProfile(String name) {
		GameProfile profile = new GameProfile();
		for(int i = 0; i < HIDLauncher.profile.getProfiles().size(); i++) {
			if(HIDLauncher.profile.getProfiles().get(i).getName().equals(name)) {
				profile = HIDLauncher.profile.getProfiles().get(i);
			} else {
				profile = HIDLauncher.profile.getProfiles().get(0);
			}
		}
		return profile;
	}
}
