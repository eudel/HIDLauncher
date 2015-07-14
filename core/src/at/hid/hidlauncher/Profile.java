/**
 * 
 */
package at.hid.hidlauncher;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

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
	 * @param profiles the profiles to set
	 */
	public void setProfiles(ArrayList<GameProfile> profiles) {
		this.profiles = profiles;
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
	 * @return the Profile object
	 */
	public void loadProfile() {
		JSONObject jsonProfile = null;
		FileHandle fhSettings = Gdx.files.external(".hidlauncher/launcher_profiles.json");
		try {
			String profileAsText = fhSettings.readString();
			jsonProfile = new JSONObject(profileAsText);
			if (jsonProfile.has("clientToken")) {
				setClientToken(jsonProfile.getString("clientToken"));
			}
			if (jsonProfile.has("selectedUser")) {
				setSelectedUser(jsonProfile.getString("selectedUser"));
			}
			if (jsonProfile.has("selectedProfile")) {
				setSelectedProfile(jsonProfile.getString("selectedProfile"));
			}
			
			if (jsonProfile.has("profiles")) {
				ArrayList<GameProfile> profilesList = new ArrayList<GameProfile>();
				JSONArray jsonArrayProfiles = new JSONArray(jsonProfile.getString("profiles"));
				for (int i = 0; i < jsonArrayProfiles.length(); i++) {
					JSONObject jsonArayProfile = jsonArrayProfiles.getJSONObject(i);
					GameProfile gameProfile = new GameProfile(jsonArayProfile.getString("name"));
					
					if (jsonArayProfile.has("gameName")) {
						gameProfile.setGameName(jsonArayProfile.getString("gameName"));
					}
					if (jsonArayProfile.has("resolutionX")) {
						gameProfile.setResolutionX(jsonArayProfile.getInt("resolutionX"));
						gameProfile.setResolutionY(jsonArayProfile.getInt("resolutionY"));
					}
					if (jsonArayProfile.has("askAssistance")) {
						gameProfile.setAskAssistance(jsonArayProfile.getBoolean("askAssistance"));
					}
					if (jsonArayProfile.has("launcherVisibility")) {
						gameProfile.setLauncherVisibility(jsonArayProfile.getInt("launcherVisibility"));
					}
					if (jsonArayProfile.has("useVersion")) {
						gameProfile.setUseVersion(jsonArayProfile.getString("useVersion"));
					}
					
					profilesList.add(gameProfile);
				}
				setProfiles(profilesList);
			}
			
			if (jsonProfile.has("authenticationDB")) {
				ArrayList<PlayerProfile> authenticationDBList = new ArrayList<PlayerProfile>();
				JSONArray jsonArrayAuthenticationDB = new JSONArray(jsonProfile.getString("authenticationDB"));
				for (int i = 0; i < jsonArrayAuthenticationDB.length(); i++) {
					JSONObject jsonArrayAuthenticationDBEntry = jsonArrayAuthenticationDB.getJSONObject(i);
					PlayerProfile playerProfile = new PlayerProfile();
					
					if (jsonArrayAuthenticationDBEntry.has("uuid")) {
						playerProfile.setUuid(jsonArrayAuthenticationDBEntry.getString("uuid"));
					}
					if (jsonArrayAuthenticationDBEntry.has("displayName")) {
						playerProfile.setDisplayName(jsonArrayAuthenticationDBEntry.getString("displayName"));
					}
					if (jsonArrayAuthenticationDBEntry.has("accessToken")) {
						playerProfile.setAccessToken(jsonArrayAuthenticationDBEntry.getString("accessToken"));
					}
					if (jsonArrayAuthenticationDBEntry.has("username")) {
						playerProfile.setUsername(jsonArrayAuthenticationDBEntry.getString("username"));
					}
					
					authenticationDBList.add(playerProfile);
				}
				setAuthenticationDB(authenticationDBList);
			}
			
		} catch (Exception e) {
			HIDLauncher.error(this.getClass().toString(), "error reading settings file", e);
		}
	}
}
