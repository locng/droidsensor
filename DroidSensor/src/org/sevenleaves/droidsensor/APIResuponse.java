package org.sevenleaves.droidsensor;

public class APIResuponse {

	private String _twitterUser;

	private int _count;

	private String _message;

	public String getTwitterUser() {
		return _twitterUser;
	}

	public void setTwitterUser(String twitterUser) {
		_twitterUser = twitterUser;
	}

	public int getCount() {
		return _count;
	}

	public void setCount(int count) {
		_count = count;
	}

	public String getMessage() {
		return _message;
	}

	public void setMessage(String message) {
		_message = message;
	}

}
