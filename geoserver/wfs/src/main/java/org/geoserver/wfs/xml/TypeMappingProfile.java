/* Copyright (c) 2001 - 2007 TOPP - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wfs.xml;

import org.geotools.feature.AttributeType;
import org.geotools.feature.type.ProfileImpl;
import org.opengis.feature.type.Name;
import org.opengis.feature.type.Schema;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


/**
 * A special profile of a pariticular {@link Schema} which maintains a unique
 * mapping of java class to xml schema type.
 *
 * @author Justin Deoliveira, The Open Planning Project
 *
 */
public class TypeMappingProfile extends ProfileImpl {
    public TypeMappingProfile(Schema schema, Set profile) {
        super(schema, profile);
    }

    /**
     * Obtains the {@link AttributeType} mapped to a particular class.
     * <p>
     * If an exact match cannot be made, then those types which are supertypes
     * of clazz are examined.
     * </p>
     * @param clazz The class.
     *
     * @return The AttributeType, or <code>null</code> if no atttribute
     * type mapped to <code>clazz</code>
     */
    public AttributeType type(Class clazz) {
        ArrayList assignable = new ArrayList();

        for (Iterator i = values().iterator(); i.hasNext();) {
            AttributeType type = (AttributeType) i.next();

            if (type.getType().isAssignableFrom(clazz)) {
                assignable.add(type);
            }

            if (clazz.equals(type.getType())) {
                return type;
            }
        }

        if (assignable.isEmpty()) {
            return null;
        }

        if (assignable.size() == 1) {
            return (AttributeType) assignable.get(0);
        } else {
            //sort
            Comparator comparator = new Comparator() {
                    public int compare(Object o1, Object o2) {
                        AttributeType a1 = (AttributeType) o1;
                        AttributeType a2 = (AttributeType) o2;

                        if (a1.getType().equals(a2.getType())) {
                            return 0;
                        }

                        if (a1.getType().isAssignableFrom(a2.getType())) {
                            return 1;
                        }

                        return -1;
                    }
                };

            Collections.sort(assignable, comparator);

            if (!assignable.get(0).equals(assignable.get(1))) {
                return (AttributeType) assignable.get(0);
            }
        }

        return null;
    }

    /**
     * Obtains the {@link Name} of the {@link AttributeType} mapped
     * to a particular class.
     *
     * @param clazz The class.
     *
     * @return The Name, or <code>null</code> if no atttribute type mapped
     * to <code>clazz</code>
     */
    public Name name(Class clazz) {
        ArrayList assignable = new ArrayList();

        for (Iterator i = entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            AttributeType type = (AttributeType) entry.getValue();

            if (type.getType().isAssignableFrom(clazz)) {
                assignable.add(entry);
            }

            if (clazz.equals(type.getType())) {
                return (Name) entry.getKey();
            }
        }

        if (assignable.isEmpty()) {
            return null;
        }

        if (assignable.size() == 1) {
            return (Name) ((Map.Entry) assignable.get(0)).getKey();
        } else {
            //sort
            Comparator comparator = new Comparator() {
                    public int compare(Object o1, Object o2) {
                        Map.Entry e1 = (Map.Entry) o1;
                        Map.Entry e2 = (Map.Entry) o2;

                        AttributeType a1 = (AttributeType) e1.getValue();
                        AttributeType a2 = (AttributeType) e2.getValue();

                        if (a1.getType().equals(a2.getType())) {
                            return 0;
                        }

                        if (a1.getType().isAssignableFrom(a2.getType())) {
                            return 1;
                        }

                        return -1;
                    }
                };

            Collections.sort(assignable, comparator);

            Map.Entry e1 = (Map.Entry) assignable.get(0);
            Map.Entry e2 = (Map.Entry) assignable.get(1);
            AttributeType a1 = (AttributeType) e1.getValue();
            AttributeType a2 = (AttributeType) e2.getValue();

            if (!a1.getType().equals(a2.getType())) {
                return (Name) e1.getKey();
            }
        }

        return null;
    }
}
