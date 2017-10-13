package org.freehep.conditions.util.cache;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * An entity resolver which caches schemas locally. This makes it possible to
 * run the tests without a network connection, provided the cache has been
 * previously seeded.
 * @author tonyj
 * @version $Id: CachingEntityResolver.java,v 1.1.1.1 2010/01/25 22:23:07 jeremy Exp $
 */
public class CachingEntityResolver implements EntityResolver {
    private FileCache fileCache;

    public CachingEntityResolver() throws IOException {
        fileCache = new FileCache();
    }

    public CachingEntityResolver(File cacheDirectory) throws IOException {
        fileCache = new FileCache(cacheDirectory);
    }

    public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
        File file = fileCache.getCachedFile(new URL(systemId));
        InputSource result = new InputSource(new FileInputStream(file));
        result.setSystemId(systemId);
        return result;
    }
}