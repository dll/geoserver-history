/**
 * 
 */
package org.geoserver.ows.http;

public class Message {
	
	public String message;
	
	public Message( String message ) {
		this.message = message;
	}
	
	public Message getMessage() {
		return this;
	}
	
	public boolean equals(Object obj) {
		if (obj instanceof Message) {
			Message other = (Message)obj;
			if (message == null)
				return other.message == null;
			
			return message.equals( other.message );
		}
		
		return false;
	}
	
	public int hashCode() {
		return message != null ? message.hashCode() : 0;
	}
	
	
}