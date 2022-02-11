/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package testpdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.json.JSONException;

/**
 *
 * @author Lenovo
 */
public class TestPDF {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException, ParseException, MalformedURLException, JSONException {
        // TODO code application logic here
        String [] point = readpdf();
        pickpoint(point);
    }
    
 public static String[] readpdf() throws IOException{
        System.out.println("Hello World");
        String strMain = "";
        try{
            
        File pdffile = new File("C:\\Fiscal\\Input\\test.pdf");
        PDDocument document = PDDocument.load(pdffile);
        
        PDFTextStripper pdfStripper = new PDFTextStripper();  
        pdfStripper.setSortByPosition(true);
        
               
        String text = pdfStripper.getText(document);  
        
        strMain = text+"END!! \r\n"+"test.pdf";
        System.out.println(strMain);
        
        document.close();
        
        }
        catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        String[] arrSplit = strMain.split("\n"); 
        return arrSplit;
    }
    
public static String pickpoint(String[] arrSplit) throws IOException,java.text.ParseException, MalformedURLException,java.text.ParseException, JSONException{
        
        String invoice = "";
        String unitprize = "";
        String code = "";
        String invoicetotal = "";
        String filenames="";
        String file = arrSplit[arrSplit.length-1].trim().replace(".pdf","");
        String currency = "nocurrency";
        String customercompany="Company Name";
        String customervat = "123456789";
        String arexists = "notexist";
        String negativevalue = "";
        String customername = "";
        String invoicestatus = "notvalid";
        String taxtotal = "";
        String clientemail = "knyabvure@gmail.com";
        String currencyid = "1";
        String customeraddress = ""; 
        List<String> list = new ArrayList<>();
         
        
        for (int i=0; i<arrSplit.length; i++){
            String sentence = arrSplit[i].replace(":","");
            String[] sentencewords = sentence.split(" ");
            String[] sTarray = Removeemptyvalues(sentencewords);
            String possibledate = sTarray[0];
            int arrlength = sentencewords.length;

            
        if(sentence.contains("Tax Invoice") || sentence.contains("Fiscal Tax Invoice") ){
           invoicestatus = "validinvoice";
           invoice = sentencewords[arrlength-1].trim();
                           File tempFile = new File("C:\\Fiscal\\Receipts\\Processed\\signed"+invoice+".html");
                boolean exists = tempFile.exists();
                File temptwo = new File("C:\\Fiscal\\Receipts\\Processed\\"+invoice+".html");
                boolean existstwo = temptwo.exists();

                if(exists==true || existstwo==true){
                    System.out.println("duplicates twooooooooo");
                    invoice = invoice+"duplicate";
                    break;
                } 
        }

        if(sentence.contains("Client")){
            customername = "";
            for(int j = 2; j < arrlength; j++) {
                if(sentencewords[j].contains("BP")){
                    break;
                }else{
                
                customername = customername + sTarray[j]+" ";
                }
            }             
        }
        if(sentence.contains("VAT Number")){
            customervat = sentencewords[arrlength-1].trim();
        }        

       
         if(sentence.contains("DESCRIPTION")){
                for (int k=i+1; k<arrSplit.length; k++){
                    String lineitem = arrSplit[k].replace("USD", "").replace("ZWL", "");
                    
                    String[] firstArray = lineitem.split(" "); 
                 
                    String[] sArray = Removeemptyvalues(firstArray);
                    int indexz = sArray.length;
                    if(indexz>=4){
                    Boolean lastvalue = TestNum(sArray[indexz-2].trim().replace(",", ""));
                    Boolean secondvalue = TestNum(sArray[indexz-4].trim().replace(",", ""));
                    Boolean thirdvalue = TestNum(sArray[indexz-3].trim().replace(",", ""));
                    
                    System.out.println(lastvalue +"LAST "+sArray[indexz-2]);
                    System.out.println(secondvalue +"FOURTH "+sArray[indexz-4]);
//                    System.out.println(thirdvalue+" "+sArray[indexz-3]);
                    
                    if(lastvalue.equals(true) && secondvalue.equals(true)){
                        unitprize = sArray[indexz-4].trim().replace(",", "");
                        code = sArray[0];
                        String qty =sArray[indexz-3];

                        int descriptionindex = indexz-4;
                            
                        String desc = "";
                            for(int j = 0; j < descriptionindex; j++) {
                              desc = desc + sArray[j]+" ";
                            }  

             
                            String lineitems = "#^"+code+"#"+desc.replace("#", "").replace("!", "")+"#"+unitprize+"#"+qty+"#2#currencyid#"+unitprize+"#\r\n";
                            list.add(lineitems);                          
                            System.out.println(lineitems);
                      
                   }
                  }
                }
            } 
         
            if(sentence.contains("Invoice Total") && invoicetotal.equals("")){
                invoicetotal = sentencewords[sentencewords.length - 2].replace(",","").trim();
                System.out.println(invoicetotal);
                
                String currencycheck=sentencewords[sentencewords.length - 3];
            
                if(currencycheck.contains("ZWL")){
                    currency="ZWL";                
                }
                else{
                    currencyid="2";
                }
            }
            
            
          
            if(sentence.contains("END!!")){
                System.out.println("Vatttttttttttttttttttttttttttttttttttttttttttt");
                
                
                filenames = "C:\\Fiscal\\Receipts\\UnProcessed\\"+invoice.trim()+".prn";
                String[] firstArray = list.toArray(new String[list.size()]);
               
                String filestatus = CreatePrnfile(filenames); 

                if (filestatus.equals("created")){
                    Writetofile("##DLRWF# \r\n", filenames);
                    Writetofile("#*1#1#JentOne#0.00#14.5#0.00#0.00#1#1#1#0#"+currencyid+"# \r\n", filenames);
                    Writetofile("#!"+customervat.trim()+"#"+customername.trim()+"#"+customername.trim()+"#N/A#N/A \r\n", filenames);
                     
                    
                    for(String s : firstArray){
                     
                        String n = s.replace("currencyid", currencyid);
                        if(taxtotal.equals("0.00")){                            
                            System.out.println(n);
                            Writetofile(n,filenames);
                        }
                        else{
                        Writetofile(n,filenames);                            
                        }
                    
                    }
                    Writetofile("#$1#"+invoicetotal.trim()+"#",filenames);  
                    
                    }    
                else{
                    break;
                } 
                
            }
            }
         

  
        if(invoicestatus.equals("notvalid")){
            invoice=file+"notvalid";
        }
          return invoice;           
        }  
  public static String[] Removeemptyvalues( String[] firstArray ) {
    List<String> list = new ArrayList<String>();

    for(String s : firstArray) {
       if(s != null && s.length() > 0) {
          list.add(s);
       }
       
    }

    firstArray = list.toArray(new String[list.size()]);
    return firstArray;
  }
   
        public static Boolean TestNum(String value) throws java.text.ParseException{
            
            String strNew = value; 
            boolean numeric = true;
            
            if (strNew.contains(".")){
            try {
                Double num = Double.parseDouble(strNew);
            } catch (NumberFormatException e) {
                    numeric = false;
                }
            }
            else{
                numeric = false;
               
            }
            return numeric;
                       
    }
public static void Writetofile(String sentence, String pathname){
    try{
        FileWriter fr = new FileWriter(pathname, true);
        fr.write(sentence);                   
        fr.close();
        }   
    catch (IOException e) {
        System.out.println("An error occurred.");
        e.printStackTrace();
        } 
     }
     public static String CreatePrnfile(String filenames){
        String status = "created";
                 try {
                    File myObj = new File(filenames);
                    
                    if (myObj.createNewFile()) {
                      System.out.println("File created: " + myObj.getName());
                    } else {
                      System.out.println("File already exists.");
                      status = "exists";
                    }
                  } catch (IOException e) {
                    System.out.println("An error occurred.");
                    e.printStackTrace();
                  }
                 
                 return status;
                 
    }
        
   
}
