/*
 * $Id: ContactDao.java 3482 2005-12-23 23:04:02Z ivaynberg $
 * $Revision: 3482 $
 * $Date: 2005-12-24 00:04:02 +0100 (Sat, 24 Dec 2005) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package wicket.spring.common;

import java.util.Iterator;

/**
 * interface for retrieving contacts from a database
 * 
 * @author Igor Vaynberg (ivaynberg)
 * 
 */
public interface ContactDao {
	/**
	 * @return total number of contacts available
	 */
	int count();

	/**
	 * @param qp
	 *            sorting and paging info
	 * @return iterator over contacts
	 */
	Iterator find(QueryParam qp);

	/**
	 * @param id
	 *            contact id
	 * @return contact with matching id
	 */
	Contact get(long id);
}
