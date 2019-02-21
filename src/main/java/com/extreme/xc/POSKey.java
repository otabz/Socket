package com.extreme.xc;

class POSKey {

	private String clientID;
	private String posID;

	public POSKey(String clientID, String posID) {
		this.clientID = clientID;
		this.posID = posID;
	}

	@Override
	public int hashCode() {
		return (this.clientID + posID).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof POSKey)) {
			return false;
		}
		POSKey other = ((POSKey) obj);
		if ((other.clientID == null) || (other.posID == null)) {
			return false;
		}
		if (other.clientID != this.clientID) {
			return false;
		}
		if (other.posID != this.posID) {
			return false;
		}
		return true;
	}
}
