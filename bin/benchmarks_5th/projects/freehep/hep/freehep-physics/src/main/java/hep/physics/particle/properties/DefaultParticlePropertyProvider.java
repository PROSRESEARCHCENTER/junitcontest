package hep.physics.particle.properties;

import java.util.Formatter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Default table of particle properties used by <code>ParticlePropertyProvider</code>.
 * @see ParticlePropertyProvider
 * @author Tony Johnson
 * Corrections to particle codes/properties as per PDG '98: M. Krishnamurthy/R.J.Wilson  8/8/99
 * Updated as a result of discussion between Bob Wilson and Tony Waite.
 * Fixed charges for some SUSY particles
 */

public class DefaultParticlePropertyProvider implements ParticlePropertyProvider
{
   /**
    * Default constructor, can be called from superclasses
    */
   protected DefaultParticlePropertyProvider()
   {
      // Units
      
      double m = 100;
      double cm = 1;
      double mm = 0.1;
      double GeV = 1;
      double MeV = 1./1000;
      double keV = 1./1000000;
      double hbarc = 197.327*MeV*m*1.E-15;
      double ec = 0.00299792;   // GeV/T-m
      
      // TODO: Hopefully soon this whole ugly thing will be replaced by a new XML
      //       file derived directly from the PDG.
      
      // originally chopped directly from arve/gismo/CreateDefault.cxx
                  
      //  name   (anti) id 2J	Q	mass		  width or hbarc/ctau
      //  ----   ------ --  -	-	----		  -------------------
      PART("gluon", "gluon_bar", 	21, 2, 0, 0, 0);
      PART("gamma",			22, 2, 0, 0.);
      PART("e-",  "e+",			11, 1,-1, 511.0*keV);
            
      PART("mu-", "mu+", 		13, 1,-1, 105.658*MeV,  hbarc/(658.65*m)	);
      PART("tau-", "tau+",		15, 1,-1, 1.777*GeV, hbarc/(87.2E-6*m) );
      PART("nu_e", "nu_e_bar",	12, 1, 0, 0.);
      PART("nu_mu", "nu_mu_bar",   	14, 1, 0, 0.);
      PART("nu_tau", "nu_tau_bar", 	16, 1, 0, 0, 0);
      
      PART("d", "d_bar",  1, 1, -1./3., 6.*MeV , 0);
      PART("u", "u_bar",  2, 1,	2./3., 3.25*MeV, 0 );
      PART("s", "s_bar",  3, 1, -1./3., 115.*MeV, 0);
      PART("c", "c_bar",  4, 1,	2./3., 1.4*GeV,  0	);
      PART("b", "b_bar",  5, 1, -1./3., 4.25*GeV, 0);
      PART("t", "t_bar",  6, 1,	2./3., 175.*GeV, 0);
      
      PART("a1+", "a1-", 			20213, 2, 0, 1.230*GeV,  400.*MeV);
      PART("eta", "eta_bar", 			221,   0, 0, 547.45*MeV, 0);
      PART("eta'", "eta'_bar",		331, 0, 0, 957.77*MeV, 0.201*MeV);
      PART("eta(c)(1S)","eta(c)(1S)_bar",	441, 0, 0, 29.798*MeV, 13.2*MeV);
      
      PART("Delta+", "Delta+_bar",		2214,  3, 1, 1.232*GeV,  350.0*MeV);
      PART("Delta-", "Delta-_bar",		1114,  3,-1, 1.232*GeV,  350.0*MeV);
      PART("Deltao", "Deltao_bar",		2114, 3, 0, 1.232*GeV, 120.0*MeV);
      PART("Delta++", "Delta--", 		2224, 3, 2, 1.232*GeV, 120.0*MeV);
      PART("Lambda(c)+", "Lambda(c)-",	4122, 1, 0, 2.285*GeV, hbarc/(61.8E-6*m));
      PART("Lambda(b)o","Lambda(b)o_bar",	5122, 1, 0, 5.6415*GeV, 0);
      
      PART("B*+", "B*-", 			523, 2, 1, 5.325*GeV, 0);
      PART("B*o", "B*o_bar", 			513, 2, 0, 5.325*GeV, 0);
      PART("B(s)o", "B(s)o_bar", 		531,   0, 0, 5.3693*GeV, hbarc/(483.0E-6*m) );
      PART("B(s)*o", "B(s)*o_bar",		533, 2, 0, 5.51*GeV, 0);// double check
      
      PART("Xi-", "Xi+", 			3312, 1, -1, 1.321*GeV, hbarc/(4.91E-3*m));
      PART("Xi(b)-", "Xi(b)+",		5132, 1, -1, 6000, 6000);//double check mass
      PART("Xi(b)*-", "Xi(b)*+", 		5314, 2, -1, 5.81*GeV, 0); //double check width
      PART("Xi(b)'-", "Xi(b)'+", 		5312, 1, -1, 5.77*GeV, 0);//double check width
      PART("Xi(b)o", "Xi(b)o_bar",		5232, 1, 0, 6.0, 0);//mass guess
      PART("Xi(b)*o", "Xi(b)*o_bar", 	5324, 2, 0, 6.0, 0);//mass guess
      PART("Xi(b)'o", "Xi(b)'o_bar", 	5322, 1, 0, 6.0, 0);//mass guess
      PART("Xi(c)+", "Xi(c)-",		4232, 1, 1, 2.465*GeV, 0);
      PART("Xi(c)o", "Xi(c)o_bar",		4132, 1, 0, 2.470*GeV, 0);
      PART("Xi(c)*+", "Xi(c)*-", 		4324, 2, 1, 2.6446*GeV, 0);
      PART("Xi(c)*o", "Xi(c)*o_bar", 	4314, 2, 0, 2.6438*GeV, 0);
      PART("Xi(c)'+", "Xi(c)'-", 		4322, 1, 1, 2.465*GeV, 0);//don't know what these are RJW
      PART("Xi(c)'o", "Xi(c)'o_bar", 	4312, 1, 0, 2.470*GeV, 0);//don't know what these are RJW
      PART("Xi(c0)(1P)","Xi(c0)(1P)_bar",10441, 0, 0, 3.415*GeV, 13.5*MeV);
      PART("Xi(c1)(1P)","Xi(c1)(1P)_bar",20443, 2, 0, 3.5105*GeV, 0.88*MeV);
      PART("Xi(c2)(1P)", "Xi(c)(1P)_bar",445, 4, 0, 3.556*GeV, 2.00*MeV);
      PART("Xi(1530)o", "Xi(1530)o_bar", 3324, 3, 0, 1.532*GeV, 9.1*MeV);
      PART("Sigma(b)-", "Sigma(b)-_bar", 5112, 1, -1, 5.812*GeV, 0);// check width
      PART("Sigma(b)+", "Sigma(b)+_bar", 5222, 1, 1, 5.812*GeV, 0);// check these two
      PART("Sigma(b)*+","Sigma(b)*+_bar",5224, 2, 1, 5.812*GeV, 0);
      PART("Sigma(b)*-","Sigma(b)*-_bar",5114, 2, -1, 5.812*GeV, 0);// check width
      PART("Sigma(b)o", "Sigma(b)o_bar", 5212, 1, 0, 1.193*GeV, 0);
      PART("Sigma(b)*o","Sigma(b)*o_bar",5214, 2, 0, 1.193*GeV,0);
      PART("Sigma(c)+", "Sigma(c)-", 	4212, 1, 1, 2.453*GeV, 0);
      PART("Sigma(c)o", "Sigma(c)o_bar", 4112, 1, 0, 2.452*GeV, 0);
      PART("Sigma(c)*o","Sigma(c)*o_bar",4114, 2, 0, 2.452*GeV, 0);// check width
      PART("Sigma(c)*+", "Sigma(c)*-",	4214, 2, 1, 2.4535*GeV, 0);// check width
      PART("Sigma(c)++", "Sigma(c)--",	4222, 1, 2, 2.453*GeV, 0);
      PART("Sigma(c)*++", "Sigma(c)*--", 4224, 2, 2, 2.455*GeV, 0);
      PART("Sigma(1385)+","Sigma(1385)-",3224,3,1,1.383*GeV,35.8*MeV);
      
      PART("Ko", "Ko_bar",			311,   0, 0, 497.672*MeV, 0);
      PART("K(1410)*o", "K(1410)*o_bar",  100313, 2, 0, 1.414*GeV, 227.0*MeV);
      PART("K(1410)*+", "K(1410)*-", 	100323, 2, 1, 1.414*GeV, 227.0*MeV);
      PART("K(892)*o", "K*o(892)_bar",	313,   2, 0, 896.10*MeV, 50.5*MeV);
      PART("K(892)*+", "K*-(892)",		323,   2, 0, 891.59*MeV, 49.8*MeV);
      PART("K(1270)1o", "K(1270)1o_bar",  10313, 2, 0, 1.273*GeV, 90.0*MeV);
      PART("K1(1270)+", "K1(1270)-", 	10323, 2, 1, 1.273*GeV, 90.0*MeV);
      PART("K2(1430)*o","K2(1430)*o_bar",	315, 4, 0, 1.4254*GeV, 98.4*MeV);
      PART("K2(1430)*+", "K2(1430)*-",	325, 4, 1, 1.4254*GeV, 98.4*MeV);
      
      PART("Omega(b)-", "Omega(b)+", 	5332, 3, -1, 6.0*GeV, 0);//Not measured; guess mass is Omega(c)+(mb-mc)
      PART("Omega(b)*-", "Omega(b)*+",	5334, 4, -1, 6.0*GeV, 0);//
      PART("Omega(c)o", "Omega(c)o_bar", 	4332, 1, 0, 2.704*GeV, 0);
      PART("Omega(c)*o","Omega(c)*o_bar",	4334, 2, 0, 2.704*GeV, 0);
      
      PART("T+", "T-",				611, 0, 1, 175.0*GeV, 0);
      PART("T*+","T*-",				613, 2, 1, 175.14*GeV, 0);
      PART("To", "To_bar",			621, 0, 0, 175.0*GeV, 0);
      PART("T*o", "T*o_bar", 			623, 2, 0, 175.14*GeV, 0);
      
      PART("W+", "W-",				 24, 2, 1, 80.33*GeV, 2.07*GeV);
      PART("J/psi(1S)", "J/psi(1S)", 443, 2, 0, 3.0969*GeV, 87.0*keV);
      
      // SUSY particles from '98 PDG
      
      // Left-handed squarks and sleptons
      
      PART("sdl-", "sdl+",  1000001, 0,-1./3.,  100.*GeV,  0  );
      PART("sul-", "sul+",  1000002, 0,-2./3.,  100.*GeV,  0  );
      PART("ssl-", "ssl+",  1000003, 0,-1./3.,  100.*GeV,  0  );
      PART("scl-", "scl+",  1000004, 0,-2./3.,  100.*GeV,  0  );
      PART("sbl-", "sbl+",  1000005, 0,-1./3.,  100.*GeV,  0  );
      PART("stl-", "stl+",  1000006, 0,-2./3.,  100.*GeV,  0  );
      
      PART("sel-", "sel+",			 1000011, 0, -1,	100.*GeV,  0  );
      PART("snuel", "snuel_bar", 	 1000012, 0,0,	100.*GeV,  0  );
      PART("smul-", "smul+", 		 1000013, 0, -1,	100.*GeV,  0  );
      PART("snumul", "snumul_bar",	 1000014, 0,0,	100.*GeV,  0  );
      PART("staul-", "staul+",		 1000015, 0, -1,	100.*GeV,  0  );
      PART("snutaul", "snutaul_bar",  1000016, 0,0,	100.*GeV,  0  );
      
      // Right-handed squarks and sleptons
      
      PART("sdr-", "sdr+",  2000001, 0,-1./3.,  100.*GeV,  0  );
      PART("sur-", "sur+",  2000002, 0,-2./3.,  100.*GeV,  0  );
      PART("ssr-", "ssr+",  2000003, 0,-1./3.,  100.*GeV,  0  );
      PART("scr-", "scr+",  2000004, 0,-2./3.,  100.*GeV,  0  );
      PART("sbr-", "sbr+",  2000005, 0,-1./3.,  100.*GeV,  0  );
      PART("str-", "str+",  2000006, 0,-2./3.,  100.*GeV,  0  );
      
      PART("ser-", "ser+",  2000011, 0, -1,  100.*GeV,  0	);
      PART("snuer", "snuer_bar",  2000012, 0,0,	100.*GeV,  0  );
      PART("smur-", "smur+",  2000013, 0, -1,	100.*GeV,  0  );
      PART("snumur", "snumur_bar",  2000014, 0,0,  100.*GeV,  0	);
      PART("staur-", "staur+",  2000015, 0, -1,  100.*GeV,  0	);
      PART("snutaur", "snutaur_bar",  2000016, 0,1,	100.*GeV,  0  );
      
      PART("gluino", 	  1000021, 1, 0,  0.);
      
      // charginos
      
      PART("neut10", "neut10_bar", 1000022, 0,0, 100.*GeV, 0. );
      PART("neut20", "neut20_bar", 1000023, 0,0, 100.*GeV, 0 );
      PART("charg1+", "charg1-", 1000024, 0, 1, 100.*GeV, 0 );
      PART("neut30", "neut30_bar", 1000025, 0,0, 100.*GeV, 0 );
      PART("zino2", "zino2_bar", 1000032, 0, 0, 0, 0);
      PART("zino3", "zino3_bar", 1000033, 0, 0, 0, 0);
      PART("wino2", "wino2_bar", 1000034, 0, 0, 0, 0);
      PART("zino4", "zino4_bar", 1000056, 0, 0, 0, 0);//double check
      PART("neut40", "neut40_bar", 1000035, 0,0, 100.*GeV, 0 );
      PART("charg2+", "charg2-", 1000037, 0,1, 100.*GeV, 0 ); 
      
      PART("gravitino",		 1000039, 2, 0,  0.);
            
      PART("pi+", "pi-", 211, 0, 1,	139.5675*MeV, hbarc/(7.804*m)	);
      PART("pi0",		111, 0, 0,	134.9739*MeV, hbarc/(2.5E-6*cm) );
      PART("K+",  "K-",	321, 1, 1,	493.646*MeV,  hbarc/(379.9*cm)	);
      
      PART("K0_S",		310, 0, 0,	497.671*MeV,  hbarc/(2.675*cm)	);
      PART("K0_L",		130, 0, 0,	497.671*MeV,  hbarc/(15.50*m)	);
      
      PART("rho+","rho-",213, 2, 1,	768.3*MeV,	   149.*MeV 		);
      PART("rho0",		113, 2, 0,	768.3*MeV,	   149.*MeV 		);
      PART("omega",		223, 2, 0,	781.95*MeV,    8.43*MeV 		);
      PART("phi",		333, 2, 0, 1019.412*MeV,   4.41*MeV 		);
      
      PART("p", "p_bar",2212, 1, 1,	938.272*MeV);
      PART("n", "n_bar",2112, 1, 0,	939.566*MeV);
      PART("J/psi" , 	443, 2, 0,	 3097.*MeV,    0.07*MeV 		);
      
      PART("Lambda","Lambda_bar",	 3122, 1, 0,  1115.63*MeV, hbarc/(7.89*cm));
      PART("Sigma+","Sigma+_bar", 3222, 1, 1,  1189.37*MeV, hbarc/(2.40*cm));
      PART("Sigma0","Sigma0_bar", 3212, 1, 0,  1192.55*MeV, hbarc/(2.2e-9*cm));
      PART("Sigma-","Sigma-_bar", 3112, 1,-1,  1197.43*MeV, hbarc/(4.43*cm));
      PART("Sigma0(1385)", "Sigma0(1385)_bar", 3214, 3, 0, 1.384*GeV, 36.0*MeV);
      PART("Sigma(1385)-", "Sigma(1385)-_bar", 3114, 3, 0, 1.3872*GeV, 39.4*MeV);
      PART("Xi0","Xi0_bar",		 3322, 1, 0,  1314.9*MeV, hbarc/(8.69*cm));
      PART("Xi-","Xi-_bar",		 3312, 1,-1,  1321.32*MeV, hbarc/(4.91*cm));
      PART("Omega-","Omega-_bar",   3334, 3,-1,	1672.43*MeV, hbarc/(2.46*cm));
      
      PART("D+", "D-",	411, 0, 1, 1869.3*MeV,	hbarc/(m*320e-6));
      PART("D0","D0-bar",421, 0, 0, 1864.5*MeV,	hbarc/(m*125.9e-6));
      PART("D*+","D*-",	413, 1, 1, 2010.1*MeV,	  0.5*MeV);
      PART("D*0","D*0-bar",423,1,0, 2007.1*MeV,	  0.5*MeV);
      PART("D(s)+","D(s)-",431,0,1, 1968.8*MeV,	  1.46E-12*GeV);
      PART("D(s)*+","D(s)*-",433,0, 0, 2110.3*MeV);
      
      PART("B+","B-",	521, 0, 1, 5278.6*MeV,	  hbarc/(m*387e-6));
      PART("B0","B0-bar",511, 0, 0, 5278.7*MeV,	  hbarc/(m*387e-6));
      
      PART("upsilon(4S)",70553, 2, 0, 10.5800*GeV, 23.8*MeV);
      
      PART("deutron", 10002,  1, 1,	 1876.01*MeV);
      PART("triton",  10003,  2, 1,	 2809.26*MeV);
      PART("alpha",	 20001,  0, 2,	 3728.17*MeV);
      
      PART("Z0", 	 	33,	 2, 0,	 91.187*GeV, 2.490*GeV);
      PART("Zo", 	 	23,	 2, 0,	 91.187*GeV, 2.490*GeV);
      
      PART("H0/H02",   	35,  	0, 0,   300*GeV, 1*GeV);
      PART("h0/H01",	25,  0, 0,   44.0*GeV, 0);
      PART("H+",	 	37,  	0, 1,  140.0*GeV,1*GeV);
      
      
   }
   private void PART(String pname, String aname,int id, int spin,double charge, double mass,double width)
   {
      add(new Type(pname, id,spin, charge,mass,width));
      add(new Type(aname,-id,spin,-charge,mass,width));
   }
   private void PART(String pname, String aname,int id, int spin,double charge, double mass)
   {
      add(new Type(pname, id,spin, charge,mass,0));
      add(new Type(aname,-id,spin,-charge,mass,0));
   }
   private void PART(String pname, int id, int spin,int charge, double mass,double width)
   {
      add(new Type(pname,id,spin,charge,mass,width));
   }
   private void PART(String pname, int id, int spin,int charge, double mass)
   {
      add(new Type(pname,id,spin,charge,mass,0));
   }
   private void add(Type type)
   {
      map.put(new Integer(type.getPDGID()),type);
   }
   
   /**
    * Get the properties for a particular particle.
    * Note, this method no longer throws UnknownParticleID. It will always
    * returns a ParticleType object, from which at least the PDGID can be
    * obtained. Calling other methods on the ParticleType object may cause
    * UnknownParticleID to be thrown if the PDGID is unknown.
    */
   public ParticleType get(int PDGID)
   {
      ParticleType pt = (ParticleType) map.get(new Integer(PDGID));
      if (pt == null) pt = new UnknownType(PDGID);
      return pt;
   }
   public Set types()
   {
      return new HashSet(map.values());
   }
   private Map map = new HashMap();
   
   private class UnknownType implements ParticleType
   {
      UnknownType(int pdgid)
      {
         this.id = pdgid;
      }
      public String getName()
      {
         return "unknown";
      }
      public int getPDGID()
      {
         return id;
      }
      public int get2xSpin()
      {
         throw new UnknownParticleIDException(id);
      }
      public double getCharge()
      {
         throw new UnknownParticleIDException(id);
      }
      public double getMass()
      {
         throw new UnknownParticleIDException(id);
      }
      public double getWidth()
      {
         throw new UnknownParticleIDException(id);
      }
      public String toString()
      {
         return "unknown (id="+id+")";
      }
      
      public hep.physics.particle.properties.ParticlePropertyProvider getParticlePropertyProvider()
      {
         return DefaultParticlePropertyProvider.this;
      }
      
      private int id;
   }
   private class Type implements ParticleType
   {
      Type(String name, int id, int spin, double charge, double mass,double width)
      {
         this.name = name;
         this.id = id;
         this.spin = spin;
         this.charge = charge;
         this.mass = mass;
         this.width = width;         
      }
      public String getName()
      {
         return name;
      }
      public int getPDGID()
      {
         return id;
      }
      public int get2xSpin()
      {
         return spin;
      }
      public double getCharge()
      {
         return charge;
      }
      public double getMass()
      {
         return mass;
      }
      public double getWidth()
      {
         return width;
      }
      public String toString()
      {
         Formatter formatter = new Formatter();
         formatter.format("%s (mass=%5g id=%d charge=%5g)",name,mass,id,charge);
         return formatter.toString();
      }
      public boolean equals(Object o)
      {
         if (o instanceof ParticleType)
         {
            ParticleType that = (ParticleType) o;
            return Math.abs(this.getPDGID()) == Math.abs(that.getPDGID());
         }
         return false;
      }
      public int hashCode()
      {
         return Math.abs(getPDGID());
      }
      
      public hep.physics.particle.properties.ParticlePropertyProvider getParticlePropertyProvider()
      {
         return DefaultParticlePropertyProvider.this;
      }
      
      private String name;
      private int id;
      private int spin;
      private double charge;
      private double mass;
      private double width;
   } 
}
