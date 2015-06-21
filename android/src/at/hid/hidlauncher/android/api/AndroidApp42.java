package at.hid.hidlauncher.android.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.shephertz.app42.paas.sdk.android.App42API;
import com.shephertz.app42.paas.sdk.android.storage.Storage;
import com.shephertz.app42.paas.sdk.android.storage.StorageService;
import com.shephertz.app42.paas.sdk.android.user.User;
import com.shephertz.app42.paas.sdk.android.user.UserService;
import com.shephertz.app42.paas.sdk.java.App42Exception;

import at.hid.hidlauncher.HIDLauncher;
import at.hid.hidlauncher.api.App42;

public class AndroidApp42 implements App42 {
	private UserService userService;
	private StorageService storageService;
	private ArrayList<User> userlist = null;
	private User user;
	private Storage storage = null;
	ArrayList<Storage.JSONDocument> jsonDocList = null;

	public AndroidApp42() {
	}

	@Override
	public void initialize(String apiKey, String secretKey) {
		App42API.initialize(App42API.appContext, apiKey, secretKey);

	}

	@Override
	public void buildUserService() {
		userService = App42API.buildUserService();
	}

	@Override
	public void buildStorageService() {
		storageService = App42API.buildStorageService();
	}

	@Override
	public void getUser(String username) {
		user = userService.getUser(username);
	}
	
	@Override
	public void createUser(String uName, String pwd, String emailAddress) {
		user = userService.createUser(uName, pwd, emailAddress);
	}

	@Override
	public void setSessionId(String sid) {
		user.setSessionId(sid);
		userService.setSessionId(sid);
		storageService.setSessionId(sid);
	}
	
	@Override
	public void userServiceSetOtherMetaHeaders(HashMap<String, String> otherMetaHeaders) {
		userService.setOtherMetaHeaders(otherMetaHeaders);
	}
	
	@Override
	public void userServiceAuthenticate(String uName, String pwd) {
		user = userService.authenticate(uName, pwd);
	}
	
	@Override
	public void userServiceLogout(String sessionId) {
		userService.logout(sessionId);
	}
	
	@Override
	public void userServiceGetAllUsers() {
		try {
			userlist = userService.getAllUsers();
		} catch (App42Exception e) {
			int errorCode = e.getAppErrorCode();
			switch (errorCode) {
				case 2006:

					break;
			}
		}
	}
	
	@Override
	public int userServiceGetUserCount() {
		return userlist.size();
	}
	
	@Override
	public void userServiceCreateOrUpdateProfile() {
		userService.createOrUpdateProfile(user);
	}
	
	@Override
	public String userGetSessionId() {
		return user.getSessionId();
	}
	
	@Override
	public String userGetUserName() {
		return user.getUserName();
	}
	
	@Override
	public String userGetEmail() {
		return user.getEmail();
	}
	
	@Override
	public String userGetFirstName() {
		return user.getProfile().getFirstName();
	}
	
	@Override
	public String userlistGetUserName(int i) {
		return userlist.get(i).getUserName();
	}

	@Override
	public void storageServiceFindDocumentByKeyValue(String dbName, String collectionName, String key, String value) {
		storage = storageService.findDocumentByKeyValue(dbName, collectionName, key, value);
	}

	@Override
	public void storageServiceDeleteDocumentsByKeyValue(String dbName, String collectionName, String key, String value) {
		storageService.deleteDocumentsByKeyValue(dbName, collectionName, key, value);
	}

	@Override
	public void storageServiceFindAllDocuments(String dbName, String collectionName) {
		storage = storageService.findAllDocuments(dbName, collectionName);
	}

	@Override
	public void storageGetJsonDocList() {
		if (storage != null) {
			jsonDocList = storage.getJsonDocList();
		}
	}
	
	@Override
	public ArrayList<String> storageGetSaveValues(String key) {
		ArrayList<String> saveValues = new ArrayList<String>();
		for (int i = 0; i < jsonDocList.size(); i++) {
			JSONObject json;
			try {
				json = new JSONObject(jsonDocList.get(i).getJsonDoc());
				saveValues.add(json.getString(key));
			} catch (JSONException e) {
				HIDLauncher.error(this.getClass().toString(), "error reading JSONObject", e);
			}
		}
		return saveValues;
	}
	
	@Override
	public void storageServiceInsertJSONDocument(String dbName, String collectionName, JSONObject json) {
		storageService.insertJSONDocument(dbName, collectionName, json);
	}
	
	@Override
	public void storageServiceUpdateDocumentByKeyValue(String dbName, String collectionName, String key, String value, JSONObject newJsonDoc) {
		storageService.updateDocumentByKeyValue(dbName, collectionName, key, value, newJsonDoc);
	}

	@Override
	public String storageGetJsonDoc(int i) {
		return storage.getJsonDocList().get(i).getJsonDoc();
	}
	
	public UserService getUserService() {
		return userService;
	}

	public StorageService getStorageService() {
		return storageService;
	}

	public User getUser() {
		return user;
	}

	public Storage getStroage() {
		return storage;
	}
}
