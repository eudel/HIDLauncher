/**
 * 
 */
package at.hid.hidlauncher;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;

/**
 * @author dunkler_engel
 *
 */
public class Profile {
	private ArrayList<GameProfile> profiles = new ArrayList<GameProfile>();
	private ArrayList<PlayerProfile> authenticationDB = new ArrayList<PlayerProfile>();
	private String clientToken, selectedProfile, selectedUser;
	
	/**
	 * @return the profiles
	 */
	public ArrayList<GameProfile> getProfiles() {
		return profiles;
	}
	/**
	 * @return the authenticationDB
	 */
	public ArrayList<PlayerProfile> getAuthenticationDB() {
		return authenticationDB;
	}
	/**
	 * @return the clientToken
	 */
	public String getClientToken() {
		return clientToken;
	}
	/**
	 * @return the selectedProfile
	 */
	public String getSelectedProfile() {
		return selectedProfile;
	}
	/**
	 * @return the selectedUser
	 */
	public String getSelectedUser() {
		return selectedUser;
	}
	/**
	 * @param profiles2 the profiles to set
	 */
	public void setProfiles(ArrayList<GameProfile> profiles2) {
		this.profiles = profiles2;
	}
	/**
	 * @param authenticationDB the authenticationDB to set
	 */
	public void setAuthenticationDB(ArrayList<PlayerProfile> authenticationDB) {
		this.authenticationDB = authenticationDB;
	}
	/**
	 * @param clientToken the clientToken to set
	 */
	public void setClientToken(String clientToken) {
		this.clientToken = clientToken;
	}
	/**
	 * @param selectedProfile the selectedProfile to set
	 */
	public void setSelectedProfile(String selectedProfile) {
		this.selectedProfile = selectedProfile;
	}
	/**
	 * @param selectedUser the selectedUser to set
	 */
	public void setSelectedUser(String selectedUser) {
		this.selectedUser = selectedUser;
	}

	/**
	 * saves the profile file to disk
	 * @param profile the profile to save
	 */
	public void saveProfile(Profile profile) {
		 Json json = new Json();
		 FileHandle fhSettings = Gdx.files.external(".hidlauncher/launcher_profiles.json");
		 String profileAsText = json.prettyPrint(profile);
		 fhSettings.writeString(profileAsText, false);
	}
	
	/**
	 * loads the profile from disk
	 * @param saveDir name of the save directory
	 * @return the Profile object
	 */
	public Profile loadProfile(String saveDir) {
		Json json = new Json();
		Profile profile = null;
		FileHandle fhSettings = Gdx.files.external(".hidlauncher/launcher_profiles.json");
		try {
			String profileAsText = fhSettings.readString();
			profile = json.fromJson(Profile.class, profileAsText);
		} catch (Exception e) {
			HIDLauncher.error(this.getClass().toString(), "error reading settings file", e);
		}
		return profile;
	}
}
