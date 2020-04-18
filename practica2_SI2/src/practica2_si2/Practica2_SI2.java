/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package practica2_si2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
/**
 *
 * @author carlo
 */
public class Practica2_SI2 {

    static String nombreArchivo = "SistemasInformacionII.xlsx";
    static String rutaArchivo = "D:\\Documentos\\Projects\\NetBeans\\practica2_SI2\\resources\\ficherosExcel\\" + nombreArchivo;
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
		
        ArrayList<Row> listaNIFNIE = new ArrayList<>();
        try (FileInputStream file = new FileInputStream(new File(rutaArchivo))) {                   
            // leer archivo excel
            XSSFWorkbook worbook;
            worbook = new XSSFWorkbook(file);
            //obtener la hoja que se va leer
            XSSFSheet sheet = worbook.getSheetAt(0);
            
            //iTERADOR comprobar dni
            Iterator<Row> rowIteratorCheckDni = sheet.iterator();

            Row rowCheckDni;
            rowCheckDni = rowIteratorCheckDni.next();
            // se recorre cada fila hasta el final
            while (rowIteratorCheckDni.hasNext()) {
                rowCheckDni = rowIteratorCheckDni.next();
                //System.out.println("EMPIEZO WHILE 1");
                
                if(!comprobarVacio(rowCheckDni.getCell(7))){
                    String dni = checkDNI(getCellValue(rowCheckDni.getCell(7)));
                    //System.out.println(dni);
                }
                //System.out.println();
                
            }
            
            //obtener todas las filas de la hoja excel
            Iterator<Row> rowIterator = sheet.iterator();

            Row row;
            row = rowIterator.next();
            // se recorre cada fila hasta el final
            while (rowIterator.hasNext()) {
                row = rowIterator.next();
                //System.out.println("EMPIEZO WHILE 2");
                if(!comprobarVacio(row.getCell(0))){
                    if(comprobarVacio(row.getCell(7))){
                        //System.out.println("BLANCO");
                        listaNIFNIE.add(row);
                    }
                    else if(comprobarRepetido(getCellValue(row.getCell(7)), row.getRowNum())){
                        //System.out.println("REPETIDO");
                        listaNIFNIE.add(row);
                    }
                }
                //System.out.println();  
            }
            
            System.out.println("GENERANDO XML...");
            generarXML(listaNIFNIE);
            
        } catch (Exception e) {
            e.getMessage();
        }     
    }
    
    
    private static Boolean comprobarVacio(Cell cell){
        if(cell == null || cell.toString() == "" || cell.equals(null) || cell.equals(""))
            return true;
        else
            return false;
    }
    
    
    private static Boolean comprobarRepetido(String nifnie, int rowNum) throws FileNotFoundException, IOException{
        try (FileInputStream file = new FileInputStream(new File(rutaArchivo))) { 
            XSSFWorkbook worbook = new XSSFWorkbook(file);
            XSSFSheet sheet = worbook.getSheetAt(0);
            Iterator<Row> rowComparator = sheet.iterator();
            Row row;
            //System.out.println("Comprobamos repetido");
            
            if(rowComparator.hasNext()){
                for(int i=0 ; i<=rowNum ; i++){
                    row = rowComparator.next();
                }
            }
            
            while(rowComparator.hasNext()){
                row = rowComparator.next();
                
                if(comprobarVacio(row.getCell(7)))
                    row = rowComparator.next();
              
                //System.out.println("COMPROBANDO: " + nifnie + " con " + row.getCell(7).toString());

                if((row.getCell(7).toString() == null ? nifnie == null : row.getCell(7).toString().equals(nifnie)) || row.getCell(7).equals(nifnie)){
                    //System.out.println("TRUE");
                    return true;
                }
            }
            
            //System.out.println("FALSE");
            return false;
        }
        
    }
    
    
    
    private static String getCellValue(Cell cell) {
            String val = "";
            
            switch (cell.getCellType()) {
            case NUMERIC:
                val = String.valueOf(cell.getNumericCellValue());
                //val = cell.getCellType().toString();
                break;
            case STRING:
                val = cell.getStringCellValue();
                //val = cell.getCellType().toString();
                break;
            case BLANK:
                val = cell.getCellType().toString();
                break;
            case BOOLEAN:
                val = String.valueOf(cell.getBooleanCellValue());
                //val = cell.getCellType().toString();
                break;
            case ERROR:
                //val = "error";
                val = cell.getCellType().toString();
                break;
            case FORMULA:
                //val = "formula";
                val = cell.getCellType().toString();
                break;
            case _NONE:
                //val = "nulo";
                val = cell.getCellType().toString();
                break;
            default:
                val = "defecto";
                //val = cell.getCellType().toString();
                break;
            }

            return val;
	}
    
    private static String checkDNI(String dni){
        
        int longitudNum, num;
        char[] arrayChar, stringNum;
        char letraDNI, letraCorrectaDNI, primeraLetra;
        
        char[] alfabeto = {'T','R','W','A','G','M','Y','F','P','D','X','B','N','J','Z','S','Q','V','H','L','C','K','E'};

        StringBuilder cadena;
        cadena = new StringBuilder(dni);
        letraDNI = cadena.charAt(cadena.length()-1);
        primeraLetra  = cadena.charAt(0);
        
        if(cadena.charAt(0)=='X' || cadena.charAt(0)=='Y' || cadena.charAt(0)=='Z'){
            if(cadena.charAt(0)=='X'){
                cadena.setCharAt(0, '0');
            }else if(cadena.charAt(0)=='Y'){
                cadena.setCharAt(0, '1');
            }else{
                cadena.setCharAt(0, '2');
            }
            //System.out.println(cadena);
        }
        
        longitudNum=8;
        int numDNI;
        numDNI = 0;
        for(int i=0; i<longitudNum; i++){
            int charAInt;
            charAInt = Character.getNumericValue(cadena.charAt(i));
            int aux2 = 7 - i;
            aux2= (int) Math.pow(10, aux2);
            charAInt = charAInt * aux2;
            numDNI += charAInt;
        }
        num = numDNI%23;
        letraCorrectaDNI = alfabeto[num];
        if(alfabeto[num]==letraDNI){
            return dni;
        }else{
            cadena.deleteCharAt(cadena.length()-1);
            cadena.append(letraCorrectaDNI);
            cadena.setCharAt(0, primeraLetra);
            return cadena.toString();
        }
    }

    private static void generarXML(ArrayList<Row> listaNIFNIE) throws ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException{
        String name = "Errores";
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(Practica2_SI2.class.getName()).log(Level.SEVERE, null, ex);
        }
        DOMImplementation implementation = builder.getDOMImplementation();
        Document document = implementation.createDocument(null, name, null);
        document.setXmlVersion("1.0");

        //Main Node
        Element raiz = document.getDocumentElement();
        Element subraiz = document.createElement("Trabajadores");
        //Por cada key creamos un item que contendrá la key y el value
        for(int i=0; i<listaNIFNIE.size();i++){
            //Item Node
            Element itemNode = document.createElement("Trabajador"); 
            
            //NumeroFila Node
            Element numFilaNode = document.createElement("NumeroFila"); 
            Text numFilaValue = document.createTextNode(listaNIFNIE.get(i).getRowNum() + 1 + "");
            numFilaNode.appendChild(numFilaValue);    
            
            //Nombre Node
            Element nombreNode = null;
            if(!comprobarVacio(listaNIFNIE.get(i).getCell(4))){
                nombreNode = document.createElement("Nombre");
                Text nodeNombreValue = document.createTextNode(listaNIFNIE.get(i).getCell(4).getStringCellValue());   
                nombreNode.appendChild(nodeNombreValue);
            }
            //Apellidos Node
            String apellidos = "";
            Element apellidosNode = null;
            if(!comprobarVacio(listaNIFNIE.get(i).getCell(5))){
                apellidosNode = document.createElement("Apellidos"); 
                apellidos = listaNIFNIE.get(i).getCell(5).getStringCellValue();
            }
            if(!comprobarVacio(listaNIFNIE.get(i).getCell(6))){
                apellidos = apellidos + " " + listaNIFNIE.get(i).getCell(6).getStringCellValue();
            }
            Text nodeApellidosValue = document.createTextNode(apellidos);                
            apellidosNode.appendChild(nodeApellidosValue);  
            
            //Empresa Node
            Element empresaNode = null;
            if(!comprobarVacio(listaNIFNIE.get(i).getCell(1))){
                empresaNode = document.createElement("Empresa"); 
                Text nodeEmpresaValue = document.createTextNode(listaNIFNIE.get(i).getCell(1).getStringCellValue());                
                empresaNode.appendChild(nodeEmpresaValue);
            }
            
            //Categoria Node
            Element categoriaNode = null;
            if(!comprobarVacio(listaNIFNIE.get(i).getCell(4))){
                categoriaNode = document.createElement("Categoria"); 
                Text nodeCategoriaValue = document.createTextNode(listaNIFNIE.get(i).getCell(2).getStringCellValue());             
                categoriaNode.appendChild(nodeCategoriaValue);
            }
            
            //append keyNode and valueNode to itemNode
            itemNode.appendChild(numFilaNode);
            itemNode.appendChild(nombreNode);
            itemNode.appendChild(apellidosNode);
            itemNode.appendChild(empresaNode);
            itemNode.appendChild(categoriaNode);
            //append itemNode to raiz
            subraiz.appendChild(itemNode); //pegamos el elemento a la raiz "Trabajador"
        }
            raiz.appendChild(subraiz);
            //Generate XML
            Source source = new DOMSource(document);
            //Indicamos donde lo queremos almacenar
            Result result = new StreamResult(new java.io.File(name + ".xml")); //nombre del archivo
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(source, result);
            copiarArchivo();
    }
    
    
    private static void copiarArchivo() throws IOException{
        Path from= Paths.get("D:\\Documentos\\Projects\\NetBeans\\practica2_SI2\\Errores.xml");
        Path to = Paths.get("D:\\Documentos\\Projects\\NetBeans\\practica2_SI2\\resources\\Errores.xml");

        //Reemplazamos el fichero si ya existe
        CopyOption[] options = new CopyOption[]{
          StandardCopyOption.REPLACE_EXISTING,
          StandardCopyOption.COPY_ATTRIBUTES
        };
        Files.copy(from, to, options);
        Files.deleteIfExists(from);
    }
    
   
    
}
