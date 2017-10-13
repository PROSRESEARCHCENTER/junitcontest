package hep.physics.particle;

import hep.physics.particle.properties.ParticleType;
import hep.physics.vec.Hep3Vector;
import hep.physics.vec.HepLorentzVector;
import java.util.ArrayList;

import java.util.Collections;
import java.util.List;


/**
 * Default implementation of Particle.
 * @author Gary Bower
 * @version $Id: BasicParticle.java 8584 2006-08-10 23:06:37Z duns $
 */
public class BasicParticle implements Particle
{
   private Hep3Vector m_origin;
   private HepLorentzVector m_p;
   private double m_productionTime;
   private int m_statusCode;
   private ParticleType m_pType;
   private List parents = Collections.EMPTY_LIST;
   private List daughters = Collections.EMPTY_LIST;
   private double m_mass = Double.NaN;
   
   /**
    * Create a new BasicParticle
    * @param origin The origin of this particle
    * @param p The momentum and energy of this particle
    * @param ptype The type of this particle
    * @param status The status of this particle
    * @param time The production time of this particle
    */
   public BasicParticle(Hep3Vector origin,HepLorentzVector p,ParticleType ptype,int status, double time)
   {
      m_origin = origin;
      m_p = p;
      m_pType = ptype;
      m_statusCode = status;
      m_productionTime = time;
   }
   /**
    * Adds a child to this particle. If the child is also an instance of this
    * class this particle will also be added as its parent.
    * @param child The child particle
    */
   public void addDaughter(Particle child)
   {
      if (daughters == Collections.EMPTY_LIST) daughters = new ArrayList();
      daughters.add(child);
      if (child instanceof BasicParticle)
      {
         ((BasicParticle) child).addParent(this);
      }
   }
   private void addParent(Particle parent)
   {
      if (parents == Collections.EMPTY_LIST) parents = new ArrayList();
      parents.add(parent);
   }
   /**
    * Overrides the mass obtained from the particle type.
    */
   public void setMass(double mass)
   {
      m_mass = mass;
   }

   public double getPX()
   {
      return m_p.v3().x();
   }
   public double getPY()
   {
      return m_p.v3().y();
   }
   public double getPZ()
   {
      return m_p.v3().z();
   }
   public double getEnergy()
   {
      return m_p.t();
   }
   public double getMass()
   {
      return Double.isNaN(m_mass) ? m_pType.getMass() : m_mass;
   }
   public double getOriginX()
   {
      return m_origin.x();
   }
   public double getOriginY()
   {
      return m_origin.y();
   }
   public double getOriginZ()
   {
      return m_origin.z();
   }
   public double getProductionTime()
   {
      return m_productionTime;
   }
   public int getGeneratorStatus()
   {
      return m_statusCode;
   }
   public Hep3Vector getMomentum()
   {
      return m_p.v3();
   }
   public Hep3Vector getOrigin()
   {
      return m_origin;
   }
   public List getDaughters()
   {
      return daughters;
   }
   public List getParents()
   {
      return parents;
   }
   public ParticleType getType()
   {
      return m_pType;
   }
   public double getCharge()
   {
      return getType().getCharge();
   }
   public int getPDGID()
   {
      return m_pType.getPDGID();
   }
   public HepLorentzVector asFourVector()
   {
      return m_p;
   }
}