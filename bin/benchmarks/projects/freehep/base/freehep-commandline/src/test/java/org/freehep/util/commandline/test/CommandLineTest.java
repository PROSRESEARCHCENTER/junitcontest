// Copyright 2000, CERN, Geneva, Switzerland.
package org.freehep.util.commandline.test;

import org.freehep.util.commandline.CommandLine;
import org.freehep.util.commandline.CommandLineException;
 

/**
 * Test class to the test the CommandLine class in its different forms
 *
 *
 * @author Mark Donszelmann
 * @version $Id: CommandLineTest.java 8584 2006-08-10 23:06:37Z duns $
 */ 
public class CommandLineTest {
    
            
    public static void main(String[] args) {
        String[][] test = {{"-version"},
                           {"--version"},
                           {"-verbose","parameter1","parameter2","parameter3"},
                           {"-x-y","parameter1","parameter2","parameter3"},
                           {"-cp","classpath","parameter1","parameter2","parameter3","optional1","optional2"},
                           {"-verbose:gc","parameter1","parameter2","parameter3","optional1"},
                           {"-startup=now","classpath","parameter1","parameter2","parameter3"},
                           {"-unknownflag","parameter1","parameter2","parameter3"},                           
                           {"-unknownoption","somevalue","parameter1","parameter2","parameter3"},                           
                           {"-help"},
                           {"-h"},
                           {"-verbose","too","few parameters"},
                           {"-cp"},
                           {"-Iincludedir1", "par1", "par2", "par3"},
                           {"-Iincludedir1", "-Iincludedir2", "par1", "par2", "par3"},
                           {"-Dvar1=value1","-Dvar2=value2","classpath","parameter1","parameter2","parameter3"},
//                           {"\"double 'and single' quoted\""},
//                           {"-path=D:\\duns\\wrk\\wired\\"},
//                           {"-path=\"D:\\duns\\wrk\\wired\\\""},
                };
                
        String cmd = "Fake Java Virtual Machine (version 0.9)";
        for (int i=0; i<test.length; i++) {
  		    CommandLine cl = new CommandLine("java", cmd, 3);
  		    
  		    cl.addOption("classpath","cp", "path", "set search path for application classes");
            cl.addBailOutOption("help", "h", "show help on command");
            cl.addOption("notSetFlag", "nsf", "never used");
            cl.addOption("notSetOption", "nso", "dummy", "not used");
            cl.addOption("notSetQualifiedFlag", null, new String[] {"dummy"},"not used");
            cl.addOption("startup", "st", "startup time", "specify startup time");
            cl.addOption("verbose", "v", new String[] {"class","gc","jni"}, "enable verbose output");
            cl.addBailOutOption("version", null, "print product version");
            cl.addOption("x", null, "option x");
            cl.addOption("y", null, "option y");
            cl.addMultiOption("I", "includedir", "add includedir to search path");
            cl.addMultiOption("D", "key=value", "define a property");
            
            cl.addParameter("class1", "first classfile");
            cl.addParameter("class2", "second classfile");
            cl.addParameter("class3", "third classfile");
            cl.addParameter("optionalfile", "optional classfile");
            cl.addParameter("args...", "further arguments");

            try {  		                   
                for (int j=0; j<test[i].length; j++) {
                    System.out.print(test[i][j]+" ");
                }
                System.out.println();
                
  		        if (!cl.parse(test[i])) {
                    if (cl.hasOption("help")) {
                        System.out.println(cl.getHelp());
                        // System.exit(1);
                    } else if (cl.hasOption("version")) {
                        System.out.println(cmd);    
                        // System.exit(1);
                    } else {
                        System.out.println("Command Bailed Out: "+cl);
                    }
                } else {
                    System.out.println("Command OK: "+cl);
                }        
            } catch (CommandLineException cle) {
                System.out.println(cle);
                System.out.println(cl);
            }
            System.out.println("+++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++");
  		}
    }        
    
}
