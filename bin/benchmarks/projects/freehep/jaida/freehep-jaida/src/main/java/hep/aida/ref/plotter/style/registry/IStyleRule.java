package hep.aida.ref.plotter.style.registry;

public interface IStyleRule {

    public static String PATH = "Path";
    public static String OBJECT = "Object";
    public static String OBJECT_TYPE = "ObjectAIDAType";
    public static String OBJECT_INSTANCEOF = "isObjectInstanceOf";
    public static String NULL = "Null";
    public static String ATTRIBUTE = "attribute(\"\")";
    public static String OVERLAY_INDEX = "OverlayIndex";
    public static String OVERLAY_TOTAL = "OverlayTotal";
    public static String REGION_INDEX = "RegionIndex";
    public static String REGION_TOTAL = "RegionTotal";
        
    String getDescription();
    
    // Evaluates the Rule    
    boolean ruleApplies(IPlotterState state);
}
