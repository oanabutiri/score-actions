/*
 * Created on Jan 30, 2006
 *
 */
package com.iconclude.dharma.commons.security;

import com.iconclude.dharma.commons.util.Dharma;
import com.iconclude.dharma.commons.util.IEntityResolvable;
import com.iconclude.dharma.commons.util.UUIDUtil;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;
import java.util.UUID;

/**
 * @author octavian
 */
//@DharmaClass(registeredName = "securityToken") //$NON-NLS-1$
public final class DharmaSecurityToken implements ISecurityToken, IEntityResolvable, Serializable { //IDharmaXMLSerializable

    private static final long serialVersionUID = 2748040957100218321L;

    private transient IEncryptionService _encryptionServ;
    private transient UUID _SID;
    private transient HashMap<String, byte[]> _valuesStorage = new HashMap<String, byte[]>();


    public DharmaSecurityToken() {
        _SID = UUIDUtil.getNewUUID();
        this._valuesStorage = new HashMap<String, byte[]>();
        this._encryptionServ = EncryptorFactory.getDefaultEncryptionServ();
    }


    public DharmaSecurityToken(String uuid, IEncryptionService es) {
        this._encryptionServ = es;
        this._valuesStorage = new HashMap<String, byte[]>();
        _SID = UUIDUtil.getNewUUID();
    }

    public long getSerialVersionUID() {
        return serialVersionUID;
    }

    public void setEncryptionService(IEncryptionService es) {
        _encryptionServ = es;
    }

    public final void addSecurityValue(String key, byte[] value, boolean encrypt) {
        if (null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.addSecurityValueNullKeyError")); //$NON-NLS-1$
        if (null == value) {
            _valuesStorage.put(key, value);
            return;
        }
        if (encrypt == true && null == _encryptionServ)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.AddNullEncryptionServiceError")); //$NON-NLS-1$

        byte[] val = (encrypt ? _encryptionServ.encrypt(value, 0, value.length) : value);
        // TODO this is wastefull, but I think there is API to figure out how long the encrypted value
        // would be; then allocate a buffer one byte bigger and avoid the extra copy. FIX IT!!!
        byte[] extraStupidBuffer = new byte[val.length + 1];
        // store the encyption byte on the first position
        extraStupidBuffer[0] = (encrypt == true ? (byte) 0x1 : (byte) 0x0);
        System.arraycopy(val, 0, extraStupidBuffer, 1, val.length);
        _valuesStorage.put(key, extraStupidBuffer);
    }

    public final void deleteSecurityValue(String key) {
        if (null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.deleteSecurityKeyNullKeyError")); //$NON-NLS-1$
        _valuesStorage.remove(key);
    }

    public final byte[] getSecurityValue(String key) {
        if (null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.getSecurityValueNullKeyValue")); //$NON-NLS-1$
        byte[] value = (byte[]) _valuesStorage.get(key);
        if (null == value || value.length == 0)
            return null;
        // the encryption byte is stored on the first position
        boolean decrypt = (value[0] == 0x1 ? true : false);
        if (decrypt == true && null == _encryptionServ)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.GetNullEncryptionServiceError")); //$NON-NLS-1$

        if (decrypt) {
            return _encryptionServ.decrypt(value, 1, value.length - 1);
        } else {
            byte[] extraStupidBuffer = new byte[value.length - 1];
            System.arraycopy(value, 1, extraStupidBuffer, 0, value.length - 1);
            return extraStupidBuffer;
        }
    }

    public final void addRawValue(String key, byte[] value) {
        if (null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.addRawValueNullKeyError")); //$NON-NLS-1$
        _valuesStorage.put(key, value);
    }

    public final byte[] getRawValue(String key) {
        if (null == key)
            throw new IllegalArgumentException(Dharma.msg("Dharma.security.DharmaSecurityToken.getRawValueNullKeyError")); //$NON-NLS-1$
        return _valuesStorage.get(key);
    }

    public final Set<String> getSecurityValueKeys() {
        return Collections.unmodifiableSet(this._valuesStorage.keySet());
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!(obj instanceof DharmaSecurityToken))
            return false;

        DharmaSecurityToken other = (DharmaSecurityToken) obj;
        return other.getSID().equals(this.getSID());
    }

    public int hashCode() {
        return this.getSID().hashCode();
    }

    public int compareTo(Object obj) {
        if (obj == this)
            return 0;
        if (!(obj instanceof DharmaSecurityToken))
            throw new ClassCastException();
        DharmaSecurityToken other = (DharmaSecurityToken) obj;
        return other.getSID().compareTo(this.getSID());
    }

    public UUID getSID() {
        return _SID;
    }

    public void setSID(UUID sid) {
        this._SID = sid;
    }

//    /**
//     * Dharma XML support **
//     */
//    public void readObject(com.iconclude.dharma.commons.repo.IDharmaSerializer serializer, org.dom4j.Element element) {
//        try {
//            Element store = element.element("_store_"); //$NON-NLS-1$
//            String source = store.getText();
//            byte[] data = Base64.decode(source);
//            ByteArrayInputStream bais = new ByteArrayInputStream(data);
//            SecurityTokenHelper.readIn(this, bais);
//            return;
//        } catch (IOException ioe) {
//            throw new DharmaException(Dharma.msg("Dharma.security.DharmaSecurityToken.ReadSecurityTokenError"), ioe); //$NON-NLS-1$
//        }
//    }
//
//    ;
//
//    @SuppressWarnings("unchecked") //$NON-NLS-1$
//    public void writeObject(IDharmaSerializer serializer, Element parentElement) {
//        Iterator<Element> pci = parentElement.elementIterator("node"); //$NON-NLS-1$
//        while (pci.hasNext()) {
//            Element pe = pci.next();
//            if ("securityToken".equals(pe.attributeValue("type"))) { //$NON-NLS-1$ //$NON-NLS-2$
//                try {
//                    Element e = pe.addElement("_store_"); //$NON-NLS-1$
//                    ByteArrayOutputStream baos = new ByteArrayOutputStream(1024); // 1K by default
//                    SecurityTokenHelper.writeOut(this, baos);
//                    byte[] data = baos.toByteArray();
//                    String enc = Base64.encode(data, 0, data.length);
//                    e.addCDATA(enc);
//                    return;
//                } catch (IOException ioe) {
//                    throw new DharmaException(Dharma.msg("Dharma.security.DharmaSecurityToken.WriteSecurityTokenError"), ioe); //$NON-NLS-1$
//                }
//            }
//        }
//        throw new DharmaException(Dharma.msg("Dharma.security.DharmaSecurityToken.WriteSecurityTokenNoParentNodeError")); //$NON-NLS-1$
//    }


    public String toString() {
        return "******"; //$NON-NLS-1$
    }

}
