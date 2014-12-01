/** This class is a modified version of the class downloaded from
 * http://www.atomikos.com/Documentation/Tomcat6Integration33
 */

package com.atomikos.tomcat;

import java.util.Hashtable;
import java.util.Enumeration;
import javax.naming.Name;
import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.RefAddr;
import javax.naming.spi.ObjectFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.naming.ResourceRef;
import org.apache.naming.factory.Constants;

import com.atomikos.beans.PropertyUtils;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.nonxa.AtomikosNonXADataSourceBean;

public class BeanFactory implements ObjectFactory {
    
    private final static Log log = LogFactory.getLog(BeanFactory.class);

  public Object getObjectInstance (
    Object obj, Name name, Context nameCtx, Hashtable environment) 
    throws NamingException {
    if (obj instanceof ResourceRef) {
      try {
        Reference ref = (Reference) obj;
        String beanClassName = ref.getClassName();
        Class beanClass = null;
        ClassLoader tcl = Thread.currentThread().getContextClassLoader();
        if (tcl != null) {
          try {
            beanClass = tcl.loadClass(beanClassName);
          } catch(ClassNotFoundException e) {
          }
        } else {
          try {
            beanClass = Class.forName(beanClassName);
          } catch(ClassNotFoundException e) {
            e.printStackTrace();
          }
        }
        if (beanClass == null) {
          throw new NamingException("Class not found: " + beanClassName);
        }
        if (!AtomikosDataSourceBean.class.isAssignableFrom(beanClass) &&
            !AtomikosNonXADataSourceBean.class.isAssignableFrom(beanClass)) {
          throw new NamingException(
            "Class is not a AtomikosDataSourceBean or a AtomikosNonXADataSourceBean: " + beanClassName);
        }
                
        if (log.isDebugEnabled()) 
          log.debug("instanciating bean of class " + beanClass.getName());

        Object bean = beanClass.newInstance();
                
        int i=0;
        Enumeration en = ref.getAll();
        while (en.hasMoreElements()) {
          RefAddr ra = (RefAddr) en.nextElement();
          String propName = ra.getType();
                    
          if (propName.equals(Constants.FACTORY) || 
              propName.equals("scope") || 
              propName.equals("auth")) {
            continue;
          }
                    
          String value = (String) ra.getContent();
                    
          if (log.isDebugEnabled()) 
            log.debug("setting property '" + propName + 
                      "' to '" + value + "'");

          PropertyUtils.setProperty(bean, propName, value);
                    
          i++;
        }

        if (log.isDebugEnabled()) 
          log.debug("done setting " + i +
                    " property(ies), now initializing resource " + bean);

        if (bean instanceof AtomikosNonXADataSourceBean) {
          ((AtomikosNonXADataSourceBean)bean).init();
        }
        else {
          ((AtomikosDataSourceBean)bean).init();
        }
        return bean;

      } catch (Exception ex) {
        throw (NamingException) new NamingException(
          "error creating AtomikosDataSourceBean").initCause(ex);
      }

    } else {
      return null;
    }

  }
}