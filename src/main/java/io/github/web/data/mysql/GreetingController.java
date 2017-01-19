package io.github.web.data.mysql;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.rosuda.JRI.REXP;
import org.rosuda.JRI.Rengine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
@Controller
public class GreetingController 
{
	Rengine engine;
	private static int nejecucion=0;
	///////////////////////////////////////////////////////////////////////////
	/*
	 * ojo: cuando se nombre una variable lp se refiere al bloque de lógica proposicional
	 * lpo para lógica de primer orden, los dos bloques de la asignatura
	 * */
	@RequestMapping(value="/", method=RequestMethod.GET)//index.html
	public ModelAndView greeting(HttpSession sesion) 
	{
		boolean vez=sesion.isNew();
		System.out.println("index: sesion nueva:"+vez);
		return new ModelAndView("index");
	}

	@RequestMapping(value="/index", method=RequestMethod.GET)//index.html
	public ModelAndView greetingIndex(HttpSession sesion) 
	{
		boolean vez=sesion.isNew();
		System.out.println("index: sesion nueva:"+vez);
		return new ModelAndView("index");
	}

	///// 							METODO QUE CALCULA LA NOTA FINAL DE LA ASIGNATURA	/////////////////////////////////////////
	//@Secured("ROLE_ADMIN")
	/**
	 * ATRIBUTOS HTTP
	 * @param repescalp
	 * @param repescalpo
	 * @param individual2
	 * @param grupal2
	 * @param sesion
	 * @return
	 */
	@RequestMapping(value="/calcularNota", params="calcular", method=RequestMethod.POST)//calculadora de nota final
	public ModelAndView calcularNota(@RequestParam (required = false) String individual1, @RequestParam String grupal1 ,@RequestParam (required = false) String individual2 , @RequestParam String grupal2,@RequestParam(required = false) String bloquerepesca,@RequestParam(required = false) String repesca,@RequestParam(required = false) String juliolp,@RequestParam(required = false) String juliolpo,HttpSession sesion) 
	{
		System.out.println("en calculo de nota final");
		System.out.println("**********************************************");
		System.out.println("nota individual LP:"+individual1);
		System.out.println("nota grupal LP:"+grupal1);
		System.out.println("nota individual LPO:"+individual2);
		System.out.println("nota grupal LPO:"+grupal2);
		System.out.println("bloque de repesca:"+bloquerepesca);
		System.out.println("nota de repesca:"+repesca);
		System.out.println("julio--lp:"+juliolp);
		System.out.println("julio--lpo:"+juliolpo);
		System.out.println("**********************************************");
		boolean hizorepesca=false;
		boolean hizorepescalp=false;
		boolean hizorepescalpo=false;
		sesion.setAttribute("lpindividual", individual1);
		sesion.setAttribute("lpoindividual", individual2);
		if(!repesca.equals(""))//significa que el alumno SI  hizo examen de repesca porque puso una nota en el formulario
		{
			System.out.println("HICE REPESCA");
			if(bloquerepesca.equals("LP"))
			{
				System.out.println("valor del examen de repesca:"+repesca);
				System.out.println("NOTA INDIVIDUAL LP SUSTITUIDA EN REPESCA");
				individual1=repesca;
				sesion.setAttribute("repescalp", repesca);
				hizorepescalp=true;

			}
			else if (bloquerepesca.equals("LPO"))
			{
				System.out.println("NOTA INDIVIDUAL LPO 2 SUSTITUIDA EN REPESCA");
				individual2=repesca;
				sesion.setAttribute("repescalpo", repesca);
				hizorepescalpo=true;
			}
			hizorepesca=true;
		}
		boolean julio=false;
		boolean hizojuliolp=false;
		boolean hizojuliolpo=false;
		double notalp=0;//nota del bloque1
		double notalpo=0;//nota del bloque2
		double notafinal=0;//nota final
		/////////////////////////////
		//comprobar si fue al examen de julio, al menos a una parte
		if(!(juliolp.equals("") && juliolpo.equals("")))//significa que  campos NO estan vacios, es decir, no si presentó a julio para algo
		{
			julio=true;
			if(!(juliolp.equals("")))
			{
				hizojuliolp=true;
			}
			if(!(juliolpo.equals("")))
			{
				hizojuliolpo=true;
			}			
		}		
		System.out.println("valor de hizorepoesca:"+hizorepesca);
		System.out.println("valor de julio:"+julio);
		boolean np1=false;
		boolean np2=false;
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		if(!julio)//si no se presenta a nada de julio
		{
			if(individual1.equals("") && !hizorepescalp)//digo: no me presenté a nada de julio y ni a la repesca
			{
				np1=true;
				//return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal","--");
			}
			if(individual2.equals("") && !hizorepescalpo )//digo: no me presenté a nada de julio y alguno de los dos campos no 
			{
				np2=true;
				//return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal","--");
			}
		}
		else//si se presento a algo de julio, 
		{
			if(individual1.equals("") && juliolp.equals(""))//si dejo vacio individual LP y tambien vacio en julio lp eso es no presentado; debo ver si para salvarlo hizo la repesca
			{
				if(!hizorepescalp)//si tampoco se puso repesca eso es NP
				{
					np1=true;
					//return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal","--");
				}				
			}
			if(individual2.equals("") && juliolpo.equals(""))//si dejo vacio invidiual lpo y tambien julio lpo, NP
			{
				if(!hizorepescalpo)//si tampoco se puso repesca eso es NP
				{
					np2=true;
					//return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal","--");
				}					
			}
		}	
		///////////////////////////////////////////////////////////////////////////////////////////////////////////////////		
		//calculo según guía de aprendizaje de lógica 16-17
		System.out.println("A CALCULO NORMAL");
		System.out.println("individual1:"+individual1);
		System.out.println("grupal1:"+grupal1);
		System.out.println("individual2L:"+individual2);
		System.out.println("grupal2:"+grupal2);
		System.out.println("np1:"+np1);
		System.out.println("np2:"+np2);
		double individualb1=0;
		double grupalb1=0;
		double individualb2=0;
		double grupalb2=0;
		boolean flaglp=true;
		boolean flaglpo=true;
		//////////CALCULO DE NOTA DEL BLOQUE LP
		if(!np1)//significa: si SI me presente al bloque 1
		{
			if(!hizojuliolp)// ya las notas individuales vienen cambiadas de la posible repesca. Dice: si no ha ido a julio y Si se presentoa  ese bloque
			{
				System.out.println("1");
				individualb1=Double.parseDouble(individual1);//conversion de texto de formulario a numero para operar con el
				if(!grupal1.equals(""))//caso donde alumno indica que no se presento a la actividdad grupal de LP
				{
					System.out.println("2");
					grupalb1=Double.parseDouble(grupal1);
				}

				notalp=notadebloque(individualb1,grupalb1);
			}
			else
			{
				System.out.println("3");
				notalp=Double.parseDouble(juliolp);//como se presento a julio de lp lo que saque ahi es la nota del bloque
				System.out.println("ME PRESENTE A JULIO de LP Y SUStiyuyo nota");
			}
		}
		else//si no me presente a bloque 1
		{
			flaglp=false;
		}
		
		//////////CALCULO DE NOTA DEL BLOQUE LPO
		if(!np2)
		{
			if(!hizojuliolpo)
			{
				System.out.println("2");
				individualb2=Double.parseDouble(individual2);
				if(!grupal2.equals(""))//caso donde alumno indica que no se presento a la actividdad grupal de LP
				{
					grupalb2=Double.parseDouble(grupal2);
				}			 
				notalpo=notadebloque(individualb2,grupalb2);
			}
			else
			{
				notalpo=Double.parseDouble(juliolpo);
				System.out.println("ME PRESENTE A JULIO de LPO Y SUStiyuyo nota");
			}	
		}
		else//no me presente a bloque 2
		{
			flaglpo=false;
		}
		
		////////////////////////////////EN ESTA PARTE DIGO: SI NO ME PRESENTE A UNA PARTE PERO A LA OTRA SI ES NO PRESENTADO
		sesion.setAttribute("notalp", notalp);
		sesion.setAttribute("notalpo", notalpo);
		if(!flaglp && flaglpo)//me presente a lpo
		{
			if(notalpo>=5)
			{
				return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal",notalpo).addObject("cual","LPO");
			}
			else
			{
				return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal",notalpo).addObject("cual","NINGUNO. Tendrás que ir a julio con toda la asignatura.");
			}

		}
		if(!flaglpo && flaglp)//me presente a lp
		{
			if(notalp>=5)
			{
				return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal",notalp).addObject("lpofinal","--").addObject("cual","LP");
			}
			else
			{
				return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal",notalp).addObject("lpofinal","--").addObject("cual","NINGUNO. Tendrás que ir a julio con toda la asignatura.");
			}
			
		}
		if(!flaglp && !flaglpo)
		{
			return new ModelAndView("sinpresentar").addObject("resultadofinal", "No presentado (NP)").addObject("lpfinal","--").addObject("lpofinal","--").addObject("cual","NINGUNO. Tendrás que ir a julio con toda la asignatura.");
		}
		//////////////////////////////////////////////CALCULO DE LA NOTA FINAL
		if(notalp<3 || notalpo <3)
		{
			//En el caso particular en el que se apruebe un bloque y el otro
			//tenga una nota inferior a 3, la nota final de la asignatura será la del bloque suspenso.
			notafinal=Math.min(notalp, notalpo);
		}
		else
		{
			notafinal=(notalp+notalpo)/2;
		}
		double resultadofinal=redondear(notafinal,2);
		double lpfinal=redondear(notalp,2);
		
		double lpofinal=redondear(notalpo,2);
		
		System.out.println("NOTA lp:"+lpfinal);
		System.out.println("NOTA LPO:"+lpofinal);
		System.out.println("NOTA FINAL:"+resultadofinal);
		////////////ahora comprobar, si suspendio que bloque se guarda
		sesion.setAttribute("notafinal", resultadofinal);
		if(resultadofinal<5)
		{
			System.out.println("suspendio asignatura");
			if(lpfinal>=5)
			{
				return new ModelAndView("resultadoNotaGuardada").addObject("resultadofinal", resultadofinal).addObject("lpfinal",lpfinal).addObject("lpofinal",lpofinal).addObject("bloqueguardado","LP").addObject("aCualPresentarse","LPO.");
			}


			if(lpofinal>=5)
			{
				return new ModelAndView("resultadoNotaGuardada").addObject("resultadofinal", resultadofinal).addObject("lpfinal",lpfinal).addObject("lpofinal",lpofinal).addObject("bloqueguardado","LPO").addObject("aCualPresentarse","LP.");
			}

			else//no guardó ningún bloque 
			{
				return new ModelAndView("resultadoNotaGuardada").addObject("resultadofinal", resultadofinal).addObject("lpfinal",lpfinal).addObject("lpofinal",lpofinal).addObject("bloqueguardado","NINGUNO").addObject("aCualPresentarse","ambos bloques.");
			}
		}	
		///////////////////////////////////////////////

		String escala="";
		///////////////////////////////////////////////añadir escala (aprobado, notable, sobresaliente)
		if(resultadofinal >=5 && resultadofinal<=6.9)
		{
			escala="APROBADO";
		}
		if(resultadofinal >=7.0 && resultadofinal<=8.9)
		{
			escala="NOTABLE";
		}
		if(resultadofinal >=9.0 && resultadofinal<=10)
		{
			escala="SOBRESALIENTE";
		}
		
		return new ModelAndView("resultadoNota").addObject("resultadofinal", resultadofinal).addObject("lpfinal",lpfinal).addObject("lpofinal",lpofinal).addObject("escala",escala);
	}
	/**
	 * 
	 * @param individualb1
	 * @param grupalb1
	 * @return
	 */
	@RequestMapping(value="/estadisticas",method = RequestMethod.GET)//alta de una pelicula
	public ModelAndView mostrarEstadisticas(HttpSession sesion)
	{		
		System.out.println("***********en mostrar estadisticas**********");
		Double examenlp=0.0;
		Double repescalp=0.0;
		Double notalp=0.0;
		Double examenlpo=0.0;
		Double repescalpo=0.0;
		Double notalpo=0.0;
		Double notafinal=0.0;
		if(sesion.getAttribute("lpindividual")!=null)//en caso que el usuario no haya metido esa variable
		{
			if(sesion.getAttribute("lpindividual")!="")
			{
				String o;
				o=(String) sesion.getAttribute("lpindividual");
				examenlp=Double.parseDouble(o);
				examenlp=examenlp*100;
			}
			
		}
		System.out.println("examenlp: "+examenlp);
		if(sesion.getAttribute("repescalp")!=null)
		{
			if(sesion.getAttribute("repescalp")!="")
			{
				String o;
				o=(String) sesion.getAttribute("repescalp");
				repescalp=Double.parseDouble(o);
				repescalp=repescalp*100;
			}
			
		}
		System.out.println("caca: "+sesion.getAttribute("notalp"));
		if(sesion.getAttribute("notalp")!=null)
		{
			notalp=(Double) sesion.getAttribute("notalp")*100;
		}
		System.out.println("lpo individual: "+sesion.getAttribute("lpoindividual"));
		if(sesion.getAttribute("lpoindividual")!=null)
		{
			if(sesion.getAttribute("lpoindividual")!="")
			{
				String o;
				o=(String) sesion.getAttribute("lpoindividual");
				examenlpo=Double.parseDouble(o);
				examenlpo=examenlpo*100;
			}
			
		}
		System.out.println("repesca lpo: "+sesion.getAttribute("repescalpo"));
		if(sesion.getAttribute("repescalpo")!=null)
		{
			if(sesion.getAttribute("repescalpo")!="")
			{
				String o;
				o=(String) sesion.getAttribute("repescalpo");
				System.out.println("repesca lpo   o  "+o);
				repescalpo=Double.parseDouble(o);
				repescalpo=repescalpo*100;
			}
			
		}
		System.out.println("nota lpo: "+sesion.getAttribute("notalpo"));
		if(sesion.getAttribute("notalpo")!=null)
		{
			notalpo=(Double) sesion.getAttribute("notalpo")*100;
		}
		System.out.println("notgafinalcaca: "+sesion.getAttribute("notafinal"));
		if(sesion.getAttribute("notafinal")!=null)
		{
			notafinal=(Double) sesion.getAttribute("notafinal")*100;
		}
		
		return new ModelAndView("charts").addObject("lpindividual",examenlp).addObject("repescalp",repescalp).addObject("notalp",notalp).addObject("lpoindividual",examenlpo).addObject("repescalpo",repescalpo).addObject("notalpo",notalpo).addObject("notafinal",notafinal);
		
	}
	

	@RequestMapping(value="/calcularNota", params="calculariesgo", method=RequestMethod.POST)//calculadora de nota final
	public ModelAndView calculaRiesgo(@RequestParam (required = false) String grupal1, @RequestParam (required = false) String individual1, @RequestParam (required = false) String grupal2, @RequestParam (required = false) String individual2, @RequestParam(required = false) String bloquerepesca,@RequestParam(required = false) String repesca,HttpSession sesion)
	{
		double medialpgrupo=5.135;
		double	medialpindividual=4.828;
		double	mediarepescalp=4.93;
		double	medialpogrupo=5.48;
		double	medialpoindividual=4.332;
		double	mediarepescalpo=4.222;
		//variables que se le pasan a la funcion dameriesgo()
		double num_lpgrupo=0;
		double num_lpindividual=0;
		double num_repescalp=0;
		double num_lpogrupo=0;
		double num_lpoindividual=0;
		double num_repescalpo=0;
		
		num_repescalp=mediarepescalp;
		num_repescalpo=mediarepescalpo;
		if(!repesca.equals(""))//significa que el alumno SI  hizo examen de repesca porque puso una nota en el formulario
		{
			if(bloquerepesca.equals("LP"))
			{
					num_repescalp=Double.parseDouble(repesca);
				
			}
			else if (bloquerepesca.equals("LPO"))
			{
				num_repescalpo=Double.parseDouble(repesca);
			}
		}
		
		//si el alumno no ingresa una nota para calcular el riesgo. se asumirá la media para esa actividad.


		if(grupal1.equals("")) //significa que el alumno no puso LP grupal, se asigna su media
		{
			num_lpgrupo=medialpgrupo;
		}
		else 
		{
			num_lpgrupo=Double.parseDouble(grupal1);
		}
		///////////////////////////////////////////////////////////////////////
		if(individual1.equals("")) //significa que el alumno no puso LP grupal, se asigna su media
		{
			num_lpindividual=medialpindividual;
		}
		else 
		{
			num_lpindividual=Double.parseDouble(individual1);
		}
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
		if(grupal2.equals("")) //significa que el alumno no puso LP grupal, se asigna su media
		{
			num_lpogrupo=medialpogrupo;
		}
		else 
		{
			num_lpogrupo=Double.parseDouble(grupal2);
		}
///////////////////////////////////////////////////////////////////////
		if(individual2.equals("")) //significa que el alumno no puso LP grupal, se asigna su media
		{
			num_lpoindividual=medialpoindividual;
		}
		else 
		{
			num_lpoindividual=Double.parseDouble(individual2);
		}
///////////////////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////
	
		double []riesgo = new double [2];
		if(nejecucion==0)
		{
			System.out.println("primera vez que se ejecuta el calculo de riesgo, corriendo el script completo");
			riesgo=dameRiesgo(num_lpindividual,num_lpgrupo,num_repescalp,num_lpoindividual,num_lpogrupo,num_repescalpo);
		}
		else
		{
			System.out.println("NO  es primera vez que se ejecuta el calculo de riesgo, se ejecuta solo la predicción");
			riesgo=otroRiesgo(num_lpindividual,num_lpgrupo,num_repescalp,num_lpoindividual,num_lpogrupo,num_repescalpo);
		}
		
		double suspender=riesgo[0]*100;
		suspender=redondear(suspender,3);
		double aprobar=riesgo[1]*100;
		aprobar=redondear(aprobar,3);
		double precision=riesgo[2];
		precision=redondear(precision,1);
		nejecucion++;
		if(aprobar>=60.0)
		{
			return new ModelAndView("riesgo_positivo").addObject("riesgo_aprobar",aprobar).addObject("riesgo_suspender",suspender).addObject("precision",precision);
		}
		else
		{
			return new ModelAndView("riesgo_negativo").addObject("riesgo_aprobar",aprobar).addObject("riesgo_suspender",suspender).addObject("precision",precision);
		}
		

	}
	
	//////////////////////////////////////////////////////////////// 		METODOS AUXILIARES 			////////////////////////////////////////////////////
	public double notadebloque(double individualb1, double grupalb1)
	{
		double notalp=0;
		if(individualb1>=5)
		{
			notalp=(individualb1*0.7)+(grupalb1*0.3);
		}
		else //si NI < 5
		{
			if(grupalb1<=individualb1)
			{
				notalp=(individualb1*0.7)+(grupalb1*0.3);
			}

			else 
			{
				if(grupalb1>individualb1)
				{
					double f=(grupalb1-individualb1)/grupalb1;
					notalp=(individualb1*(0.7+(0.3*f)))+(grupalb1*(1-(0.7+0.3*f)));

				}
			}

		}
		return notalp;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////	
	public static double redondear(double value, int places) {
		if (places < 0) throw new IllegalArgumentException();

		long factor = (long) Math.pow(10, places);
		value = value * factor;
		long tmp = Math.round(value);
		return (double) tmp / factor;
	}
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	@RequestMapping(value="/error", method=RequestMethod.GET)//index.html
	public ModelAndView error() 
	{
		return new ModelAndView("error");
	}
	  //funcionDame riesgo: llama al script de R, realiza la predicción y devuelve un array de dos doubles, en la primera posición devuevle el riesgo de suspender y en la segunda, el de aprobar.
public double[] dameRiesgo(double lpindividual, double lpgrupal, double repescalp, double lpoindividual, double lpogrupal, double repescalpo)
{
	System.out.println("en dame riesgo");
	System.out.println("lpindividual: "+lpindividual);
	System.out.println("lpgrupal"+lpgrupal);
	System.out.println("repesca lp "+repescalp);
	System.out.println("LPO individual: "+lpoindividual);
	System.out.println("LPO grupal: "+lpogrupal);
	System.out.println("LPO repescalp: "+repescalpo);
	 this.engine = new Rengine(new String[] { "--vanilla" }, false, null);
	 String filepath="D:/log.txt";
	 //engine.eval("install.packages(\"xlsx\")"); //instalacion de paquetes se hace en el script
	// engine.eval("install.packages(\"caret\")");
	engine.eval("log<-file('"+filepath+"')"); // en la ruta especificada, tendre el fichero log.txt para ver la consola de la máquina R emulada
	engine.eval("sink(log, append=TRUE)");
	engine.eval("sink(log, append=TRUE, type='message')");
    String rutadatos="./prediccion.R"; /// IMPORTANTE: RUTA DEL SCRIPT A EJECUTAR *****
    //engine.eval("setwd('D:/')"); // es como hacer un cd a la ruta donde está el scritp
    engine.eval("source("+"'"+rutadatos+"'"+")");     
    //source('script') para ejecutar un fichero en una maquina R
   String datos_predecir= "new.sore3 <- data.frame(lpgrupo="+lpgrupal+",lpindividual="+lpindividual+", repescalp="+repescalp+",lpogrupo="+lpogrupal+",lpoindividual="+lpoindividual+", repescalpo="+repescalpo+")";
   engine.eval(datos_predecir); //creo la nueva instancias de datos
  // System.out.println("string de prediccion "+datos_predecir);
   String res="pred=predict(mod_fit3, new.sore3, type=\"prob\")";
   //System.out.println("string de llamar a pred "+res);
   engine.eval(res);
  REXP  susp= engine.eval("pred$SUSPENSO");
  REXP  apro= engine.eval("pred$APROBADO");
  REXP  preci= engine.eval("precision_modelo2");
  double suspenso=susp.asDouble();
  double aprobado=apro.asDouble();
  double precision=preci.asDouble();
    System.out.println("Suspenso: "+suspenso);
    System.out.println("Aprobado: "+aprobado);
    double [] respuestas; 
    respuestas= new double [3];
    respuestas[0]=suspenso;
    respuestas[1]=aprobado;
    respuestas[2]=precision;
    engine.end();
return respuestas;
}


////////////////////////////////////////////////////////
/*
 * ANTES DABA ERROR SI SE EJECUTABA LA PREDICCION DE RIESGO DOS 
 * VECES; LLEGUE A LA CONCLUSION QUE EL PROBLERA RADICABA EN LA CREACION VARIAS VECES DE UN OBJETO RENGINE, POR TANTO,
 * cree la variable engine global, y se inicializa solo en dameRiesgo() -se hace new ahi solo- y en la funcion otroRiesgo, ya solo se llama 
 * al engine sin crearlo, y ya no da ese problema.
 * Gracias a eso tambien las posteriores ejecuciones de la prediccion de riesgo son más ràpidas, porque no carga todo el script sino que solo se ahce la mera predicción sobre las nuevas notas
 * */

public double[] otroRiesgo(double lpindividual, double lpgrupal, double repescalp, double lpoindividual, double lpogrupal, double repescalpo)
{

	//Rengine engine = new Rengine(new String[] { "--vanilla" }, false, null);
	 String filepath="C:/log.txt";
	 //engine.eval("install.packages(\"xlsx\")"); //instalacion de paquetes se hace en el script
	// engine.eval("install.packages(\"caret\")");
	engine.eval("log<-file('"+filepath+"')"); // en la ruta especificada, tendre el fichero log.txt para ver la consola de la máquina R emulada
	engine.eval("sink(log, append=TRUE)");
	engine.eval("sink(log, append=TRUE, type='message')");
   String datos_predecir= "new.sore3 <- data.frame(lpgrupo="+lpgrupal+",lpindividual="+lpindividual+", repescalp="+repescalp+",lpogrupo="+lpogrupal+",lpoindividual="+lpoindividual+", repescalpo="+repescalpo+")";
   engine.eval(datos_predecir); //creo la nueva instancias de datos
  // System.out.println("string de prediccion "+datos_predecir);
   String res="pred=predict(mod_fit3, new.sore3, type=\"prob\")";
   //System.out.println("string de llamar a pred "+res);
   engine.eval(res);
  REXP  susp= engine.eval("pred$SUSPENSO");
  REXP  apro= engine.eval("pred$APROBADO");
  REXP  preci= engine.eval("precision_modelo2");
  double suspenso=susp.asDouble();
  double aprobado=apro.asDouble();
  double precision=preci.asDouble();
    System.out.println("Suspenso: "+suspenso);
    System.out.println("Aprobado: "+aprobado);
    double [] respuestas; 
    respuestas= new double [3];
    respuestas[0]=suspenso;
    respuestas[1]=aprobado;
    respuestas[2]=precision;
    engine.end();
return respuestas;
}

}//fin del programa