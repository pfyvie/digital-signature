package com.baloise.confluence.digitalsignature;

import static org.apache.commons.codec.digest.DigestUtils.*;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class Signature implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String key = "";
	private String hash = "";
	private long pageId;
	private String title = "";
	private String body = "";
	private long maxSignatures = -1;
	private Map<String, Date> signatures = new HashMap<String, Date>();
	private Set<String> missingSignatures = new TreeSet<String>();
	private Set<String> notified = new TreeSet<String>();
	
	public Signature() {}
	public Signature(long pageId, String body,String title) {
		this.pageId = pageId;
		this.body = body;
		this.title = title == null ? "" : title;
		hash = sha256Hex(pageId +":"+ title +":" + body);
		key = "signature."+hash;
	}
	public String getHash() {
		if(hash == null) {
			hash = getKey().replace("signature.", "");
		}
		return hash;
	}
	public void setHash(String hash) {
		this.hash = hash;
	}
	public String getKey() {
		return key;
	}
	public String getProtectedKey() {
		return "protected."+getHash();
	}
	public void setKey(String key) {
		this.key = key;
	}
	public long getPageId() {
		return pageId;
	}
	public void setPageId(long pageId) {
		this.pageId = pageId;
	}
	public String getBody() {
		return body;
	}
	public void setBody(String body) {
		this.body = body;
	}
	public Map<String, Date> getSignatures() {
		return signatures;
	}
	public void setSignatures(Map<String, Date> signatures) {
		this.signatures = signatures;
	}
	public Set<String> getMissingSignatures() {
		return missingSignatures;
	}
	public void setMissingSignatures(Set<String> missingSignatures) {
		this.missingSignatures = missingSignatures;
	}
	public long getMaxSignatures() {
		return maxSignatures;
	}
	public void setMaxSignatures(long maxSignatures) {
		this.maxSignatures = maxSignatures;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Set<String> getNotify() {
		return notified;
	}
	public void setNotify(Set<String> notify) {
		this.notified = notify;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Signature other = (Signature) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		return true;
	}
	
	public Signature withNotified(Set<String> notified) {
		this.notified = notified;
		return this;
	}
	
	public Signature withMaxSignatures(long maxSignatures) {
		this.maxSignatures = maxSignatures;
		return this;
	}
	
	public boolean hasSigned(String userName) {
		return signatures.containsKey(userName);
	}
	
	public boolean isPetitionMode() {
		return isPetitionMode(getMissingSignatures());
	}
	
	public static boolean isPetitionMode(Set<String> userGroups) {
		return userGroups != null && userGroups.size() == 1 && userGroups.iterator().next().trim().equals("*");
	}
	public boolean sign(String userName) {
		if(!isMaxSignaturesReached() &&  !isPetitionMode() && !getMissingSignatures().remove(userName)) {
			return false;
		} else {
			getSignatures().put(userName, new Date());
			return true;
		}
	}
	public boolean isMaxSignaturesReached() {
		return maxSignatures > -1 && maxSignatures <= getSignatures().size();
	}
	public boolean isSignatureMissing(String userName) {
		return !isMaxSignaturesReached() && !hasSigned(userName)  && (isPetitionMode() || getMissingSignatures().contains(userName));
	}
	public boolean hasMissingSignatures() {
		return !isMaxSignaturesReached() && (isPetitionMode() || !getMissingSignatures().isEmpty());
	}
}
