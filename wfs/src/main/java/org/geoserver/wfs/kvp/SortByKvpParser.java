package org.geoserver.wfs.kvp;

import org.geoserver.ows.NestedKvpParser;
import org.opengis.filter.FilterFactory;
import org.opengis.filter.sort.SortBy;
import org.opengis.filter.sort.SortOrder;

/**
 * Parses kvp of the form 'sortBy=Field1 {A|D},Field2 {A|D}...' into a 
 * list of {@link org.opengis.filter.sort.SortBy}.
 * 
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public class SortByKvpParser extends NestedKvpParser {

	FilterFactory filterFactory;
	
	public SortByKvpParser( FilterFactory filterFactory ) {
		super( "sortBy", SortBy.class );
		this.filterFactory = filterFactory;
	}
	
	/**
	 * Parses a token of the form 'Field1 {A|D}' into an instnace of 
	 * {@link SortBy}.
	 */
	protected Object parseToken(String token) throws Exception {
		String[] nameOrder = token.split( " " );
		String propertyName = nameOrder[0];
		
		SortOrder order = SortOrder.ASCENDING;
		if ( nameOrder.length > 1 ) {
			if ( "D".equalsIgnoreCase( nameOrder[1] ) ) {
				order = SortOrder.DESCENDING;
			}
		}
		
		return filterFactory.sort( propertyName, order );
	}

}
