package hep.aida.util;

import hep.aida.IBaseHistogram;
import hep.aida.ICloud1D;
import hep.aida.ICloud2D;
import hep.aida.ICloud3D;
import hep.aida.IHistogram1D;
import hep.aida.IHistogram2D;
import hep.aida.IHistogram3D;
import hep.aida.IHistogramFactory;
import hep.aida.IManagedObject;
import hep.aida.IProfile1D;
import hep.aida.IProfile2D;
import hep.aida.ITree;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 *
 * @author onoprien
 */
public class Aida {
  
// -- Private parts : ----------------------------------------------------------
  
  private final ConcurrentHashMap<String, Future<IBaseHistogram>> histMap = new ConcurrentHashMap<String, Future<IBaseHistogram>>(32, .75f, 4);
  
  private IHistogramFactory histFactory;
  private ITree tree;
  
// -- Construction : -----------------------------------------------------------
  
  public Aida(ITree tree, IHistogramFactory hFactory) {
    this.tree = tree;
    histFactory = hFactory;
  }
  
// -- Creating, fetching, and destroying histograms : --------------------------

  public void destroy(IBaseHistogram ibh) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud1D cloud1D(final String pathAndTitle) {
    Future<IBaseHistogram> f = histMap.get(pathAndTitle);
    if (f == null) {
      Callable<ICloud1D> task = new Callable<ICloud1D>() {
        public ICloud1D call() throws Exception {
          return histFactory.createCloud1D(validatePath(pathAndTitle));
        }
      };
      f = histMap.putIfAbsent(pathAndTitle, f);
    }
    try {
      return (ICloud1D) getFromFuture(f);
    } catch (ClassCastException x) {
      throw new IllegalArgumentException(x);
    }  
  }

  public ICloud1D cloud1D(String path, String title) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud1D cloud1D(String path, String title, int maxEntries) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud1D cloud1D(String path, String title, int maxEntries, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud2D cloud2D(String pathAndTitle) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud2D cloud2D(String path, String title) {
    throw new UnsupportedOperationException("Not supported yet.");
  }
  
  public ICloud2D cloud2D(String path, String title, int maxEntries) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud2D cloud2D(String path, String title, int maxEntries, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud3D cloud3D(String pathAndTitle) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud3D cloud3D(String path, String title) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud3D cloud3D(String path, String title, int maxEntries) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public ICloud3D cloud3D(String path, String title, int maxEntries, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram1D histogram1D(String pathAndTitle, int nBins, double lowerEdge, double upperEdge) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram1D histogram1D(String path, String title, int nBins, double lowerEdge, double upperEdge) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram1D histogram1D(String path, String title, int nBins, double lowerEdge, double upperEdge, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram1D histogram1D(String path, String title, double[] binEdges) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram1D histogram1D(String path, String title, double[] binEdges, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram2D histogram2D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram2D histogram2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram2D histogram2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram2D histogram2D(String path, String title, double[] binEdgesX, double[] binEdgesY) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram2D histogram2D(String path, String title, double[] binEdgesX, double[] binEdgesY, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram3D histogram3D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram3D histogram3D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, int nBinsZ, double lowerEdgeZ, double upperEdgeZ) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram3D histogram3D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, int nBinsZ, double lowerEdgeZ, double upperEdgeZ, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram3D histogram3D(String path, String title, double[] binEdgesX, double[] binEdgesY, double[] binEdgesZ) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IHistogram3D histogram3D(String path, String title, double[] binEdgesX, double[] binEdgesY, double[] binEdgesZ, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String pathAndTitle, int nBins, double lowerEdge, double upperEdge) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String pathAndTitle, int nBins, double lowerEdge, double upperEdge, double lowerValue, double upperValue) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, int nBins, double lowerEdge, double upperEdge) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, int nBins, double lowerEdge, double upperEdge, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, int nBins, double lowerEdge, double upperEdge, double lowerValue, double upperValue) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, int nBins, double lowerEdge, double upperEdge, double lowerValue, double upperValue, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, double[] binEdges) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, double[] binEdges, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, double[] binEdges, double lowerValue, double upperValue) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile1D profile1D(String path, String title, double[] binEdges, double lowerValue, double upperValue, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String pathAndTitle, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, double lowerValue, double upperValue) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, double lowerValue, double upperValue) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, int nBinsX, double lowerEdgeX, double upperEdgeX, int nBinsY, double lowerEdgeY, double upperEdgeY, double lowerValue, double upperValue, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, double[] binEdgesX, double[] binEdgesY) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, double[] binEdgesX, double[] binEdgesY, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, double[] binEdgesX, double[] binEdgesY, double lowerValue, double upperValue) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

  public IProfile2D profile2D(String path, String title, double[] binEdgesX, double[] binEdgesY, double lowerValue, double upperValue, String options) {
    throw new UnsupportedOperationException("Not supported yet.");
  }

// -- Local methods : ----------------------------------------------------------
  
  private IBaseHistogram getFromFuture(Future<IBaseHistogram> future) {
    try {
      return future.get();
    } catch (InterruptedException x) {
      throw new IllegalArgumentException(x);
    } catch (ExecutionException x) {
      Throwable t = x.getCause();
      if (t instanceof RuntimeException) {
        throw (RuntimeException) t;
      } else {
        throw new IllegalArgumentException(t);
      }
    }
  }
  
  private String validatePath(String path) {
    int i = path.lastIndexOf('/');
    if (i == -1) return "/" + path;
    if (path.startsWith("/")) throw new IllegalArgumentException("Specify path relative to the tree root");
    String dirPath = "/" + path.substring(0, i);
    try {
      IManagedObject mo = tree.find(dirPath);
      if (! mo.type().equals("dir")) throw new IllegalArgumentException("Non-directory object conflicts with the specified path");
    } catch (IllegalArgumentException x) {
      tree.mkdirs(dirPath);
    }
    return "/" + path;
  }
  
}
