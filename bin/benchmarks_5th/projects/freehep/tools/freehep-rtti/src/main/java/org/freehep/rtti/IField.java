// Copyright 2000-2005, FreeHEP.
package org.freehep.rtti;

import java.util.*;

/**
 * Defines a field in the RTTI.
 *
 * Fields can be changed, but must then consist of one type, and all be const or not.
 *
 * @author Mark Donszelmann
 * @version $Id: IField.java 8584 2006-08-10 23:06:37Z duns $
 */
public class IField {
    private INamedType namedType;
    private Vector comments;
    private IField next;

    IField(INamedType namedType, Vector comments) {
        this.namedType = namedType;
        this.comments = comments;
        this.next = null;
    }

    /**
     * Returns the list of comments decribing this method.
     *
     *
     * @return list of comments
     */
    public String[] getComments(String language) {
        return RTTI.getComments(comments, language);
    }

    /**
     * Returns the named type.
     *
     * @return namedType
     */
    public INamedType getNamedType() {
        return namedType;
    }

    /**
     * Returns the next field, if the definition was a list of fields.
     *
     *
     * @return next field
     */
    public IField getNext() {
        return next;
    }

    /**
     * chains the next field to this field (appends to the end of the list)
     *
     * @param next next field
     */
    public void setNext(IField next) {
        this.next = next;
    }

    /**
     * Returns a string representation of this field (list).
     *
     *
     * @return string representatoon of this field (list)
     */
    public String toString() {
        StringBuffer s = new StringBuffer();

        String[] comments = getComments(null);

        for (int i = 0; i < comments.length; i++) {
            s.append(comments[i]);
        }
        s.append("\n");

        s.append("    ");
        s.append("public ");
        s.append(getNamedType().getType());
        s.append(" ");

        IField field = this;
        while (field != null) {
            INamedType namedType = field.getNamedType();
            s.append(namedType.getName());
            if (namedType.getInit() != null) {
                s.append(" = ");
                s.append(namedType.getInit());
            }

            field = field.getNext();
            if (field != null) {
                s.append(", ");
            }
        }
        s.append(";");
        return s.toString();
    }
}

