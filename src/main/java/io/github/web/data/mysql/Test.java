package io.github.web.data.mysql;

import java.io.IOException;

import org.rosuda.JRI.RBool;
import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.rosuda.REngine.JRI.*;
import org.rosuda.REngine.*;
public class Test {

    public static void main(String a[]) {

    	double lpindividual=5;
    	double lpgrupal=6;
    	double repescalp=7;
    	double lpoindividual=4;
    	double lpogrupal=3;
    	double repescalpo=2;	
    	System.out.println("en dame riesgo");
    	System.out.println("lpindividual: "+lpindividual);
    	System.out.println("lpgrupal"+lpgrupal);
    	System.out.println("repesca lp "+repescalp);
    	System.out.println("LPO individual: "+lpoindividual);
    	System.out.println("LPO grupal: "+lpogrupal);
    	System.out.println("LPO repescalp: "+repescalpo);
    	Rengine engine = new Rengine(new String[] { "--vanilla" }, false, null);
    	 String filepath="D:/log.txt";
    	 //engine.eval("install.packages(\"xlsx\")"); //instalacion de paquetes se hace en el script
    	// engine.eval("install.packages(\"caret\")");
    	engine.eval("log<-file('"+filepath+"')"); // en la ruta especificada, tendre el fichero log.txt para ver la consola de la máquina R emulada
    	engine.eval("sink(log, append=TRUE)");
    	engine.eval("sink(log, append=TRUE, type='message')");
        String rutadatos="D:/hola.R"; /// IMPORTANTE: RUTA DEL SCRIPT A EJECUTAR *****
        engine.eval("setwd('D:/')"); // es como hacer un cd a la ruta donde está el scritp
        engine.eval("source("+"'"+rutadatos+"'"+")");     
       String datos_predecir= "new.sore3 <- data.frame(lpgrupo="+lpgrupal+",lpindividual="+lpindividual+", repescalp="+repescalp+",lpogrupo="+lpogrupal+",lpoindividual="+lpoindividual+", repescalpo="+repescalpo+")";
       engine.eval(datos_predecir); //creo la nueva instancias de datos
      // System.out.println("string de prediccion "+datos_predecir);
       String res="pred=predict(mod_fit3, new.sore3, type=\"prob\")";
       System.out.println("string de llamar a pred "+res);
       engine.eval(res);
      REXP  susp= engine.eval("pred$SUSPENSO");
      REXP  apro= engine.eval("pred$APROBADO");
      double suspenso=susp.asDouble();
      double aprobado=apro.asDouble();
        System.out.println("Suspenso: "+suspenso);
        System.out.println("Aprobado: "+aprobado);
      
    }
}