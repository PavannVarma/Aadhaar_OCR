import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

  
public class ImageToTextConvertor { 
    public static void main(String[] args) 
    { 
        Tesseract tesseract = new Tesseract(); 
        try { 
  
            tesseract.setDatapath("E:\\Varma\\Documents\\OCR VSK-20240620T095831Z-001\\OCR VSK\\AadharOcr\\tessdata");  
  
            // the path of your tess data folder 
            // inside the extracted file 
            String text
                = tesseract.doOCR(new File("E:\\Varma\\Documents\\OCR VSK-20240620T095831Z-001\\OCR VSK\\AadharOcr\\src\\Aadhar .jpg")); 
  
            // path of your image file 
            //System.out.print(text+" ");
            
            BufferedReader br = new BufferedReader(new StringReader(text));
            
            String line;
            while((line = br.readLine()) != null) {
            	//System.out.println(line+" ###");
            	String val = line.toString().replaceAll("[^a-zA-Z0-9\\/ ]", "");
            	val = val.trim();
            	if(val.length() > 2) {
            		setMetaData(val.trim());
            	}
            }
            
        } 
        catch (TesseractException | IOException e) { 
            e.printStackTrace(); 
        } 
    } 

    public static void setMetaData(String val){
    		List filterKeywords = Arrays.asList("GOVERNMENT", "INDIA");
    		
            String aadharRegex = "^[2-9]{1}[0-9]{3}\\s[0-9]{4}\\s[0-9]{4}$";
            String nameRegex = "^[a-zA-Z\\s]*$";

            Matcher aadharMatcher = getPatternMatcher(aadharRegex, val);
            Matcher nameMatcher = getPatternMatcher(nameRegex, val);

            String metaData = "OTHER";
            String srcVal = val.toUpperCase();
            String tgtVal = val;

            if (nameMatcher.matches() && !srcVal.contains("GOVERNMENT") && !srcVal.contains("INDIA")) {
            if (srcVal.contains("MALE") || srcVal.contains("FEMALE") || srcVal.contains("TRANS") || srcVal.contains("Male")) {
                metaData = "GENDER";
                if (val.contains("/")) {
                   // tgtVal = val.split("/")[1];
                	String[] tgtValArr = val.split("/");
                	if(tgtValArr.length > 1) {
                		tgtVal = tgtValArr[1];
                	}
                } else {
                    if (val.contains(" ")) {
                        tgtVal = val.split(" ")[1];
                    }
                }

            } else if (srcVal.contains("YEAR") || srcVal.contains("BIRTH") || srcVal.contains("DATE") || srcVal.contains("DOB") ||
                    srcVal.contains("YEAR OF") || srcVal.contains("YOB")) {
                metaData = "DATE_OF_YEAR";

                if (val.contains(":")) {
                    tgtVal = val.split(":")[1];
                } else {
                    String dobValArr[] = val.split(" ");
                    int dobValLen = dobValArr.length;
                    tgtVal = dobValArr[dobValLen - 1];
                }

                tgtVal = getFormatedDate(tgtVal);

            } else if (aadharMatcher.matches()) {
                metaData = "AADHAR";
            } else if (nameMatcher.matches() && !srcVal.contains("GOVERNMENT") && !srcVal.contains("INDIA") && !srcVal.contains("FATHER")) {
                metaData = "NAME";


            }
            
            }
        //    metadataMap.put(metaData, tgtVal.trim());
            
            System.out.println("Metadata : "+ metaData + " Target Val: "+ tgtVal);
        
    }
    private static String getFormatedDate(String datevalue)  {
        
            datevalue = (datevalue != null && !datevalue.isEmpty()) ? datevalue.trim() : "";

            if (datevalue.matches("\\d{4}")) {
                //This block will execute when we have only year in the aadhaar card
                return "01-01-" + datevalue;
            } else {
//                return DateUtils.aAdhaarDateFormated(datevalue);
            	return datevalue;
            }
        } 
    

    private static Matcher getPatternMatcher(String regex, String value) {
        Pattern pattern = Pattern.compile(regex);
        Matcher patternMatcher = pattern.matcher(value);

        return patternMatcher;
    }
}