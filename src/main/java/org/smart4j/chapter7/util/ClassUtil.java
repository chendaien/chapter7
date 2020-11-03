package org.smart4j.chapter7.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class ClassUtil {

    private final static Logger LOGGER = LoggerFactory.getLogger(ClassUtil.class);

    /**
     * 获取类加载器
     */
    public static ClassLoader getClassLoader(){
        return Thread.currentThread().getContextClassLoader();
    }

    /**
     * 加载类
     */
    public static Class<?> loadClass(String className,boolean isInitialized){
        Class<?> cls;
        try {
            cls = Class.forName(className,isInitialized,getClassLoader());
        }catch (ClassNotFoundException e){
           LOGGER.error("load class failure",e);
           throw new RuntimeException(e);
        }
        return cls;
    }

    /**
     * 获取指定包名下的所有类
     */
    public static Set<Class<?>> getClassSet(String packageName){
        Set<Class<?>> classSet = new HashSet<Class<?>>();
        try {
            Enumeration<URL> urlEnumerations = getClassLoader().getResources(packageName.replace(".","/"));
            while (urlEnumerations.hasMoreElements()){
                URL url = urlEnumerations.nextElement();
                if(url != null){
                    String protocol = url.getProtocol();
                    if(protocol.equals("file")){
                        String packagePath = url.getPath().replaceAll("%20","");
                        addClass(classSet,packagePath,packageName);
                    }else if(protocol.equals("jar")){
                        JarURLConnection jarURLConnection = (JarURLConnection) url.openConnection();
                        if(jarURLConnection !=null){
                            JarFile jarFile = jarURLConnection.getJarFile();
                            if(jarFile != null){
                              Enumeration<JarEntry> jarEntryEnumerations = jarFile.entries();
                              while (jarEntryEnumerations.hasMoreElements()){
                                  JarEntry jarEntry = jarEntryEnumerations.nextElement();
                                  String jarEntryName = jarEntry.getName();
                                  if(jarEntryName.equals(".class")){
                                      String className = jarEntryName.substring(0,jarEntryName.lastIndexOf(".")).replaceAll("/",".");
                                      doAddClass(classSet,className);
                                  }
                              }
                            }
                        }
                    }
                }
            }
        }catch (Exception e){
            LOGGER.error("get class Set failure",e);
            throw new RuntimeException(e);
        }
        return classSet;
    }

    private static void addClass(Set<Class<?>> classSet,String packagePath,String packageName){
        File[] files = new File(packagePath).listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                return (file.isFile()&&file.getName().endsWith(".class"))||file.isDirectory();
            }
        });
        for (File file:files) {
            String filename = file.getName();
            if(file.isFile()){
                String className = filename.substring(0,filename.lastIndexOf("."));
                if(StringUtil.isNotEmpty(packageName)){
                    className = packageName+"."+className;
                }
                doAddClass(classSet,className);
            }else{
                String subPackagePath = filename;
                if(StringUtil.isNotEmpty(packagePath)){
                    subPackagePath = packagePath+"/"+subPackagePath;
                }
                String subPackageName = filename;
                if(StringUtil.isNotEmpty(packageName)){
                    subPackageName = packageName+"."+subPackageName;
                }
                addClass(classSet,subPackagePath,subPackageName);
            }
        }
    }

    private static void doAddClass(Set<Class<?>> classSet,String className){
        Class<?> cls = loadClass(className,false);
        classSet.add(cls);
    }

}
