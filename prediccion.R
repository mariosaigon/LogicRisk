
##seccion de codigo 1
#install.packages('caret', dependencies = TRUE)
#install.packages("xlsx")
#install.packages('e1071', dependencies=TRUE)
library(xlsx)
datos= read.xlsx("./datos1.xls" , sheetName="ppal")
##seccion de codigo 4
datos$notalp <- as.numeric(as.character(datos$notalp))
datos$notalpo <- as.numeric(as.character(datos$notalpo))
datos$notacurso <- as.numeric(as.character(datos$notacurso))
##seccion de codigo 9
datos$notacurso[is.na(datos$notacurso)] <- 0  #sustituir NA en notacurso por ceros
#seccion de codigo 10
datos$repescalp[((datos$lpindividual>=5) & is.na(datos$repescalp))] <- 10
datos$repescalpo[((datos$lpoindividual>=5) & is.na(datos$repescalpo))] <- 10
#seccion de codigo 11
datos$repescalp[(((datos$lpindividual<5) | is.na(datos$lpindividual)) & is.na(datos$repescalp))] <- 0  
datos$repescalpo[(((datos$lpoindividual<5) | is.na(datos$lpoindividual)) & is.na(datos$repescalpo))] <- 0 
#seccion de codigo 12
datos$lpindividual[is.na(datos$lpindividual)] <- 0  #sustituir NA en lpindividual por ceros
datos$lpoindividual[is.na(datos$lpoindividual)] <- 0  #sustituir NA en lpoindividual por ceros
#seccion de codigo 13
datos$lpgrupo[is.na(datos$lpgrupo)] = 0
datos$lpogrupo[is.na(datos$lpogrupo)] = 0
datos$notalp[is.na(datos$notalp)] <- 0  #sustituir NA en nota bloque LP por ceros
datos$notalpo[is.na(datos$notalpo)] <- 0  #sustituir NA en nota LPO por ceros
#modelo
library(caret)
ctrl2 <- trainControl(method = "repeatedcv", number = 10, savePredictions = TRUE, repeats=10)
mod_fit3 <- train(CLASE ~ lpgrupo+lpindividual+repescalp+lpogrupo+lpoindividual+repescalpo,  data=datos, method="glm", family="binomial",trControl = ctrl2, tuneLength = 6, maxit = 100)
matrizconf2=confusionMatrix(mod_fit3)
TP2=matrizconf2$table[1,"APROBADO"] #true positive: casos predichos como positivos y que s? eran positivos
TN2=matrizconf2$table[2,"SUSPENSO"] #true negative: casos predichos como negativos y que s? eran negativos
FP2=matrizconf2$table[2,"APROBADO"] #falso positivo: caso predicho como positivo pero era negativo
FN2=matrizconf2$table[1,"SUSPENSO"] #falso negativo: caso predicho como negativo pero era positivo
precision_modelo2=(TP2/(TP2+FP2))*100