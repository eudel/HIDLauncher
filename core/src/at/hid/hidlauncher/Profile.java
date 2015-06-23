/**
 * 
 */
package at.hid.hidlauncher;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

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
	public boolean saveProfile() {
		FileHandle fhSettings = null;
		if (Gdx.files.isExternalStorageAvailable()) {
			fhSettings = Gdx.files.external(".hidlauncher/launcher_profiles.json");
		} else {
			fhSettings = Gdx.files.local(".hidlauncher/launcher_profiles.json");
		}
		JSONObject json = new JSONObject();
		JSONArray profiles = new JSONArray();
		JSONArray authenticationDB = new JSONArray();
		try {
			for (int i = 0; i < this.profiles.size(); i++) {
				JSONObject profile = new JSONObject();
				profile.put("name", this.profiles.get(i).getName());
				profile.put("gameName", this.profiles.get(i).getGameName());
				if (this.profiles.get(i).getLauncherVisibility() != 0)
					profile.put("launcherVisibility", this.profiles.get(i).getLauncherVisibility());
				
				if (this.profiles.get(i).getResolutionX() != 0) {
					profile.put("resolutionX", this.profiles.get(i).getResolutionX());
					profile.put("resolutionY", this.profiles.get(i).getResolutionY());
				}
				profile.put("useVersion", this.profiles.get(i).getUseVersion());
				
				profiles.put(profile);
			}
			json.put("profiles", profiles);
			
			for (int i = 0; i < this.authenticationDB.size(); i++) {
				JSONObject authenticatedUser = new JSONObject();
				authenticatedUser.put("username", this.authenticationDB.get(i).getUsername());
				authenticatedUser.put("accessToken", this.authenticationDB.get(i).getAccessToken());
				authenticatedUser.put("uuid", this.authenticationDB.get(i).getUuid());
				authenticatedUser.put("displayName", this.authenticationDB.get(i).getDisplayName());
				
				authenticationDB.put(authenticatedUser);
			}
			json.put("authenticationDB", authenticationDB);
			
			json.put("clientToken", getClientToken());
			json.put("selectedProfile", getSelectedProfile());
			json.put("selectedUser", getSelectedUser());
			String profileAsText = json.toString();
			
			profileAsText = profileAsText.replaceAll("\\{", "{\n");
			profileAsText = profileAsText.replaceAll(",", ",\n");
			profileAsText = profileAsText.replaceAll("\\[", "\n[\n");
			profileAsText = profileAsText.replaceAll("\\}", "}\n");
			profileAsText = profileAsText.replaceAll("\"\\}", "\"\n}");
			profileAsText = profileAsText.replaceAll("\\]", "]\n");
			
			fhSettings.writeString(profileAsText, false, "UTF-8");
		} catch(Exception e) {
			HIDLauncher.error(this.getClass().toString(), "error creating PlayerProfile JSONObject", e);
			return false;
		}
		return true;
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
