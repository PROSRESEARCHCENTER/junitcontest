package hep.aida.ref.xml;

import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IDataPointSet;
import hep.aida.IFunction;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITuple;
import hep.aida.ref.*;
import java.lang.ref.SoftReference;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Creates a proxy object for AIDA IManagedObject,
 * which can be stored in a tree. This proxy objects 
 * are to be used with AidaXMLStore and "zip" format only.
 *
 * @author The AIDA Team at SLAC
 */
public class AidaObjectProxy {
    
    public static IManagedObject createProxy(AidaXMLStore store, String path, String type) {
        Class proxyClass = getClassForType(type);
        Class[] interfaces = { IManagedObject.class, ObjectProvider.class, proxyClass };
        InvocationHandler handler = new AidaProxyInvocationHandler(store, path);
        Object proxyObject = Proxy.newProxyInstance(AidaObjectProxy.class.getClassLoader(), interfaces, handler);
        //System.out.println("\n*** Proxy :: name="+((IManagedObject) proxyObject).name()+", type="+((IManagedObject) proxyObject).type()+", path="+path);
        return (IManagedObject) proxyObject;
    }
    
    public interface ObjectProvider {
        public IManagedObject getManagedObject();
    }
    
    private static Class getClassForType(String nodeType) {
        Class clazz = IManagedObject.class;
        if (nodeType.toLowerCase().indexOf("icloud1d") >= 0) clazz = ICloud1D.class;
        else if (nodeType.toLowerCase().indexOf("icloud2d") >= 0) clazz = ICloud2D.class;
        else if (nodeType.toLowerCase().indexOf("icloud3d") >= 0) clazz = ICloud3D.class;
        else if (nodeType.toLowerCase().indexOf("idatapointset") >= 0) clazz = IDataPointSet.class;
        else if (nodeType.toLowerCase().indexOf("ifunction") >= 0) clazz = IFunction.class;
        else if (nodeType.toLowerCase().indexOf("ihistogram1d") >= 0) clazz = IHistogram1D.class;
        else if (nodeType.toLowerCase().indexOf("ihistogram2d") >= 0) clazz = IHistogram2D.class;
        else if (nodeType.toLowerCase().indexOf("ihistogram3d") >= 0) clazz = IHistogram3D.class;
        else if (nodeType.toLowerCase().indexOf("iprofile1d") >= 0) clazz = IProfile1D.class;
        else if (nodeType.toLowerCase().indexOf("iprofile2d") >= 0) clazz = IProfile2D.class;
        else if (nodeType.toLowerCase().indexOf("ituple") >= 0) clazz = ITuple.class;
        return clazz;
    }
    
    private static class AidaProxyInvocationHandler implements InvocationHandler {
        private String name;
        private String path;
        private AidaXMLStore store;
        private SoftReference backend;
        
        AidaProxyInvocationHandler(AidaXMLStore store, String path) {
            this.path = path;
            this.store = store;
            String tmpPath = path;
            int index = tmpPath.lastIndexOf(".");
            if (index >= 0) {
                tmpPath = tmpPath.substring(0, index);
            }
            this.name = AidaUtils.parseName(tmpPath);
        }
        
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            int nArgs = args == null ? 0 : args.length;
            if (nArgs ==0) {
                if ("name"    .equals(methodName)) return name;
                if ("hashCode".equals(methodName)) return new Integer(path.hashCode());
                if ("toString".equals(methodName)) return name;
                if ("type"    .equals(methodName)) return ManagedObject.typeForClass(proxy.getClass());
            } else if (nArgs == 1) {
                if ("equals"  .equals(methodName)) return Boolean.valueOf(proxy == args[0]);
            }
            
            if (backend == null || backend.get() == null) {
                //System.out.println("Conversion of "+name+" caused by call to "+methodName);
                //RuntimeException e = new RuntimeException();
                //e.printStackTrace();
                IManagedObject mo = store.readManagedObject(path);
                backend = new SoftReference(mo);
            }
            if ("getManagedObject".equals(methodName)) return (IManagedObject) backend.get();

            return method.invoke((IManagedObject) backend.get(), args);
        }
    }
}