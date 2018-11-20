
Generador de CSV
================================================

Este proyecto está relacionado con la normativa de Administración Electrónica en España.

Generación de Códigos Seguros de Verificación (CSV) de acuerdo a las especificaciones de [Portafirma-CSV](https://administracionelectronica.gob.es/ctt/resources/Soluciones/829/descargas/Analisis%20funcional%20de%20CSV.pdf?idIniciativa=829&idElemento=1966) y la Ley 11/2007

El **Código Seguro de Verificación** es un identificador único dentro de un sistema que nos permite identificar de manera inequívoca documentos firmados. La composición del código requiere el uso de datos no predecibles y dependientes del proceso de firma.

>> Los documentos con el código impreso junto a la información de la firma electrónica, indicando el identificador de la sede electrónica para efectuar el cotejo de documentos, se considerán documentos firmados válidos siempre que podamos obtener el original electrónico. 

Los CSV generados constan de 32 caracteres:

 *  3 caracteres fijos para el prefijo que identifican a la entidad (P)
 *  21 caracteres en Base36 que identifican al HASH del documento (H)
 *  7 caracteres en Base36 que identifican al documento en la organización (I)
 *  1 carácter en Base36 que identifica la configuración de aletoriedad aplicada (R)
  
El orden de los caracteres es fijo para el prefijo `PPP` al inicio de la cadena y para el carácter de aleatoriedad `R` al final de la cadena. En las posiciones intermedias se mezclan los 21 caracteres correspondientes con el hash `H` y los 7 caracteres correspondientes con la identifación del documento `I`.

Ejemplos
 
* `PPPIIIIIIIHHHHHHHHHHHHHHHHHHHHHR` para un vector aleatorio `(0,1,2,3,4,5,6,7)`
* `PPPIHIHIHIHIHIHIHHHHHHHHHHHHHHHR` para un vector aleatorio `(0,2,4,6,8,10,12,14)`

**Licencia**
El código es licenciado bajo [LGPL v3.0](http://www.gnu.org/licenses/lgpl-3.0.html). 

**Estado**
La versión actual es 1.0.0

**Compatibilidad**
Requiere Java 8 o superior


Uso
---

Hay dos métodos públicos en la clase `CSVGenerator`: para generar el código a partir de un fichero o a partir de un hash.

```java
// Generar CSV con prefijo "CSV", identificador de documento 2018 y fichero "muestra.pdf"
File file = new File("c:/temp/muestra.pdf");
String csv = CSVGenerator.getCSV("CSV", 2018l, file);
```

