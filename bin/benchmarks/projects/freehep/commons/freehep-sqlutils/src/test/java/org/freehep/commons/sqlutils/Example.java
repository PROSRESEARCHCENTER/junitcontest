
package org.freehep.commons.sqlutils;

import org.freehep.commons.sqlutils.Table;
import org.freehep.commons.sqlutils.Column;
import org.freehep.commons.sqlutils.Select;
import org.freehep.commons.sqlutils.Param;
import java.sql.SQLException;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.freehep.commons.sqlutils.Op.or;
import org.freehep.commons.sqlutils.format.SQLFormatter;
import org.freehep.commons.sqlutils.interfaces.Schema;
/**
 *
 * @author bvan
 */
public class Example {

    public static class ProcessInstance_t extends Table {
        @Schema(name="ProcessInstance") public Column pk;
        @Schema(name="Process") public Column process;
        @Schema(name="Stream") public Column stream;
        @Schema(name="ProcessingStatus") public Column status;
        @Schema(name="IsLatest") public Column latest;

        public ProcessInstance_t() {
            super("ProcessInstance");
        }
    };
    
    public static class ProcessInstanceExt_t extends ProcessInstance_t {
        @Schema(name="CreateDate") public Column createDate;
        @Schema(name="StartDate") public Column startDate;
        @Schema(name="EndDate") public Column endDate;

        public ProcessInstanceExt_t() {
            super();
        }
    };
    
    public static class Stream_t extends Table {
        @Schema(name="Stream") public Column pk;
        @Schema(name="ParentStream") public Column parent;
        @Schema(name="Task") public Column task;
        @Schema(name="StreamID") public Column id;
        @Schema(name="StreamStatus") public Column status;
        @Schema(name="IsLatest") public Column latest;

        public Stream_t() {
            super("Stream");
        }
    };
    
    public Example(){
        
        Stream_t stream = new Stream_t();
        ProcessInstance_t pi = new ProcessInstance_t();
        
        stream.as("s");
        
        Param<Long> pStream = new Param("parent");
        
        Select pselect = new Select( )
                .from( stream )
                .where( 
                        or( stream.parent.eq( pStream ),
                            stream.pk.eq( pStream )
                        )
                );
        
        pselect.selection( stream.columns() );
        
        pselect.dump();
        

        Select piselect = new Select( pi.pk,stream.id ).from(pi)
                .join(stream, pi.stream.eq(stream.pk))
                .where(stream.status.eq("RUNNING"));
        
        
        new ProcessInstanceExt_t().select("Stream","StreamStatus").dump();

        try {
            SQLFormatter.getDefault().setDebug( true );
            pselect.dump();
            pselect.prepareAndBind(null);
            
            pStream.setValue( 16841566L );
            pselect.dump();
            pselect.prepareAndBind(null);
            SQLFormatter.getDefault().setDebug( false );

        } catch (SQLException ex) {
            Logger.getLogger(Example.class.getName()).log(Level.SEVERE, null, ex);
        
        }
        
    }
    
    public static void main(String[] args) {
        new Example();
    }
    
}
