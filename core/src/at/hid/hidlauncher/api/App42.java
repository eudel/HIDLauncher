/**
 * 
 */
package at.hid.hidlauncher.api;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

/**
 * @author dunkler_engel
 *
 */
public interface App42 {
	public void initialize(String apiKey, String secretKey);
	public void buildUserService();
	public void buildStorageService();
	public void getUser(String username);
	public void getUserByMail(String mail);
	public void createUser(String uName, String pwd, String emailAddress);
	public void setSessionId(String sid);
	public void userServiceSetOtherMetaHeaders(HashMap<String, String> otherMetaHeaders);
	public boolean userServiceAuthenticate(String uName, String pwd);
	public void userServiceLogout(String sessionId);
	public void userServiceGetAllUsers();
	public int userServiceGetUserCount();
	public void userServiceCreateOrUpdateProfile();
	public String userGetSessionId();
	public String userGetUserName();
	public String userGetEmail();
	public String userGetFirstName();
	public void userSetFirstName(String displayName);
	public String userlistGetUserName(int i);
	public String userlistGetUserMail(int i);
	public String userlistGetUserDisplayName(int i);
	public void storageServiceFindDocumentByKeyValue(String dbName, String collectionName, String key, String value);
	public void storageServiceDeleteDocumentsByKeyValue(String dbName, String collectionName, String key, String value);
	public void storageServiceFindAllDocuments(String dbName, String collectionName);
	public void storageGetJsonDocList();
	public ArrayList<String> storageGetSaveValues(String key);
	public void storageServiceInsertJSONDocument(String dbName, String collectionName, JSONObject json);
	public void storageServiceUpdateDocumentByKeyValue(String dbName, String collectionName, String key, String value, JSONObject newJsonDoc);
	public String storageGetJsonDoc(int i);
	public void serviceAPITts();
	public void buildUserServiceTts();
	public void userServiceTtsGetAllUsers();
	public boolean userServiceTtsIsUserCreated();
	public void userServiceTtsCreateUser(String username, String pwd, String email);
	public void userServiceTtsAuthenticate(String username, String pwd);
	public void userServiceTtsSetSessionId(String sid);
	public void userServiceTtsCreateOrUpdateProfile();
	public String userServiceTtsGetSessionId();
	public void userServiceTtsGetUser(String uName);
	public void serviceAPIHvr();
	public void buildUserServiceHvr();
	public void userServiceHvrGetAllUsers();
	public boolean userServiceHvrIsUserCreated();
	public void userServiceHvrCreateUser(String username, String pwd, String email);
	public void userServiceHvrAuthenticate(String username, String pwd);
	public void userServiceHvrSetSessionId(String sid);
	public void userServiceHvrCreateOrUpdateProfile();
	public String userServiceHvrGetSessionId();
	public void userServiceHvrGetUser(String uName);
}
