package test.extreme.xc;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;

import com.extreme.xc.DBManager;
import com.extreme.xc.OAuthException;
import com.extreme.xc.entity.CreditTeller;
import com.extreme.xc.entity.CreditTellerUser;

public class AuthServer extends com.extreme.xc.AuthServer {

	public AuthServer(DBManager db) {
		super(db);
		// TODO Auto-generated constructor stub
	}

	@Override
	public CreditTellerUser auth(@Context HttpHeaders headers)
			throws OAuthException {

		CreditTellerUser user = new CreditTellerUser();
		user.setId("Tayyab");
		user.setCreditTeller(new CreditTeller(420, "Nahdi", "Pharmacy", "1"));
		return user;
	}
	
	public static class Headers implements HttpHeaders {

		@Override
		public List<String> getRequestHeader(String name) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public String getHeaderString(String name) {
			return MediaType.APPLICATION_JSON;
		}

		@Override
		public MultivaluedMap<String, String> getRequestHeaders() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<MediaType> getAcceptableMediaTypes() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public List<Locale> getAcceptableLanguages() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public MediaType getMediaType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Locale getLanguage() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Map<String, Cookie> getCookies() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Date getDate() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public int getLength() {
			// TODO Auto-generated method stub
			return 0;
		}
		
	}

}
