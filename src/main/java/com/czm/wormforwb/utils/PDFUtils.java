package com.czm.wormforwb.utils;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;

import java.io.FileNotFoundException;

/**
 * PDF工具类
 * @author Slience
 * @date 2022/3/11 16:34
 **/
public class PDFUtils {

    public static Document createNewPDF(String location) throws FileNotFoundException {
        String dest = "C:/itextExamples/sample.pdf";
        PdfWriter writer = new PdfWriter(dest);

        // 2、Creating a PdfDocument
        PdfDocument pdfDoc = new PdfDocument(writer);
        // 3、Adding an empty page
        pdfDoc.addNewPage();
        // 4、Creating a Document
        return new Document(pdfDoc);
    }


}
